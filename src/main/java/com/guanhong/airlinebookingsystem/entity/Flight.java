package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name= "flight")
public class Flight {
    public Flight() {
    }

    @Id
    @Column(name = "id", nullable = false)
    private long flightNumber;

    @Column(name = "departure_city", nullable = false)
    private String departureCity;

    @Column(name = "destination_city", nullable = false)
    private String destinationCity;

    @Column(name = "departure_time", nullable = false)
    private Time departureTime;

    @Column(name = "arrival_time", nullable = false)
    private Time arrivalTime;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "overbooking", nullable = false)
    private int overbooking;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "available_seat", nullable = false)
    private int availableSeat;

    public long getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(long flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOverbooking() {
        return overbooking;
    }

    public void setOverbooking(int overbooking) {
        this.overbooking = overbooking;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getAvailableSeat() {
        return availableSeat;
    }

    public void setAvailableSeat(int availableSeat) {
        this.availableSeat = availableSeat;
    }

    public int calculateAvailableSeat(int capacity, int overbooking){
        int availableSeats = capacity + (int)Math.floor(capacity * overbooking);
        return availableSeats;
    }
}
