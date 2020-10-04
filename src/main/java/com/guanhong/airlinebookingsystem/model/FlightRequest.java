package com.guanhong.airlinebookingsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class FlightRequest extends FlightNumberRequest{

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date flightDate;

    public FlightRequest() {
    }

    public FlightRequest(Long flightNumber, Date flightDate) {
        super(flightNumber);
        this.flightDate = flightDate;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }
}
