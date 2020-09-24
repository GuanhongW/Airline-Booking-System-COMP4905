package com.guanhong.airlinebookingsystem.entity;


import javax.persistence.*;

import java.io.Serializable;

@Entity
@IdClass(UnavailableSeatInfoPK.class)
@Table(name = "unavailable_seat_info")

public class UnavailableSeatInfo implements Serializable {

    @Id
    @Column(name = "flight_id", nullable = false)
    private long flightId;

    @Id
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "seat_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;


    public UnavailableSeatInfo() {
    }

    public UnavailableSeatInfo(long flightId, int seatNumber, SeatStatus seatStatus) {
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.seatStatus = seatStatus;
    }

    public long getFlightId() {
        return flightId;
    }

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }

}
