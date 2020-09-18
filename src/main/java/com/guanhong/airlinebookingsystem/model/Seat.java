package com.guanhong.airlinebookingsystem.model;

public class Seat {
    private int seatId;
    private SeatStatus seatStatus;

    public Seat(int seatId, SeatStatus seatStatus) {
        this.seatId = seatId;
        this.seatStatus = seatStatus;
    }

    public Seat() {
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }
}
