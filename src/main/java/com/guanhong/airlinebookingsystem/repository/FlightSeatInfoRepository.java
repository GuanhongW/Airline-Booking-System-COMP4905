package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.Flight;

import com.guanhong.airlinebookingsystem.entity.FlightSeatInfo;
import com.guanhong.airlinebookingsystem.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FlightSeatInfoRepository extends JpaRepository<FlightSeatInfo, Long> {
    public FlightSeatInfo findFlightSeatInfoByFlightNumber(long flightNumber);
}