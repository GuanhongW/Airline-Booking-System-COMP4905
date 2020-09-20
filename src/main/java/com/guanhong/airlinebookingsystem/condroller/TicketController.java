package com.guanhong.airlinebookingsystem.condroller;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.config.JwtTokenUtil;
import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.entity.FlightRoute;
import com.guanhong.airlinebookingsystem.entity.Role;
import com.guanhong.airlinebookingsystem.service.FlightService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
//    @RequestMapping(value = "/bookFlight", method = RequestMethod.POST)
//    public ResponseEntity bookFlightController(HttpServletRequest request, @RequestBody Flight flight){
//        try{
//            final String requestTokenHeader = request.getHeader("Authorization");
//
//            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
//                String jwtToken = requestTokenHeader.substring(7);
//                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
//                Role role = jwtUserDetailsService.getUserRole(username);
//                if (!role.equals(Role.ADMIN)){
//                    log.warn("A Non-admin user: " + username + " try to create flight.");
//                    return new ResponseEntity("Only admin user can create new flights.", HttpStatus.BAD_REQUEST);
//                }
//            }
//            if (flight == null){
//                log.error("Http Code: 400  URL: bookFlight  flight information is empty");
//                return ResponseEntity.badRequest().body("flight information is empty");
//            }
//            return null;
//        }
//        catch (ServerException e){
//            log.error("URL: bookFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
//            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        catch (ClientException e){
//            log.error("URL: bookFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
//            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//        catch (DataIntegrityViolationException e){
//            log.error(e.getMessage());
//            log.info("Create entity in customer info table is failed, rolling back in user table");
//            return new ResponseEntity("URL: bookFlight, Http Code: 500: Book a new flight failed because of server error.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        catch (Exception e){
//            log.error("URL: bookFlight, Http Code: 400: " + e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
}
