package com.guanhong.airlinebookingsystem.condroller;

import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.entity.Flight;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.UserCredential;
import com.guanhong.airlinebookingsystem.service.FlightService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.guanhong.airlinebookingsystem.service.JwtUserDetailsService;

@RestController
@CrossOrigin
@Slf4j
public class FlightController {

    @Autowired
    private FlightService flightService;

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
            log.error("URL: register, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("URL: register, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
