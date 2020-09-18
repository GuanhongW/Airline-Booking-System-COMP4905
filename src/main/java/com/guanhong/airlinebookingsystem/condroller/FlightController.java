package com.guanhong.airlinebookingsystem.condroller;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.config.JwtTokenUtil;
import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.UserCredential;
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
public class FlightController {

    @Autowired
    private FlightService flightService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/createFlight", method = RequestMethod.POST)
    public ResponseEntity createFlightController(@RequestBody Flight newFlight){
        try{
            if (newFlight == null){
                log.error("Http Code: 400  URL: createFlight  new flight information is empty");
                return ResponseEntity.badRequest().body("new flight information is empty");
            }
            return ResponseEntity.ok(flightService.createNewFlight(newFlight));
        }
        catch (ServerException e){
            log.error("URL: createFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (ClientException e){
            log.error("URL: createFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            log.info("Create entity in customer info table is failed, rolling back in user table");
            return new ResponseEntity("URL: createFlight, Http Code: 500: Create a new flight failed because of server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("URL: createFlight, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/getFlights", method = RequestMethod.POST)
    public ResponseEntity getAvailableFlights(){
        try{
            return new ResponseEntity(flightService.getAllAvailableFlights(), HttpStatus.OK);
        }
        catch (ServerException e){
            log.error("URL: getFlights, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            log.info("Create entity in customer info table is failed, rolling back in user table");
            return new ResponseEntity("Create a new flight failed because of server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("URL: getFlights, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
