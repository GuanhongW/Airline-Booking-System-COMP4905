package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.*;


@Entity
@Table(name = "flight_seat_info")
public class FlightSeatInfo {
    @Id
    @Column(name = "flight_id", nullable = false)
    private long flightNumber;

    @Column(name = "seat_list")
    private String seatList;

    public FlightSeatInfo() {
    }

    public FlightSeatInfo(long flightNumber, String seatList) {
        this.flightNumber = flightNumber;
        this.seatList = seatList;
    }

    public long getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(long flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getSeatList() {
        return seatList;
    }

    public void setSeatList(String seatList) {
        this.seatList = seatList;
    }
}
