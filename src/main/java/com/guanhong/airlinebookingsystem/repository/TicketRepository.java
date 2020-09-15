package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.Flight;

import com.guanhong.airlinebookingsystem.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    public List<Ticket> findTicketsByCustomerId(long customerId);

    public List<Ticket> findTicketsByFlightNumber(long flightNumber);
}