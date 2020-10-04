package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.DateHelper;
import com.guanhong.airlinebookingsystem.model.AricraftConstant;
import com.guanhong.airlinebookingsystem.model.FlightNumberRequest;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRouteRepository;
import com.guanhong.airlinebookingsystem.repository.TicketRepository;
import com.guanhong.airlinebookingsystem.repository.UnavailableSeatInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FlightService {

    @Autowired
    private FlightRouteRepository flightRouteRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UnavailableSeatInfoRepository unavailableSeatInfoRepository;

    @Autowired
    private TicketRepository ticketRepository;


    @Autowired
    private BatchService batchService;


    @Transactional(rollbackFor = Exception.class)
    public FlightRoute createNewFlight(FlightRoute flightRoute) throws Exception {

        validNewFlightInfo(flightRoute);

        FlightRoute returnedFlightRoute = flightRouteRepository.save(flightRoute);
        log.info("Create flight " + returnedFlightRoute.getFlightNumber() + " in the system");
        createFlightByFlightRoute(returnedFlightRoute);
        return returnedFlightRoute;
    }

    @Transactional(rollbackFor = Exception.class)
    public FlightRoute updateFlight(FlightRoute newFlightRoute) throws Exception {
        validFlightInfoWOFlightNumber(newFlightRoute);
        FlightRoute existFlightRoute = flightRouteRepository.findById(newFlightRoute.getFlightNumber()).get();
        if (existFlightRoute != null) {
            // Get original capacity and new capacity
            // Todo: When aircraft is small, it should update unavailable_Seat_info and ticket table
            cancelSeatReservationWhenUpdateAircraft(existFlightRoute, newFlightRoute);
            updateAvailableTickets(existFlightRoute, newFlightRoute);
            if (updateFlightDateRange(existFlightRoute, newFlightRoute)) {
                FlightRoute returnedFlightRoute = flightRouteRepository.save(newFlightRoute);
                return returnedFlightRoute;
            } else {
                log.error("Update flights in flight table of flight seat info table failed");
                throw new ServerException("Failed to update flight " + newFlightRoute.getFlightNumber(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            log.warn("Admin user tries to update a non-existent flight: " + newFlightRoute.getFlightNumber());
            throw new ClientException("The flight " + newFlightRoute.getFlightNumber() + " does not existed in the system.",
                    HttpStatus.BAD_REQUEST);
        }
    }


    public List<FlightRoute> getAllAvailableFlightRoutes() throws Exception {
        List<FlightRoute> flightRoutes = flightRouteRepository.findAllByEndDateAfter(new DateHelper().today());
        return flightRoutes;
    }

    public List<Flight> getAllAvailableFlightsByFlightNumber(long flightNumber) throws Exception {
        int availableSeat = 0;
        List<Flight> flights = flightRepository.findAllByFlightNumberAndAvailableTicketsGreaterThanAndFlightDateGreaterThanEqualOrderByFlightDate(flightNumber, availableSeat, new DateHelper().today());
        return flights;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelFlightRoute(long flightNumber) throws Exception{
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(flightNumber);
        if (flightRoute != null){
            flightRouteRepository.delete(flightRoute);
            return true;
        }
        else {
            log.error("Deleting flight route" + flightNumber + " is failed.");
            throw new ClientException("The flight route is unavailable in the system.", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelFlight(long flightNumber, Date flightDate) throws Exception{
        return false;
    }

    private boolean validNewFlightInfo(FlightRoute flightRoute) throws Exception {
        //  1. Valid all flight information without flight number
        validFlightInfoWOFlightNumber(flightRoute);

        // 2. valid if the flight number is valid and exist in the system
        if (flightRoute.getFlightNumber() > 9999 || flightRoute.getFlightNumber() <= 0) {
            throw new ClientException("The flight number should not excess 4 digits.");
        } else if (flightRouteRepository.findFlightByflightNumber(flightRoute.getFlightNumber()) != null) {
            throw new ClientException("The flight number already be used.");
        }
        return true;
    }


    private BigDecimal roundOverbookingAllowance(BigDecimal overbooking) {
        return overbooking.setScale(2, RoundingMode.FLOOR);
    }

    private boolean validFlightInfoWOFlightNumber(FlightRoute flightRoute) throws ClientException {
        // 1. Verify if capacity is valid
        if (flightRoute.getAircraftId() == null) {
            throw new ClientException("Flight's aircraft cannot be empty.");
        } else {
            if (!AricraftConstant.validAircraftID(flightRoute.getAircraftId())) {
                throw new ClientException("Flight's aircraft is invalid.");
            }
        }

        // 2. Verify if the overbook allowance is valid
        if (flightRoute.getOverbooking() == null) {
            throw new ClientException("Flight's overbook allowance cannot be empty.");
        } else {
            flightRoute.setOverbooking(roundOverbookingAllowance(flightRoute.getOverbooking()));
            if (flightRoute.getOverbooking().compareTo(BigDecimal.valueOf(0)) == -1 ||
                    flightRoute.getOverbooking().compareTo(BigDecimal.valueOf(10)) == 1) {
                throw new ClientException("Flight's overbooking allowance should between 0% to 10%");
            }
        }

        // 3. Verify if the range of travel date is valid
        if (flightRoute.getStartDate() == null || flightRoute.getEndDate() == null) {
            throw new ClientException("Flight's range of travel date cannot be empty.");
        } else {
            // Check if end date is before than start date
            Date today = new DateHelper().today();
            if (flightRoute.getEndDate().before(flightRoute.getStartDate())) {
                throw new ClientException("The end of travel range should not before the start of travel range.");
            } else if (!today.equals(flightRoute.getStartDate()) && !today.before(flightRoute.getStartDate())) {
                throw new ClientException("The start of travel range should not before today.");
            }
        }


        // 4. Verify departure city is not null
        if (flightRoute.getDepartureCity() == null) {
            throw new ClientException("The departure city should not be empty.");
        } else {
            if (flightRoute.getDepartureCity().length() > 255) {
                throw new ClientException("The length of departure city cannot excess than 255.");
            }
        }

        // 5. Verify if destination city is null
        if (flightRoute.getDestinationCity() == null) {
            throw new ClientException("The destination city should not be empty.");
        } else {
            if (flightRoute.getDestinationCity().length() > 255) {
                throw new ClientException("The length of destination city cannot excess than 255.");
            }
        }

        // 6. Verify if departure time is null
        if (flightRoute.getDepartureTime() == null) {
            throw new ClientException("The departure time should not be empty.");
        }

        // 7. Verify if arrival time is null
        if (flightRoute.getArrivalTime() == null) {
            throw new ClientException("The arrival time should not be empty.");
        }
        return true;
    }

    private boolean createFlightByFlightRoute(FlightRoute newFlightRoute) throws Exception {
        DateHelper dateHelper = new DateHelper();
        Date currentDate = newFlightRoute.getStartDate();

        int capacity = AricraftConstant.getCapacityByAircraft(newFlightRoute.getAircraftId());
        int availableTickets = newFlightRoute.calculateAvailableTickets(capacity);
        List<Flight> newFlights = new ArrayList<>();
        Flight flight;

        while (!currentDate.after(newFlightRoute.getEndDate())) {
            flight = new Flight(newFlightRoute.getFlightNumber(), currentDate, availableTickets);
            newFlights.add(flight);
            currentDate = dateHelper.datePlusSomeDays(currentDate, 1);
        }
        List<Flight> returnedFlights = flightRepository.saveAll(newFlights);
        if (returnedFlights != null) {
            log.info("Created flights of flightRoute " + newFlightRoute.getFlightNumber() + " in the system");
            return true;
        }
        return false;
    }

    private boolean updateFlightDateRange(FlightRoute originalFlightRoute, FlightRoute newFlightRoute) {
        if (originalFlightRoute.getStartDate().equals(newFlightRoute.getStartDate()) &&
                originalFlightRoute.getEndDate().equals(newFlightRoute.getEndDate())) {
            return true;
        }
        // If the travel date range is different
        else {
            DateHelper dateHelper = new DateHelper();
            // Check start date first
            List<Flight> deleteFlights = new ArrayList<>();
            List<Flight> addFlights = new ArrayList<>();
            List<Flight> flights = new ArrayList<>();
            int newCapacity = AricraftConstant.getCapacityByAircraft(newFlightRoute.getAircraftId());
            if (originalFlightRoute.getStartDate().before(newFlightRoute.getStartDate())) {
                flights = flightRepository.findAllByFlightNumberAndFlightDateBetweenOrderByFlightDate(newFlightRoute.getFlightNumber(),
                        originalFlightRoute.getStartDate(), newFlightRoute.getStartDate());
                // Delete the last element from flights
                flights.remove(flights.get(flights.size() - 1));
                deleteFlights.addAll(flights);
            }
            if (originalFlightRoute.getStartDate().after(newFlightRoute.getStartDate())) {
                Date currentDate = newFlightRoute.getStartDate();
                Flight newFlight;
                while (currentDate.before(originalFlightRoute.getStartDate())) {

                    int availableTickets = newFlightRoute.calculateAvailableTickets(newCapacity);
                    newFlight = new Flight(newFlightRoute.getFlightNumber(), currentDate, availableTickets);
                    addFlights.add(newFlight);
                    currentDate = dateHelper.datePlusSomeDays(currentDate, 1);
                }
            }
            if (originalFlightRoute.getEndDate().after(newFlightRoute.getEndDate())) {
                flights = flightRepository.findAllByFlightNumberAndFlightDateBetweenOrderByFlightDate(newFlightRoute.getFlightNumber(),
                        newFlightRoute.getEndDate(), originalFlightRoute.getEndDate());
                // Delete the first element from flights
                flights.remove(flights.get(0));
                deleteFlights.addAll(flights);
            }
            if (originalFlightRoute.getEndDate().before(newFlightRoute.getEndDate())) {
                Date currentDate = dateHelper.datePlusSomeDays(originalFlightRoute.getEndDate(), 1);
                Flight newFlight;
                while (!currentDate.after(newFlightRoute.getEndDate())) {
                    int availableTickets = newFlightRoute.calculateAvailableTickets(newCapacity);
                    newFlight = new Flight(newFlightRoute.getFlightNumber(), currentDate, availableTickets);
                    addFlights.add(newFlight);
                    currentDate = dateHelper.datePlusSomeDays(currentDate, 1);
                }
            }
            if (deleteFlights.size() > 0) {
                flightRepository.deleteAll(deleteFlights);
            }
            if (addFlights.size() > 0) {
                flightRepository.saveAll(addFlights);
            }
            return true;
        }
    }

    private boolean updateAvailableTickets(FlightRoute originalFlightRoute, FlightRoute newFlightRoute) throws Exception {
        int originalCapacity = AricraftConstant.getCapacityByAircraft(originalFlightRoute.getAircraftId());
        int newCapacity = AricraftConstant.getCapacityByAircraft(newFlightRoute.getAircraftId());
        int origianlInitAvailableTickets = originalFlightRoute.calculateAvailableTickets(originalCapacity);
        int newInitAvailableTickets = newFlightRoute.calculateAvailableTickets(newCapacity);

        if (origianlInitAvailableTickets == newInitAvailableTickets) {
            return true;
        }


        Date startRange;
        Date endRange;

        // Get startRange and end Range
        if (originalFlightRoute.getStartDate().before(newFlightRoute.getStartDate())) {
            startRange = newFlightRoute.getStartDate();
        } else {
            startRange = originalFlightRoute.getStartDate();
        }
        if (originalFlightRoute.getEndDate().after(newFlightRoute.getEndDate())) {
            endRange = newFlightRoute.getEndDate();
        } else {
            endRange = originalFlightRoute.getEndDate();
        }

        // Get all flight in the range
        List<Flight> updateFlights = flightRepository.findAllByFlightNumberAndFlightDateBetweenOrderByFlightDate(
                newFlightRoute.getFlightNumber(), startRange, endRange);
        int diffAvailableSeat = Math.abs(newInitAvailableTickets - origianlInitAvailableTickets);
        if (newInitAvailableTickets > origianlInitAvailableTickets) {
            for (int i = 0; i < updateFlights.size(); i++) {
                updateFlights.get(i).addAvailableTickets(diffAvailableSeat);
            }
            batchService.batchUpdate(updateFlights);
            return true;
        } else {
            List<Flight> unavilableFlights = new ArrayList<>();
            for (int i = 0; i < updateFlights.size(); i++) {
                if (!updateFlights.get(i).subtractAvailableTickets(diffAvailableSeat)) {
                    unavilableFlights.add(updateFlights.get(i));
                }
            }
            if (unavilableFlights.size() > 0) {
                String exceptionStr = "The flight " + unavilableFlights.get(0).getFlightNumber() + " on these date: ";
                for (int i = 0; i < unavilableFlights.size(); i++) {
                    log.error("Flight " + unavilableFlights.get(i).getFlightId() + "'s available seats is not enough" +
                            "for updating flights.");
                    exceptionStr += unavilableFlights.get(i).getFlightDate().toString();
                    if (i+1 != unavilableFlights.size()){
                        exceptionStr += ", ";
                    }
                }
                exceptionStr += " are not available for updating because the remaining available seats are not enough";
                throw new ClientException(exceptionStr, HttpStatus.BAD_REQUEST);
            }
            batchService.batchUpdate(updateFlights);
            return true;
        }

    }

    private boolean cancelSeatReservationWhenUpdateAircraft(FlightRoute originalFlightRoute, FlightRoute newFlightRoute){
        // Check if aircraft is changed
        if (originalFlightRoute.getAircraftId().equals(newFlightRoute.getAircraftId())){
            return true;
        }
        else {
            // Get all seat reservations
            List<Flight> flights = flightRepository.findAllByFlightNumberOrderByFlightDate(newFlightRoute.getFlightNumber());
            List<UnavailableSeatInfo> seatReservations = new ArrayList<>();
            List<Ticket> updateTickets = new ArrayList<>();
            for (int i = 0; i < flights.size(); i++){
                seatReservations.addAll(unavailableSeatInfoRepository.findAllByFlightId(flights.get(i).getFlightId()));
                updateTickets.addAll(ticketRepository.findTicketsByFlightId(flights.get(i).getFlightId()));
//                Integer test = unavailableSeatInfoRepository.deleteAllByFlightId(flights.get(i).getFlightId());
//                System.out.println(test);
            }
            // Update ticket's seat as null
            for (int i = 0; i < updateTickets.size(); i++){
                updateTickets.get(i).setSeatNumber(null);
            }
            if (updateTickets.size() > 0){
                batchService.batchUpdate(updateTickets);
            }
            if (seatReservations.size() > 0){
                unavailableSeatInfoRepository.deleteAll(seatReservations);
            }

            return true;
        }


    }

}
