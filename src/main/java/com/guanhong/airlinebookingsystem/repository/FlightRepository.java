package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.Flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    public Flight findFlightByflightNumber(long id);
    public List<Flight> findAllByAvailableSeatIsGreaterThanAndEndDateAfter(int availableSeats, Date date);
}
