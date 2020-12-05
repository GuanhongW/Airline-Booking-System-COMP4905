package com.guanhong.airlinebookingsystem.controller;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.config.JwtTokenUtil;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.BookSeatRequest;
import com.guanhong.airlinebookingsystem.model.FlightRequest;
import com.guanhong.airlinebookingsystem.service.TicketService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import com.guanhong.airlinebookingsystem.service.JwtUserDetailsService;


import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@RestController
@CrossOrigin
@Slf4j
public class TicketController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TicketService ticketService;

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/bookFlight", method = RequestMethod.POST)
    public ResponseEntity bookFlightController(HttpServletRequest request, @RequestBody FlightRequest flightRequest) throws Exception {
        int bookIndex = 0;
        User user = null;
        try{
            if (flightRequest == null){
                log.error("Http Code: 400  URL: bookFlight  flight information is empty");
                return ResponseEntity.badRequest().body("flight information is empty.");
            }
            else if (flightRequest.getFlightNumber() == null || flightRequest.getFlightDate() == null){
                log.error("Http Code: 400  URL: bookFlight  flight number or flight date is empty.");
                return ResponseEntity.badRequest().body("flight number or flight date is empty.");
            }
            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                user = jwtUserDetailsService.getUserByUsername(username);
                if (!user.getRole().equals(Role.USER)){
                    log.warn("A admin user: " + username + " try to create flight.");
                    return new ResponseEntity("Only customer user can book new flights.", HttpStatus.UNAUTHORIZED);
                }
            }
        }
        catch (ServerException e){
            log.error("URL: bookFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        while (bookIndex < 3){
            try{
                // Timestamp for book flight
//                log.warn("Book flight try Index: " + bookIndex + "at " + new Timestamp(System.currentTimeMillis()));
                ResponseEntity res = new ResponseEntity(ticketService.bookFlight(flightRequest, user.getId()), HttpStatus.OK);
                log.info(user.getId() + " got the ticket in flight " + flightRequest.getFlightNumber() + " on "+
                        flightRequest.getFlightDate().toString());
                return res;
            }
            catch (ServerException e){
                log.error("URL: bookFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
                return new ResponseEntity(e.getMessage(), e.getHttpStatus());
            }
            catch (ClientException e){
                log.error("URL: bookFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
                return new ResponseEntity(e.getMessage(), e.getHttpStatus());
            }
            catch (DataIntegrityViolationException e){
                log.error(e.getMessage());
                log.info("Create entity in flight or flight route table is failed, rolling back in user table");
                return new ResponseEntity("URL: bookFlight, Http Code: 500: Book a new flight failed because of server error.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            catch (StaleStateException e){
                bookIndex++;
                log.info("#" + bookIndex+1 + ": User " + user.getId() + " try to book the flight but failed by optimistic lock.");
            }
            catch (ObjectOptimisticLockingFailureException e){
                bookIndex++;
                log.info("#" + bookIndex+1 + ": User " + user.getId() + " try to book the flight but failed by optimistic lock.");
            }
            catch (Exception e){
                log.error("URL: bookFlight, Http Code: 400: " + e.getMessage());
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        log.error("User: " + user.getId() + " tried book the flight " + flightRequest.getFlightNumber() + " on " + flightRequest.getFlightDate().toString() + "," +
                " the system failed three times optimistic lock.");
        return new ResponseEntity("Server is busy. Try to book flight failed.", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/bookSeat", method = RequestMethod.POST)
    public ResponseEntity bookSeatController(HttpServletRequest request, @RequestBody BookSeatRequest bookSeatRequest) throws Exception {
        try{
            User user = null;
            if (bookSeatRequest == null){
                log.error("Http Code: 400  URL: bookSeat  flight information is empty");
                return ResponseEntity.badRequest().body("flight information is empty.");
            }
            else if (bookSeatRequest.getFlightNumber() == null || bookSeatRequest.getFlightDate() == null){
                log.error("Http Code: 400  URL: bookSeat  flight number or flight date is empty.");
                return ResponseEntity.badRequest().body("Flight number or flight date is empty.");
            }
            else if (bookSeatRequest.getSeatNumber() == null){
                log.error("Http Code: 400  URL: bookSeat  seat number is empty.");
                return ResponseEntity.badRequest().body("Seat number is empty.");
            }
            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                user = jwtUserDetailsService.getUserByUsername(username);
                if (!user.getRole().equals(Role.USER)){
                    log.warn("A admin user: " + username + " try to create flight.");
                    return new ResponseEntity("Only customer user can book new flights.", HttpStatus.UNAUTHORIZED);
                }
            }
            log.warn("The book seat transaction start at " + new Timestamp(System.currentTimeMillis()));
            Ticket responseTicket = ticketService.bookSeat(bookSeatRequest, user.getId());
            log.warn("The book seat transaction end at " + new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.ok(responseTicket);
        }
        catch (ServerException e){
            log.error("URL: bookSeat, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), e.getHttpStatus());
        }
        catch (ClientException e){
            log.error("URL: bookSeat, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e){
            if (e.getRootCause().toString().contains("Duplicate entry")){
                log.info("URL: bookSeat, Http Code: 400  The seat " + bookSeatRequest.getSeatNumber() + " in the flight " + bookSeatRequest.getFlightNumber() +
                        " on " + bookSeatRequest.getFlightDate() +  " is not available." );
                return new ResponseEntity("The seat " + bookSeatRequest.getSeatNumber() +
                        " in the flight " + bookSeatRequest.getFlightNumber() +
                        " on " + bookSeatRequest.getFlightDate() +  " is not available.", HttpStatus.BAD_REQUEST);
            }
            log.error(e.getMessage());
            log.info("Create entity in Unavailable Seat Info table is failed, rolling back.");
            return new ResponseEntity("URL: bookSeat, Http Code: 500: Book a seat flight failed because of server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("URL: bookSeat, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/cancelTicket", method = RequestMethod.POST)
    public ResponseEntity cancelTicketController(HttpServletRequest request, @RequestBody FlightRequest flightRequest) throws Exception {
        try{
            User user = null;
            if (flightRequest == null){
                log.error("Http Code: 400  URL: cancelTicket  flight information is empty");
                return ResponseEntity.badRequest().body("flight information is empty.");
            }
            else if (flightRequest.getFlightNumber() == null || flightRequest.getFlightDate() == null){
                log.error("Http Code: 400  URL: cancelTicket  flight number or flight date is empty.");
                return ResponseEntity.badRequest().body("Flight number or flight date is empty.");
            }

            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                user = jwtUserDetailsService.getUserByUsername(username);
                if (!user.getRole().equals(Role.USER)){
                    log.warn("A admin user: " + username + " try to cancel a tucjet.");
                    return new ResponseEntity("Only customer user can cancel existent ticket.", HttpStatus.UNAUTHORIZED);
                }
            }
            log.warn("The cancel ticket transaction start at " + new Timestamp(System.currentTimeMillis()));
            boolean isSuccess = ticketService.cancelTicket(flightRequest, user.getId());
            log.warn("The cancel ticket transaction end at " + new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.ok(isSuccess);
        }
        catch (ServerException e){
            log.error("URL: cancelTicket, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (ClientException e){
            log.error("URL: cancelTicket, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            log.info("Cancel the ticket in flight " + flightRequest.getFlightNumber() + " on " + flightRequest.getFlightDate() + " is failed, rolling back.");
            return new ResponseEntity("URL: cancelTicket, Http Code: 500: Cancel the ticket in flight " +
                    flightRequest.getFlightNumber() + " on " + flightRequest.getFlightDate() +
                    " is failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("URL: cancelTicket, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/getTicketByCustomer", method = RequestMethod.GET)
    public ResponseEntity getTicketbyCustomerId(HttpServletRequest request) throws Exception {
        try{
            User user = null;
            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                user = jwtUserDetailsService.getUserByUsername(username);
                if (!user.getRole().equals(Role.USER)){
                    log.warn("A admin user: " + username + " try to get customer's ticket without customer Id.");
                    return new ResponseEntity("Only customer user can get all tickets without customer Id", HttpStatus.UNAUTHORIZED);
                }
            }
            return ResponseEntity.ok(ticketService.getAllTicketByCustomerId(user.getId()));
        }
        catch (ServerException e){
            log.error("URL: getTicketByCustomer, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (ClientException e){
            log.error("URL: getTicketByCustomer, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            log.error("URL: getTicketByCustomer, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
