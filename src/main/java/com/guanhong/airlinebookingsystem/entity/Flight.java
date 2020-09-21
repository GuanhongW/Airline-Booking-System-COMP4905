package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "flight")
public class Flight {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Column(name = "flight_id", nullable = false)
    private Long flightId;

    @Column(name = "flight_number", nullable = false)
    private Long flightNumber;

    @Column(name = "flight_date", nullable = false)
    private Date flightDate;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    public Flight() {
    }

    public Flight(long flightNumber, Date flightDate, int availableSeats) {
        this.flightNumber = flightNumber;
        this.flightDate = flightDate;
        this.availableSeats = availableSeats;
    }

    public Flight(long flightNumber, Date flightDate) {
        this.flightNumber = flightNumber;
        this.flightDate = flightDate;
    }


    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(Long flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }
}
