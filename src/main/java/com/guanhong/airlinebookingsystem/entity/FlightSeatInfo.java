package com.guanhong.airlinebookingsystem.entity;


import javax.persistence.*;

import java.io.Serializable;

@Entity
@IdClass(FlightSeatInfoPK.class)
@Table(name = "flight_seat_info")

public class FlightSeatInfo implements Serializable {
//    @Id
//    @GeneratedValue(strategy =  GenerationType.AUTO)
//    @Column(name = "id", nullable = false)
//    private long id;

    @Id
    @Column(name = "flight_id", nullable = false)
    private long flightId;

    @Id
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "seat_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;


    public FlightSeatInfo() {
    }

    public FlightSeatInfo(long flightId, int seatNumber, SeatStatus seatStatus) {
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
