package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "ticket")
public class Ticket {
    public Ticket() {
    }

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long ticketId;

    @Column(name = "customer_id", nullable = false)
    private long customerId;



    @Column(name = "flight_id", nullable = false)
    private long flightId;

    @Column(name = "seat_number")
    private int seatNumber;

    @Column(name = "flight_date", nullable = false)
    private Date flightDate;

    public Ticket(long ticketId, long customerId, long flightId, Date flightDate) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.flightId = flightId;
        this.flightDate = flightDate;
    }


    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getFlightId() {
        return flightId;
    }

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}
