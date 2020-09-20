package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.FlightRoute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface FlightRouteRepository extends JpaRepository<FlightRoute, Long> {
    public FlightRoute findFlightByflightNumber(long id);
    public List<FlightRoute> findAllByEndDateAfter(Date date);
}
