package com.guanhong.airlinebookingsystem.repository;

import com.guanhong.airlinebookingsystem.entity.UnavailableSeatInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.sql.Date;
import java.util.List;


@Repository
public interface UnavailableSeatInfoRepository extends JpaRepository<UnavailableSeatInfo, Long> {
    public List<UnavailableSeatInfo> findAllByFlightId(long flightId);
    public int deleteAllByFlightId(long flightId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from UnavailableSeatInfo a where a.flightId = :flightId and a.seatNumber = :seatNumber")
    public UnavailableSeatInfo findUnavailableSeatInfoByFlightIdAndSeatNumberWithPessimisticLock(long flightId, Integer seatNumber);

    public UnavailableSeatInfo findUnavailableSeatInfoByFlightIdAndSeatNumber(long flightId, Integer seatNumber);

    public int deleteUnavailableSeatInfoByFlightIdAndSeatNumber(long flightId, Integer seatNumber);

}