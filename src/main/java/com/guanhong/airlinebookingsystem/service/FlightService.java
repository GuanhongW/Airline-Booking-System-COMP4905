package com.guanhong.airlinebookingsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.FlightRoute;
import com.guanhong.airlinebookingsystem.entity.FlightSeatInfo;
import com.guanhong.airlinebookingsystem.model.DateHelper;
import com.guanhong.airlinebookingsystem.model.SeatList;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRouteRepository;
import com.guanhong.airlinebookingsystem.repository.FlightSeatInfoRepository;
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
    private FlightSeatInfoRepository flightSeatInfoRepository;

    @Transactional(rollbackFor=Exception.class)
    public FlightRoute createNewFlight(FlightRoute flightRoute) throws Exception {

        validNewFlightInfo(flightRoute);
        flightRoute.setOverbooking(roundOverbookingAllowance(flightRoute.getOverbooking()));
        FlightRoute returnedFlightRoute = flightRouteRepository.save(flightRoute);
        log.info("Create flight " + returnedFlightRoute.getFlightNumber() + " in the system");
        createFlightByFlightRoute(returnedFlightRoute);
        return returnedFlightRoute;
    }



    public List<FlightRoute> getAllAvailableFlightRoutes() throws Exception{
        List<FlightRoute> flightRoutes = flightRouteRepository.findAllByEndDateAfter(new DateHelper().today());
        return flightRoutes;
    }

    private boolean validNewFlightInfo(FlightRoute flightRoute) throws Exception {
        //  1. Valid all flight information without flight number
        validFlightInfoWOFlightNumber(flightRoute);

        // 2. valid if the flight number is valid and exist in the system
        if (flightRoute.getFlightNumber() > 9999 || flightRoute.getFlightNumber() <= 0){
            throw new ClientException("The flight number should not excess 4 digits.");
        }
        else if (flightRouteRepository.findFlightByflightNumber(flightRoute.getFlightNumber()) != null){
            throw new ClientException("The flight number already be used.");
        }
        return true;
    }

    private int calculateAvailableSeat(int capacity, BigDecimal overbooking){
        return overbooking.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(capacity)).
                add(BigDecimal.valueOf(capacity)).intValue();
    }

    private BigDecimal roundOverbookingAllowance(BigDecimal overbooking){
        return overbooking.setScale(2, RoundingMode.FLOOR);
    }

    private boolean validFlightInfoWOFlightNumber(FlightRoute flightRoute) throws ClientException {
        // 1. Verify if capacity is valid
        if (flightRoute.getCapacity() == null){
            throw new ClientException("Flight's capacity cannot be empty.");
        }
        else{
            if (flightRoute.getCapacity() <= 0){
                throw new ClientException("Flight's capacity cannot be zero.");
            }
        }

        // 2. Verify if the overbook allowance is valid
        if (flightRoute.getOverbooking() == null){
            throw new ClientException("Flight's overbook allowance cannot be empty.");
        }
        else {
            if (flightRoute.getOverbooking().compareTo(BigDecimal.valueOf(0)) == -1 ||
                    flightRoute.getOverbooking().compareTo(BigDecimal.valueOf(10)) == 1){
                throw new ClientException("Flight's overbooking allowance should between 0% to 10%");
            }
        }

        // 3. Verify if the range of travel date is valid
        if (flightRoute.getStartDate() == null || flightRoute.getEndDate() == null){
            throw new ClientException("Flight's range of travel date cannot be empty.");
        }
        else {
            // Check if end date is before than start date
            if (flightRoute.getEndDate().before(flightRoute.getStartDate())){
                throw new ClientException("The end of travel range should not before the start of travel range.");
            }
            else if (! new DateHelper().today().before(flightRoute.getStartDate())){
                throw new ClientException("The start of travel range should not before today.");
            }
        }


        // 4. Verify departure city is not null
        if (flightRoute.getDepartureCity() == null){
            throw new ClientException("The departure city should not be empty.");
        }
        else {
            if (flightRoute.getDepartureCity().length() > 255){
                throw new ClientException("The length of departure city cannot excess than 255.");
            }
        }

        // 5. Verify if destination city is null
        if (flightRoute.getDestinationCity() == null){
            throw new ClientException("The destination city should not be empty.");
        }
        else {
            if (flightRoute.getDestinationCity().length() > 255){
                throw new ClientException("The length of destination city cannot excess than 255.");
            }
        }

        // 6. Verify if departure time is null
        if (flightRoute.getDepartureTime() == null){
            throw new ClientException("The departure time should not be empty.");
        }

        // 7. Verify if arrival time is null
        if (flightRoute.getArrivalTime() == null){
            throw new ClientException("The arrival time should not be empty.");
        }
        return true;
    }

    private boolean createFlightByFlightRoute(FlightRoute newFlightRoute) throws JsonProcessingException {
        DateHelper dateHelper = new DateHelper();
        Date currentDate = newFlightRoute.getStartDate();

        int availableSeats = calculateAvailableSeat(newFlightRoute.getCapacity(), newFlightRoute.getOverbooking());
        String seatListJson = new SeatList(newFlightRoute.getCapacity()).toJsonString();
        List<Flight> newFlights = new ArrayList<>();
        Flight flight;

        while (! currentDate.after(newFlightRoute.getEndDate())){
            flight = new Flight(newFlightRoute.getFlightNumber(), currentDate, availableSeats);
            newFlights.add(flight);
            currentDate = dateHelper.datePlusSomeDays(currentDate,1);
        }
        List<Flight> returnedFlights = flightRepository.saveAll(newFlights);
        log.info("Created flights of flightRoute " + newFlightRoute.getFlightNumber() + " in the system");
        List<FlightSeatInfo> newSeatInfos = new ArrayList<>();
        FlightSeatInfo seatInfo;
        for (int i = 0; i < returnedFlights.size(); i++){
            seatInfo = new FlightSeatInfo(returnedFlights.get(i).getFlightId(), seatListJson);
            newSeatInfos.add(seatInfo);
        }
        flightSeatInfoRepository.saveAll(newSeatInfos);
        log.info("Created seat info of flightRoute " + newFlightRoute.getFlightNumber() + " in the system");

        return true;
    }

    private boolean upadteFlightAndSeatInfo(FlightRoute originalFlightRoute, FlightRoute newFlightRoute){
        if (originalFlightRoute.getStartDate().equals(newFlightRoute.getStartDate()) &&
                originalFlightRoute.getEndDate().equals(newFlightRoute.getEndDate())){
            return true;
        }
        return true;

    }

}
