package com.guanhong.airlinebookingsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "ticket")
public class Ticket {
    public Ticket() {
    }

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long ticketId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;



    @Column(name = "flight_id", nullable = false)
    private Long flightId;

    @Column(name = "flight_number", nullable = false)
    private Long flightNumber;


    @Column(name = "flight_date", nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date flightDate;
    
    @Column(name = "seat_number")
    private Integer seatNumber;



    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    public Ticket(long customerId, long flightId, Date flightDate, long flightNumber) {
        this.customerId = customerId;
        this.flightId = flightId;
        this.flightDate = flightDate;
        this.flightNumber = flightNumber;
    }

    public Ticket(long ticketId){
        this.ticketId = ticketId;
    }



    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }

    public Long getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(Long flightNumber) {
        this.flightNumber = flightNumber;
    }
}
