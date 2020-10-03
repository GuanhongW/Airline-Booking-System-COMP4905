package com.guanhong.airlinebookingsystem.model;

public class FlightNumberRequest {

    private Long flightNumber;

    public FlightNumberRequest() {
    }

    public FlightNumberRequest(Long flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Long getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(Long flightNumber) {
        this.flightNumber = flightNumber;
    }
}
