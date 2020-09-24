package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.FlightRoute;
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
public class GetAvailableFlightsTest {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRouteRepository flightRouteRepository;

    @Autowired
    private FlightSeatInfoRepository flightSeatInfoRepository;

    private static Constants constants = Constants.getInstance();

    @Test
    @Transactional
    void getAllAvailableFlightsByFlightNumber_Success() throws Exception {
        long flightNumber = constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 200;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        Date startDate = constants.datePlusSomeDays(constants.today(), 0);
        Date endDate = constants.datePlusSomeDays(constants.today(), 5);
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute returnedFlightRoute = assertDoesNotThrow(() -> flightService.createNewFlight(newFlightRoute1));
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute1);
        validFlightInfo(validFlightRoute, flightNumber, 53, false);


        List<Flight> flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        // Update one flight's available seats as 0
        flights.get(1).setAvailableTickets(0);
        flightRepository.save(flights.get(1));
        assertEquals(0, flightRepository.findFlightByFlightId(flights.get(1).getFlightId()).getAvailableTickets());
        // Update one flight's start date is yesterday
        flights.get(3).setFlightDate(constants.datePlusSomeDays(constants.today(), -1));
        flightRepository.save(flights.get(3));
        assertEquals(constants.datePlusSomeDays(constants.today(), -1), flightRepository.findFlightByFlightId(flights.get(3).getFlightId()).getFlightDate());


        List<Flight> returnedFlights = flightService.getAllAvailableFlightsByFlightNumber(flightNumber);
        assertEquals(4, returnedFlights.size());
        assertEquals(constants.today(), returnedFlights.get(0).getFlightDate());
        assertEquals(constants.datePlusSomeDays(constants.today(), 2), returnedFlights.get(1).getFlightDate());
        assertEquals(constants.datePlusSomeDays(constants.today(), 4), returnedFlights.get(2).getFlightDate());
        assertEquals(constants.datePlusSomeDays(constants.today(), 5), returnedFlights.get(3).getFlightDate());
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
//                List<FlightSeatInfo> flightSeatInfos = flightSeatInfoRepository.findAllByFlightId(flight.getFlightId());
//                assertEquals(availableTicket, flightSeatInfos.size());
            }
        }
    }
}
