package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.entity.Flight;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FlightService {

    public Flight createNewFlight(Flight flight) throws ServerException {
        throw new ServerException("test", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
