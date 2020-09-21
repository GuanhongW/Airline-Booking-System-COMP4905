package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    public List<Ticket> findTicketsByCustomerId(long customerId);

    public List<Ticket> findTicketsByFlightId(long flightId);

    public Ticket findTicketByCustomerIdAndFlightIdAndFlightDate(long customerId, long flightId, Date flightDate);
}