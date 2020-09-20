package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.Ticket;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TicketService {

    @Autowired
    private FlightRepository flightRepository;

    @Transactional
    public Ticket bookFlight(Flight flight) throws Exception{
        validFlightIsAvailable(flight);
        return null;
    }

    private boolean validFlightIsAvailable(Flight flight){
        return true;
    }
}
