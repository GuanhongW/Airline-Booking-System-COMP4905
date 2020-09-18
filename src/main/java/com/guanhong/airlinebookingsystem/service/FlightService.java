package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.FlightSeatInfo;
import com.guanhong.airlinebookingsystem.model.SeatList;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightSeatInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

@Service
@Slf4j
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightSeatInfoRepository flightSeatInfoRepository;

    @Transactional(rollbackFor=Exception.class)
    public Flight createNewFlight(Flight flight) throws Exception {

        FlightSeatInfo returnedFlightSeatInfo;
        validNewFlightInfo(flight);
        flight.setOverbooking(roundOverbookingAllowance(flight.getOverbooking()));
        flight.setAvailableSeat(calculateAvailableSeat(flight.getCapacity(),flight.getOverbooking()));
        Flight returnedFlight = flightRepository.save(flight);
        log.info("Create flight " + returnedFlight.getFlightNumber() + " in the system");
        if (returnedFlight != null){
            SeatList newSeatList = new SeatList(returnedFlight.getCapacity());
            FlightSeatInfo flightSeatInfo = new FlightSeatInfo(returnedFlight.getFlightNumber(),
                    newSeatList.toJsonString());
            returnedFlightSeatInfo = flightSeatInfoRepository.save(flightSeatInfo);
            log.info("Created seat info of flight " + returnedFlightSeatInfo.getFlightNumber() + " in the system");
        }
        return returnedFlight;



    }

    private boolean validNewFlightInfo(Flight flight) throws Exception {
        // 1. Verify if capacity is valid
        if (flight.getCapacity() == null){
            throw new ClientException("Flight's capacity cannot be empty.");
        }
        else{
            if (flight.getCapacity() <= 0){
                throw new ClientException("Flight's capacity cannot be zero.");
            }
        }

        // 2. Verify if the overbook allowance is valid
        if (flight.getOverbooking() == null){
            throw new ClientException("Flight's overbook allowance cannot be empty.");
        }
        else {
            if (flight.getOverbooking().compareTo(BigDecimal.valueOf(0)) == -1 ||
                    flight.getOverbooking().compareTo(BigDecimal.valueOf(10)) == 1){
                throw new ClientException("Flight's overbooking allowance should between 0% to 10%");
            }
        }

        // 3. Verify if the range of travel date is valid
        if (flight.getStartDate() == null || flight.getEndDate() == null){
            throw new ClientException("Flight's range of travel date cannot be empty.");
        }
        else {
            // Check if end date is before than start date
            if (flight.getEndDate().before(flight.getStartDate())){
                throw new ClientException("The end of travel range should not before the start of travel range.");
            }
            else if (! new Date().before(flight.getStartDate())){
                throw new ClientException("The start of travel range should not before today.");
            }
        }
        // 4. Verify if the available seat is null
        if (flight.getAvailableSeat() != null){
            throw new ClientException("Front-end bug: available seat should be calculate by server.");
        }

        // 5. Verify departure city is not null
        if (flight.getDepartureCity() == null){
            throw new ClientException("The departure city should not be empty.");
        }
        else {
            if (flight.getDepartureCity().length() > 255){
                throw new ClientException("The length of departure city cannot excess than 255.");
            }
        }

        //6. Verify if destination city is null
        if (flight.getDestinationCity() == null){
            throw new ClientException("The destination city should not be empty.");
        }
        else {
            if (flight.getDestinationCity().length() > 255){
                throw new ClientException("The length of destination city cannot excess than 255.");
            }
        }

        //7. Verify if departure time is null
        if (flight.getDepartureTime() == null){
            throw new ClientException("The departure time should not be empty.");
        }

        // 8. Verify if arrival time is null
        if (flight.getArrivalTime() == null){
            throw new ClientException("The arrival time should not be empty.");
        }

        // 9. valid if the flight number is valid and exist in the system
        if (flight.getFlightNumber() > 9999 || flight.getFlightNumber() <= 0){
            throw new ClientException("The flight number should not excess 4 digits.");
        }
        else if (flightRepository.findFlightByflightNumber(flight.getFlightNumber()) != null){
            throw new ClientException("The flight number already be used.");
        }
        return true;
    }

    private int calculateAvailableSeat(int capacity, BigDecimal overbooking){
        return overbooking.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(capacity)).
                add(BigDecimal.valueOf(capacity)).intValue();
    }

    private BigDecimal roundOverbookingAllowance(BigDecimal overbooking){
        return overbooking.setScale(2);
    }



}
