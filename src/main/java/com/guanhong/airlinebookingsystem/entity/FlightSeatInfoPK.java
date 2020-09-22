package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class FlightSeatInfoPK implements Serializable {

    @Column(name = "flight_id", nullable = false)
    private long flightId;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    public FlightSeatInfoPK() {
    }

    public FlightSeatInfoPK(long flightId, Integer seatNumber) {
        this.flightId = flightId;
        this.seatNumber = seatNumber;
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

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightSeatInfoPK flightSeatInfoPK = (FlightSeatInfoPK) o;
        return Objects.equals(flightId, flightSeatInfoPK.flightId) &&
                Objects.equals(seatNumber, flightSeatInfoPK.seatNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(flightId, seatNumber);
    }
}
