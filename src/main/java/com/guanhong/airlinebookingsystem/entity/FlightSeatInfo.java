package com.guanhong.airlinebookingsystem.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.model.Seat;
import com.guanhong.airlinebookingsystem.model.SeatList;


import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "flight_seat_info")
public class FlightSeatInfo {
    @Id
    @Column(name = "flight_id", nullable = false)
    private long flightId;

    @Column(name = "seat_list")
    private String seatList;

    public FlightSeatInfo() {
    }

    public FlightSeatInfo(long flightId, String seatList) {
        this.flightId = flightId;
        this.seatList = seatList;
    }

    public long getFlightId() {
        return flightId;
    }

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }

    public String getSeatList() {
        return seatList;
    }

    public void setSeatList(String seatList) {
        this.seatList = seatList;
    }

    public SeatList getSeatListByJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Seat> test = mapper.readValue(this.seatList, new TypeReference<List<Seat>>() {});
        return new SeatList(test);
    }
}
