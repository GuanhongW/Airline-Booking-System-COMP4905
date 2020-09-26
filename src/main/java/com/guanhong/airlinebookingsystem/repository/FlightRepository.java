package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.FlightRoute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    public List<Flight> findAllByFlightNumberAndAvailableTicketsGreaterThanAndFlightDateGreaterThanEqualOrderByFlightDate(long flightNumber, int availableSeats, Date today);
    public List<Flight> findAllByFlightNumberOrderByFlightDate(long flightNumber);
    public Flight findFlightByFlightId(long flightId);

//    @Lock(value = LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    public Flight findFlightByFlightNumberAndFlightDate(long flightNumber, Date flightDate);

    public List<Flight> findAllByFlightNumberAndFlightDateBetweenOrderByFlightDate(long flightNumber, Date startDate, Date endDate);

}

