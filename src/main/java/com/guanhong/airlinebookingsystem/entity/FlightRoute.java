package com.guanhong.airlinebookingsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.repository.AircraftRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name= "flight_route")
public class FlightRoute {
    public FlightRoute() {
    }

    @Id
    @Column(name = "flight_number", nullable = false)
    private long flightNumber;

    @Column(name = "departure_city", nullable = false)
    private String departureCity;

    @Column(name = "destination_city", nullable = false)
    private String destinationCity;

    @Column(name = "departure_time", nullable = false)
    private Time departureTime;

    @Column(name = "arrival_time", nullable = false)
    private Time arrivalTime;

    @Column(name = "aircraft_id", nullable = false)
    private Integer aircraftId;

    @Column(name = "overbooking", nullable = false)
    private BigDecimal overbooking;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    public FlightRoute(FlightRoute flightRoute) {
        this.flightNumber = flightRoute.getFlightNumber();
        this.departureCity = flightRoute.getDepartureCity();
        this.destinationCity = flightRoute.getDestinationCity();
        this.departureTime = flightRoute.getDepartureTime();
        this.arrivalTime = flightRoute.getArrivalTime();
        this.aircraftId = flightRoute.getAircraftId();
        this.overbooking = flightRoute.getOverbooking();
        this.startDate = flightRoute.getStartDate();
        this.endDate = flightRoute.getEndDate();

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

    public Integer getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(Integer aircraftId) {
        this.aircraftId = aircraftId;
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


    public int calculateAvailableTickets(int capacity){
        int availableTickets = this.overbooking.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(capacity)).
                add(BigDecimal.valueOf(capacity)).intValue();
        return availableTickets;
    }

    public FlightRoute(long flightNumber, String departureCity, String destinationCity, Time departureTime, Time arrivalTime, Integer aircraftId, BigDecimal overbooking, Date startDate, Date endDate) {
        this.flightNumber = flightNumber;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraftId = aircraftId;
        this.overbooking = overbooking;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
