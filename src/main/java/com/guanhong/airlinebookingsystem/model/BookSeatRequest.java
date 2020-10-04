package com.guanhong.airlinebookingsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class BookSeatRequest extends FlightRequest {

    private Integer seatNumber;

    public BookSeatRequest() {
    }

    public BookSeatRequest(Long flightNumber, Date flightDate, Integer seatNumber) {
        super(flightNumber, flightDate);
        this.seatNumber = seatNumber;
    }


    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }
}
