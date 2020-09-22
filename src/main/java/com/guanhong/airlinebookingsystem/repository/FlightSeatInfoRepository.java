package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.FlightSeatInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.List;


@Repository
public interface FlightSeatInfoRepository extends JpaRepository<FlightSeatInfo, Long> {
    public List<FlightSeatInfo> findAllByFlightId(long flightId);

}