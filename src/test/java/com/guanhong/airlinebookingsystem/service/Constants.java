package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.FlightSeatInfo;
import com.guanhong.airlinebookingsystem.model.SeatList;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightSeatInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Constants {


    public static long DEFAULT_FLIGHT_NUMBER = 7777;

    public static long FLIGHT_NUMBER_9999 = 9999;
    public static long FLIGHT_NUMBER_999 = 999;
    public static long FLIGHT_NUMBER_99 = 99;
    public static long FLIGHT_NUMBER_NO_AVAILABLE_SEAT = 9998;
    public static long FLIGHT_NUMBER_EXPIRED = 9997;
    public static long FLIGHT_NUMBER_AVAILABLE = 9996;

    public static List<Long> flightList = new ArrayList<>();



    private Constants(){
        // 0 is not a valid flight number
        flightList.add((long)0);
    }

    private static class SingletonHolder{
        private static final Constants INSTANCE = new Constants();
    }

    public static final Constants getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public long getNextAvailableFlightNumber(){
        long flightNumber = flightList.size();
        if (flightNumber == FLIGHT_NUMBER_9999 ||
                flightNumber == FLIGHT_NUMBER_999 ||
                flightNumber == FLIGHT_NUMBER_99 ||
                flightNumber == DEFAULT_FLIGHT_NUMBER){
            flightList.add(flightNumber);
            flightNumber++;
        }
        flightList.add(flightNumber);
        return flightNumber;
    }

    public Date tomorrow() {
        return datePlusSomeDays(today(),1);
    }

    public Date today(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return new Date(calendar.getTime().getTime());
    }

    public Date datePlusSomeDays(Date date, int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return new Date(calendar.getTime().getTime());
    }

    public BigDecimal getOverbookingByNumber(double overbooking){
        return  BigDecimal.valueOf(overbooking).setScale(2);
    }


}
