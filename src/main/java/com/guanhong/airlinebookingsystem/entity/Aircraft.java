package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.*;

@Entity
@Table(name = "aircraft")
public class Aircraft {
    @Id
    @Column(name = "aircraft_id", nullable = false)
    private int aircraftId;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    public Aircraft() {
    }

    public Aircraft(int aircraft_id, int capacity) {
        this.aircraftId = aircraft_id;
        this.capacity = capacity;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(int aircraft_id) {
        this.aircraftId = aircraft_id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
