package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "flight")
public class Flight {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Column(name = "flight_id", nullable = false)
    private long flightId;

    @Column(name = "flight_number", nullable = false)
    private long flightNumber;

    @Column(name = "flight_date", nullable = false)
    private Date flightDate;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    public Flight() {
    }

    public Flight(long flightNumber, Date flightDate, int availableSeats) {
        this.flightNumber = flightNumber;
        this.flightDate = flightDate;
        this.availableSeats = availableSeats;
    }

    public long getFlightId() {
        return flightId;
    }

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }

    public long getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(long flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
