package com.guanhong.airlinebookingsystem.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.model.Seat;
import com.guanhong.airlinebookingsystem.model.SeatList;
import com.guanhong.airlinebookingsystem.model.SeatStatus;


import javax.persistence.*;
import java.util.HashMap;
import java.util.List;


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

    public SeatList getSeatListByJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Seat> test =mapper.readValue(this.seatList, new TypeReference<List<Seat>>() {});
        return new SeatList(test);
    }
}
