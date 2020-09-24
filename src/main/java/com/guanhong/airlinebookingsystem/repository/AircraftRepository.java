package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.Aircraft;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    public Aircraft findAircraftByAircraftId(int aircraftId);
}
