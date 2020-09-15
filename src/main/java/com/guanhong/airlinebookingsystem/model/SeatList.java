package com.guanhong.airlinebookingsystem.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.HashMap;


public class SeatList {

    private HashMap<String, SeatStatus> seatList;

    public SeatList() {
        this.seatList = new HashMap<String, SeatStatus>();
    }

    public SeatList(HashMap<String, SeatStatus> seatList) {
        this.seatList = seatList;
    }

    public HashMap<String, SeatStatus> getSeatList() {
        return seatList;
    }

    public void setSeatList(HashMap<String, SeatStatus> seatList) {
        this.seatList = seatList;
    }

    public void addSeat(String seatNumber, SeatStatus seatStatus){
        this.seatList.put(seatNumber,seatStatus);
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this.seatList);
    }

}
