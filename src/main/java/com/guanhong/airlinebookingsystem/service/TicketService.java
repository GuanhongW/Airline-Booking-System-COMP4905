package com.guanhong.airlinebookingsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.Ticket;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TicketService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Transactional(rollbackFor=Exception.class)
    public Ticket bookFlight(Flight flight, long customerId) throws Exception {
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
            if (checkIsDuplicatedBooking(newTicket) == true){
                log.error("The customer (" + customerId + ") already book the ticket for flight " + newTicket.getFlightId());
                throw new ClientException("Customer already booked the ticket in the same flight", HttpStatus.BAD_REQUEST);
            }
            returnedFlight.setAvailableSeats(returnedFlight.getAvailableSeats() - 1);
            Flight newFlight = flightRepository.saveAndFlush(returnedFlight);
            if (newFlight == null) {
                log.error("Update flight's available seats error. (flight Id: " +
                        returnedFlight.getFlightId() + "). Rollback all transactions.");
                throw new ServerException("Unknown Server Exception.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
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

    private boolean validFlightIsAvailable(Flight flight) throws Exception {

        // Check if flight is in the system
        if (flight == null) {
            throw new ClientException("Selected Flight is not exist in the system. Please check the flight number and flight date again.",
                    HttpStatus.BAD_REQUEST);
        }
        // Check if the flight has available seats
        else if (flight.getAvailableSeats() <= 0) {
            return false;
        }
        return true;
    }

    private boolean checkIsDuplicatedBooking(Ticket newTicket) {
        Ticket returnedTicket = ticketRepository.findTicketByCustomerIdAndFlightIdAndFlightDate(newTicket.getCustomerId(),
                newTicket.getFlightId(), newTicket.getFlightDate());
        if (returnedTicket == null){
            return false;
        }
        else {
            return true;
        }
    }
}
