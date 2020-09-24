package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.FlightRoute;
import com.guanhong.airlinebookingsystem.entity.FlightSeatInfo;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRouteRepository;
import com.guanhong.airlinebookingsystem.repository.FlightSeatInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class GetAvailableFlightRoutesTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRouteRepository flightRouteRepository;

    @Autowired
    private FlightSeatInfoRepository flightSeatInfoRepository;

    @Autowired
    private FlightRepository flightRepository;

    private static Constants constants = Constants.getInstance();

    @Test
    @Transactional
    void getAllAvailableFlightRoutes_Success() throws Exception {
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int aircraft = 200;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        Date startDate = constants.datePlusSomeDays(constants.today(), 5);
        Date endDate = constants.datePlusSomeDays(constants.today(), 35);


        // Create a flight the end date is before
        flightNumber = constants.FLIGHT_NUMBER_EXPIRED;

        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        FlightRoute returnedFlightRoute = assertDoesNotThrow(() -> flightService.createNewFlight(newFlightRoute1));
        newFlightRoute1.setStartDate(constants.datePlusSomeDays(constants.today(), -100));
        newFlightRoute1.setEndDate(constants.datePlusSomeDays(constants.today(), 0));
        assertDoesNotThrow(() -> flightRouteRepository.save(newFlightRoute1));
        validFlightInfo(newFlightRoute1, flightNumber, 156, true);

        // Create a flight router both start date and end date are after today
        flightNumber = constants.FLIGHT_NUMBER_START_DATE_EXPIRED;

        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        returnedFlightRoute = assertDoesNotThrow(() -> flightService.createNewFlight(newFlightRoute2));
        newFlightRoute2.setStartDate(constants.datePlusSomeDays(constants.today(), -5));
        newFlightRoute2.setEndDate(constants.datePlusSomeDays(constants.today(), 8));
        assertDoesNotThrow(() -> flightRouteRepository.save(newFlightRoute2));
        validFlightInfo(newFlightRoute2, flightNumber, 156, true);

        // Create a flight router both start date and end date are after today
        flightNumber = constants.FLIGHT_NUMBER_AVAILABLE;

        FlightRoute newFlightRoute3 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        returnedFlightRoute = assertDoesNotThrow(() -> flightService.createNewFlight(newFlightRoute3));
        newFlightRoute3.setStartDate(constants.datePlusSomeDays(constants.today(), 0));
        newFlightRoute3.setEndDate(constants.datePlusSomeDays(constants.today(), 8));
        assertDoesNotThrow(() -> flightRouteRepository.save(newFlightRoute3));
        validFlightInfo(newFlightRoute3, flightNumber, 156, true);

        assertFalse(validFlightExistInList(flightService.getAllAvailableFlightRoutes(), constants.FLIGHT_NUMBER_EXPIRED));
        assertTrue(validFlightExistInList(flightService.getAllAvailableFlightRoutes(), constants.FLIGHT_NUMBER_START_DATE_EXPIRED));
        assertTrue(validFlightExistInList(flightService.getAllAvailableFlightRoutes(), constants.FLIGHT_NUMBER_AVAILABLE));
    }


    private void validFlightInfo(FlightRoute expectedFlightRoute, long actualFlightNumber, int availableTicket,
                                 boolean isSkipSeatList) {
        assertEquals(expectedFlightRoute.getFlightNumber(), actualFlightNumber);
        FlightRoute returnedFlightRoute = flightRouteRepository.findFlightByflightNumber(actualFlightNumber);
        assertNotNull(returnedFlightRoute);
        assertEquals(expectedFlightRoute.getDepartureCity(), returnedFlightRoute.getDepartureCity());
        assertEquals(expectedFlightRoute.getDestinationCity(), returnedFlightRoute.getDestinationCity());
        assertEquals(expectedFlightRoute.getDepartureTime(), returnedFlightRoute.getDepartureTime());
        assertEquals(expectedFlightRoute.getArrivalTime(), returnedFlightRoute.getArrivalTime());
        assertEquals(expectedFlightRoute.getAircraftId(), returnedFlightRoute.getAircraftId());
        assertEquals(expectedFlightRoute.getOverbooking(), returnedFlightRoute.getOverbooking());
        assertTrue(expectedFlightRoute.getStartDate().equals(returnedFlightRoute.getStartDate()));
        assertTrue(expectedFlightRoute.getEndDate().equals(returnedFlightRoute.getEndDate()));

        //Verify Flights in flight table
        List<Flight> returnedFlights = assertDoesNotThrow(() -> flightRepository.findAllByFlightNumberOrderByFlightDate(returnedFlightRoute.getFlightNumber()));
        Date expectedDate = returnedFlightRoute.getStartDate();
        if (isSkipSeatList == false) {
            for (int i = 0; i < returnedFlights.size(); i++) {
                Flight flight = returnedFlights.get(i);
                assertEquals(expectedDate, flight.getFlightDate());
                assertEquals(availableTicket, flight.getAvailableTickets());
                expectedDate = constants.datePlusSomeDays(expectedDate, 1);
                // Verify Flight Seat Info
                List<FlightSeatInfo> flightSeatInfos = flightSeatInfoRepository.findAllByFlightId(flight.getFlightId());
                assertEquals(availableTicket, flightSeatInfos.size());
            }
        }
    }

    private boolean validFlightExistInList(List<FlightRoute> flightRouteList, long flightNumber) {
        for (int i = 0; i < flightRouteList.size(); i++) {
            if (flightRouteList.get(i).getFlightNumber() == flightNumber) {
                return true;
            }
        }
        return false;
    }

}