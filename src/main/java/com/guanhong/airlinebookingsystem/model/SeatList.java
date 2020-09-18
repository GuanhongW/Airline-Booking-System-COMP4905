package com.guanhong.airlinebookingsystem.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SeatList {

    private List<Seat> seatList;

    public SeatList() {
        this.seatList = new ArrayList<>();
    }

    public SeatList(List<Seat> seatList){
        this.seatList = seatList;
    }

    public SeatList(int capacity) {
        this.seatList = new ArrayList<>();
        for (int i = 1; i <= capacity; i++){
            this.seatList.add(new Seat(i, SeatStatus.AVAILABLE));
        }
    }

    public List<Seat> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
    }

    public void addSeat(int seatNumber, SeatStatus seatStatus){
        this.seatList.add(new Seat(seatNumber, seatStatus));
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this.seatList);
    }

    public int getSize(){
        return this.seatList.size();
    }

}
