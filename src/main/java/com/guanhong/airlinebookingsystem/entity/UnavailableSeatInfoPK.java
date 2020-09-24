package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Objects;

public class UnavailableSeatInfoPK implements Serializable {

    @Column(name = "flight_id", nullable = false)
    private long flightId;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    public UnavailableSeatInfoPK() {
    }

    public UnavailableSeatInfoPK(long flightId, Integer seatNumber) {
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
        UnavailableSeatInfoPK unavailableSeatInfoPK = (UnavailableSeatInfoPK) o;
        return Objects.equals(flightId, unavailableSeatInfoPK.flightId) &&
                Objects.equals(seatNumber, unavailableSeatInfoPK.seatNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(flightId, seatNumber);
    }
}
