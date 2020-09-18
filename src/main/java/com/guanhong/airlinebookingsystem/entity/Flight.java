package com.guanhong.airlinebookingsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

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
    private Integer capacity;

    @Column(name = "overbooking", nullable = false)
    private BigDecimal overbooking;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "available_seat", nullable = false)
    private Integer availableSeat;

    public Flight(Flight flight) {
        this.flightNumber = flight.getFlightNumber();
        this.departureCity = flight.getDepartureCity();
        this.destinationCity = flight.getDestinationCity();
        this.departureTime = flight.getDepartureTime();
        this.arrivalTime = flight.getArrivalTime();
        this.capacity = flight.getCapacity();
        this.overbooking = flight.getOverbooking();
        this.startDate = flight.getStartDate();
        this.endDate = flight.getEndDate();
        this.availableSeat = flight.getAvailableSeat();
    }

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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getOverbooking() {
        return overbooking;
    }

    public void setOverbooking(BigDecimal overbooking) {
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

    public Integer getAvailableSeat() {
        return availableSeat;
    }

    public void setAvailableSeat(Integer availableSeat) {
        this.availableSeat = availableSeat;
    }

    public int calculateAvailableSeat(int capacity, int overbooking){
        int availableSeats = capacity + (int)Math.floor(capacity * overbooking);
        return availableSeats;
    }

    public Flight(long flightNumber, String departureCity, String destinationCity, Time departureTime, Time arrivalTime, Integer capacity, BigDecimal overbooking, Date startDate, Date endDate, Integer availableSeat) {
        this.flightNumber = flightNumber;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.capacity = capacity;
        this.overbooking = overbooking;
        this.startDate = startDate;
        this.endDate = endDate;
        this.availableSeat = availableSeat;
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
