package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.FlightSeatInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FlightSeatInfoRepository extends JpaRepository<FlightSeatInfo, Long> {
    public FlightSeatInfo findFlightSeatInfoByFlightId(long flightId);
}