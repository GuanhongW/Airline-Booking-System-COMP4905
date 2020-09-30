package com.guanhong.airlinebookingsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class BookSeatRequest {

    private Long flightNumber;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date flightDate;

    private Integer seatNumber;

    public BookSeatRequest() {
    }

    public BookSeatRequest(Long flightNumber, Date flightDate, Integer seatNumber) {
        this.flightNumber = flightNumber;
        this.flightDate = flightDate;
        this.seatNumber = seatNumber;
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

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }
}
