package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.BookSeatRequest;
import com.guanhong.airlinebookingsystem.model.FlightRequest;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.TicketRepository;
import com.guanhong.airlinebookingsystem.repository.UnavailableSeatInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TicketService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UnavailableSeatInfoRepository unavailableSeatInfoRepository;

    @Transactional(rollbackFor = Exception.class)
    public Ticket bookFlight(FlightRequest flight, long customerId) throws Exception {
        Flight returnedFlight = flightRepository.findFlightByFlightNumberAndFlightDate(flight.getFlightNumber(), flight.getFlightDate());
        log.error("Lock the flight row");
        if (validFlightIsAvailable(returnedFlight) == false) {
            // A valid flight ticket id should greater than 0.
            // Therefore, ticketId = 0 means the flight is full.
            int fullFlightTicketId = 0;
            log.info("Customer " + customerId + " failed to book the ticket because the flight is full.");
            return new Ticket(fullFlightTicketId);
        } else {
            Ticket newTicket = new Ticket(customerId, returnedFlight.getFlightId(), returnedFlight.getFlightDate());
            if (checkIsDuplicatedBooking(newTicket) == true) {
                log.error("The customer (" + customerId + ") already book the ticket for flight " + newTicket.getFlightId());
                throw new ClientException("Customer already booked the ticket in the same flight", HttpStatus.BAD_REQUEST);
            }
            returnedFlight.setAvailableTickets(returnedFlight.getAvailableTickets() - 1);
            Flight newFlight = flightRepository.saveAndFlush(returnedFlight);
            if (newFlight == null) {
                log.error("Update flight's available seats error. (flight Id: " +
                        returnedFlight.getFlightId() + "). Rollback all transactions.");
                throw new ServerException("Unknown Server Exception.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // TODO: Add flight number into ticket table
            Ticket returnedTicket = ticketRepository.save(newTicket);
            if (returnedTicket != null) {
                log.info("Customer Id: " + customerId + " successfully booked the ticket in flight " + returnedFlight.getFlightId());
                return returnedTicket;
            } else {
                log.error("Save new ticket into database error. (Customer Id: " + customerId + ", flight Id: " +
                        returnedFlight.getFlightId() + "). Rollback all transactions.");
                throw new ServerException("Unknown Server Exception.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Ticket bookSeat(BookSeatRequest bookSeatRequest, long customerId) throws Exception {
        Flight flight = flightRepository.findFlightByFlightNumberAndFlightDate(bookSeatRequest.getFlightNumber(),
                bookSeatRequest.getFlightDate());
        // Valid if the customer book the ticket
        Ticket originalTicket = validTicket(flight.getFlightId(), customerId);
        // Valid if the seat is available, if it is available, create the new entity in DB
        validSeatStatus(flight.getFlightId(), bookSeatRequest.getSeatNumber(), bookSeatRequest.getFlightNumber());
        // Update ticket information in db
        Ticket newTicket = bookSeatForTicket(originalTicket, bookSeatRequest.getSeatNumber());
        log.info("Customer " + newTicket.getCustomerId() + " booked the seat " + newTicket.getSeatNumber() +
                " in the flight " + newTicket.getFlightId());
        return newTicket;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTicket(FlightRequest flightRequest, long customerId) throws Exception {
        Flight flight = flightRepository.findFlightByFlightNumberAndFlightDate(flightRequest.getFlightNumber(),
                flightRequest.getFlightDate());
        if (flight == null) {
            log.info("Customer " + customerId + " try to cancel an non-existent flight " + flightRequest.getFlightNumber() +
                    " on " + flightRequest.getFlightDate() + ".");
            throw new ClientException("The flight does not exist in the system.");
        }
        Ticket ticket = ticketRepository.findTicketByCustomerIdAndFlightId(customerId, flight.getFlightId());
        if (ticket == null) {
            log.info("Customer " + customerId + " does not book the ticket in flight " + flight.getFlightId());
            throw new ClientException("You do not book this flight.");

        }
        // Check if customer book the seat
        if (ticket.getSeatNumber() != null){
            int seatNumber = ticket.getSeatNumber();
            // Clean the seat record
            ticketRepository.delete(ticket);
            int deleteResult = unavailableSeatInfoRepository.deleteUnavailableSeatInfoByFlightIdAndSeatNumber(flight.getFlightId(),
                    seatNumber);
            if (deleteResult != 1){
                log.error("Delete seat reservation from unavailable seat info table failed.");
                throw new ServerException("Failed delete the seat reservation.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            ticketRepository.delete(ticket);
        }
        return true;
    }

    @Transactional(rollbackFor =  Exception.class)
    public List<Ticket> getAllTicketByCustomerId(long customerId) throws Exception {
        return null;
    }

    private boolean validFlightIsAvailable(Flight flight) throws Exception {
        // Check if flight is in the system
        if (flight == null) {
            throw new ClientException("Selected Flight is not exist in the system. Please check the flight number and flight date again.",
                    HttpStatus.BAD_REQUEST);
        }
        // Check if the flight has available seats
        else if (flight.getAvailableTickets() <= 0) {
            return false;
        }
        return true;
    }

    private boolean checkIsDuplicatedBooking(Ticket newTicket) {
        Ticket returnedTicket = ticketRepository.findTicketByCustomerIdAndFlightId(newTicket.getCustomerId(),
                newTicket.getFlightId());
        if (returnedTicket == null) {
            return false;
        } else {
            return true;
        }
    }

    private Ticket validTicket(long flightId, long customerId) throws ClientException {
        Ticket ticket = ticketRepository.findTicketByCustomerIdAndFlightId(customerId, flightId);
        if (ticket == null) {
            log.info("Customer " + customerId + " does not book the ticket in flight " + flightId);
            throw new ClientException("You have to book the ticket before booking a seat.", HttpStatus.BAD_REQUEST);
        } else {
            return ticket;
        }
    }

    private synchronized boolean validSeatStatus(long flightId, int seatNumber, long flightNumber) throws Exception {
        UnavailableSeatInfo seatInfo = unavailableSeatInfoRepository.findUnavailableSeatInfoByFlightIdAndSeatNumber(flightId, seatNumber);
        if (seatInfo == null) {
            //If the seat is available, reserve the seat in unavailable seat info table
            UnavailableSeatInfo seatReservation = new UnavailableSeatInfo(flightId, seatNumber, SeatStatus.BOOKED);
            UnavailableSeatInfo returnedSeatReservation = unavailableSeatInfoRepository.save(seatReservation);
            if (returnedSeatReservation != null) {
                return true;
            }
            log.error("Unavailable to create seat reservation in Unavailable Seat Info. Release the lock.");
            throw new ServerException("Unavailable to book the seat.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("The seat " + seatNumber + " in the flight " + flightId + " is not available.");
        throw new ClientException("The seat " + seatNumber + " in the flight " + flightNumber + " is not available.",
                HttpStatus.BAD_REQUEST);
    }

    private Ticket bookSeatForTicket(Ticket ticket, int seatNumber) throws Exception {
        // Check if the ticket already have a seat

        // If the ticket's seatNumber is null, book the new seat
        if (ticket.getSeatNumber() != null) {
            // Delete the current seat reservation in unavailable seat info table
            int deleteResult = unavailableSeatInfoRepository.deleteUnavailableSeatInfoByFlightIdAndSeatNumber(ticket.getFlightId(),
                    ticket.getSeatNumber());
            if (deleteResult != 1) {
                log.error("Unavailable to delete the current seat reservation in unavailable_seat_info table. " +
                        "Rollback all transactions.");
                throw new ServerException("Unavailable to book the seat.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        ticket.setSeatNumber(seatNumber);
        Ticket returnedTicket = ticketRepository.save(ticket);
        if (returnedTicket != null) {
            return returnedTicket;
        }
        log.error("Unavailable to update seat number in the ticket table. Rollback all transaction");
        throw new ServerException("Unavailable to book the seat.", HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
