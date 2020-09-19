package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.FlightSeatInfo;
import com.guanhong.airlinebookingsystem.model.SeatList;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightSeatInfoRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
class GetAvailableFlightTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightSeatInfoRepository flightSeatInfoRepository;

    private static Constants constants = Constants.getInstance();

    @AfterAll
    static void deleateTemporaryFlight(@Autowired FlightRepository flightRepository,
                                       @Autowired FlightSeatInfoRepository flightSeatInfoRepository){
        Flight temporaryFlight = flightRepository.findFlightByflightNumber(constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT);
        if (temporaryFlight != null){
            flightRepository.delete(temporaryFlight);
            assertNull(flightRepository.findFlightByflightNumber(constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT));
            assertNull(flightSeatInfoRepository.findFlightSeatInfoByFlightNumber(constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT));
        }
        temporaryFlight = flightRepository.findFlightByflightNumber(constants.FLIGHT_NUMBER_EXPIRED);
        if (temporaryFlight != null){
            flightRepository.delete(temporaryFlight);
            assertNull(flightRepository.findFlightByflightNumber(constants.FLIGHT_NUMBER_EXPIRED));
            assertNull(flightSeatInfoRepository.findFlightSeatInfoByFlightNumber(constants.FLIGHT_NUMBER_EXPIRED));
        }
        temporaryFlight = flightRepository.findFlightByflightNumber(constants.FLIGHT_NUMBER_AVAILABLE);
        if (temporaryFlight != null){
            flightRepository.delete(temporaryFlight);
            assertNull(flightRepository.findFlightByflightNumber(constants.FLIGHT_NUMBER_AVAILABLE));
            assertNull(flightSeatInfoRepository.findFlightSeatInfoByFlightNumber(constants.FLIGHT_NUMBER_AVAILABLE));
        }
    }

    @Test
    void getAllAvailableSeat_Success() throws Exception {
        //Create a flight without any available seats
        long flightNumber = constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        Date startDate = constants.datePlusSomeDays(constants.today(), 5);;
        Date endDate = constants.datePlusSomeDays(constants.today(), 35);
        Integer availableSeat = 0;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        Flight returnedFlight = assertDoesNotThrow(()->flightRepository.save(newFlight1));
        SeatList newSeatList = new SeatList(returnedFlight.getCapacity());
        FlightSeatInfo flightSeatInfo = new FlightSeatInfo(returnedFlight.getFlightNumber(),
                newSeatList.toJsonString());
        FlightSeatInfo returnedFlightSeatInfo = flightSeatInfoRepository.save(flightSeatInfo);
        validFlightInfo(newFlight1,flightNumber,0);


        // Create a flight the end date is before
        flightNumber = constants.FLIGHT_NUMBER_EXPIRED;
        startDate = constants.datePlusSomeDays(constants.today(), -100);
        endDate = constants.datePlusSomeDays(constants.today(),  0);
        availableSeat = 156;
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        returnedFlight = assertDoesNotThrow(()->flightRepository.save(newFlight2));
        newSeatList = new SeatList(returnedFlight.getCapacity());
        flightSeatInfo = new FlightSeatInfo(returnedFlight.getFlightNumber(),
                newSeatList.toJsonString());
        returnedFlightSeatInfo = flightSeatInfoRepository.save(flightSeatInfo);
        validFlightInfo(newFlight2,flightNumber,156);


        flightNumber = constants.FLIGHT_NUMBER_AVAILABLE;
        startDate = constants.datePlusSomeDays(constants.today(), 1);
        endDate = constants.datePlusSomeDays(constants.today(),  8);
        Flight newFlight3 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        returnedFlight = assertDoesNotThrow(()->flightRepository.save(newFlight3));
        newSeatList = new SeatList(returnedFlight.getCapacity());
        flightSeatInfo = new FlightSeatInfo(returnedFlight.getFlightNumber(),
                newSeatList.toJsonString());
        returnedFlightSeatInfo = flightSeatInfoRepository.save(flightSeatInfo);
        validFlightInfo(newFlight3,flightNumber,156);

        assertTrue(validFlightNotExistInList(flightService.getAllAvailableFlights(), constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT));
        assertTrue(validFlightNotExistInList(flightService.getAllAvailableFlights(), constants.FLIGHT_NUMBER_EXPIRED));
        assertTrue(validFlightExistInList(flightService.getAllAvailableFlights(), constants.FLIGHT_NUMBER_AVAILABLE));
    }


    private void validFlightInfo(Flight expectedFlight, long actualFlightNumber, int availableSeats) {
        assertEquals(expectedFlight.getFlightNumber(), actualFlightNumber);
        Flight returnedFlight = flightRepository.findFlightByflightNumber(actualFlightNumber);
        assertNotNull(returnedFlight);
        assertEquals(expectedFlight.getDepartureCity(), returnedFlight.getDepartureCity());
        assertEquals(expectedFlight.getDestinationCity(), returnedFlight.getDestinationCity());
        assertEquals(expectedFlight.getDepartureTime(), returnedFlight.getDepartureTime());
        assertEquals(expectedFlight.getArrivalTime(), returnedFlight.getArrivalTime());
        assertEquals(expectedFlight.getCapacity(), returnedFlight.getCapacity());
        assertEquals(expectedFlight.getOverbooking(), returnedFlight.getOverbooking());
        assertTrue(expectedFlight.getStartDate().equals(returnedFlight.getStartDate()));
        assertTrue(expectedFlight.getEndDate().equals(returnedFlight.getEndDate()));
        assertEquals(availableSeats, returnedFlight.getAvailableSeat());
        FlightSeatInfo flightSeatInfo = assertDoesNotThrow(()->flightSeatInfoRepository.findFlightSeatInfoByFlightNumber(expectedFlight.getFlightNumber()));
        SeatList seatList = assertDoesNotThrow(()->flightSeatInfo.getSeatListByJson());
        assertEquals(expectedFlight.getCapacity(), seatList.getSize());
    }

    private boolean validFlightExistInList(List<Flight> flightList, long flightNumber){
        for (int i = 0; i < flightList.size(); i++){
            if (flightList.get(i).getFlightNumber() == flightNumber){
                return true;
            }
        }
        return false;
    }

    private boolean validFlightNotExistInList(List<Flight> flightList, long flightNumber){
        for (int i = 0; i < flightList.size(); i++){
            if (flightList.get(i).getFlightNumber() == flightNumber){
                return false;
            }
        }
        return true;
    }
}
