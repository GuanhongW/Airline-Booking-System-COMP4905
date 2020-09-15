package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.*;

@Entity
@Table(name = "ticket")
public class ticket {
    public ticket() {
    }

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;



    @Column(name = "flight_id", nullable = false)
    private long flightNumber;

    @Column(name = "seat_number")
    private int seatNumber;

    public ticket(long id, long customerId, long flightNumber) {
        this.id = id;
        this.customerId = customerId;
        this.flightNumber = flightNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(long flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}
