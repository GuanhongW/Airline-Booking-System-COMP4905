package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.UnavailableSeatInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;


@Repository
public interface UnavailableSeatInfoRepository extends JpaRepository<UnavailableSeatInfo, Long> {
    public List<UnavailableSeatInfo> findAllByFlightId(long flightId);
    public int deleteAllByFlightId(long flightId);

}