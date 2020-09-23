package com.guanhong.airlinebookingsystem.condroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.config.JwtTokenUtil;
import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.FlightRoute;
import com.guanhong.airlinebookingsystem.entity.Role;
import com.guanhong.airlinebookingsystem.entity.User;
import com.guanhong.airlinebookingsystem.service.FlightService;
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
    @RequestMapping(value = "/bookFlight", method = RequestMethod.POST)
    public ResponseEntity bookFlightController(HttpServletRequest request, @RequestBody Flight flight) throws Exception {
        int bookIndex = 0;
        User user = null;
        try{
            if (flight == null){
                log.error("Http Code: 400  URL: bookFlight  flight information is empty");
                return ResponseEntity.badRequest().body("flight information is empty.");
            }
            else if (flight.getFlightNumber() == null || flight.getFlightDate() == null){
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
                    return new ResponseEntity("Only customer user can book new flights.", HttpStatus.BAD_REQUEST);
                }
            }
        }
        catch (ServerException e){
            log.error("URL: bookFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        while (bookIndex < 3){
            try{
                ResponseEntity res = new ResponseEntity(ticketService.bookFlight(flight, user.getId()), HttpStatus.OK);
                log.info(user.getId() + " got the ticket in flight " + flight.getFlightNumber() + " on "+
                        flight.getFlightDate().toString());
                return res;
            }
            catch (ServerException e){
                log.error("URL: bookFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
                return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            catch (ClientException e){
                log.error("URL: bookFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (DataIntegrityViolationException e){
                log.error(e.getMessage());
                log.info("Create entity in customer info table is failed, rolling back in user table");
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
        log.error("User: " + user.getId() + " tried book the flight " + flight.getFlightNumber() + " on " + flight.getFlightDate().toString() + "," +
                " the system failed three times optimistic lock.");
        return new ResponseEntity("Server is busy. Try to book flight failed.", HttpStatus.SERVICE_UNAVAILABLE);


    }
}
