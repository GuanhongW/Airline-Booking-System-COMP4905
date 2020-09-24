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

    @Column(name = "available_tickets", nullable = false)
    private Integer availableTickets;

    @Column(name = "version")
    @Version
    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Flight() {
    }

    public Flight(long flightNumber, Date flightDate, int availableTickets) {
        this.flightNumber = flightNumber;
        this.flightDate = flightDate;
        this.availableTickets = availableTickets;
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

    public Integer getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(Integer availableTickets) {
        this.availableTickets = availableTickets;
    }

    public void addAvailableTickets(int availableSeats){
        this.availableTickets += availableSeats;
    }

    public boolean subtractAvailableTickets(int availableSeats){
        if (this.availableTickets < availableSeats){
            return false;
        }
        else {
            this.availableTickets -= availableSeats;
            return true;
        }
    }
}
