package com.guanhong.airlinebookingsystem.condroller;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.config.JwtTokenUtil;
import com.guanhong.airlinebookingsystem.entity.FlightRoute;
import com.guanhong.airlinebookingsystem.entity.Role;
import com.guanhong.airlinebookingsystem.model.FlightNumberRequest;
import com.guanhong.airlinebookingsystem.model.FlightRequest;
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
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/createFlight", method = RequestMethod.POST)
    public ResponseEntity createFlightController(HttpServletRequest request, @RequestBody FlightRoute newFlightRoute){
        try{
            if (newFlightRoute == null){
                log.error("Http Code: 400  URL: createFlight  new flight information is empty");
                return ResponseEntity.badRequest().body("new flight information is empty");
            }
            final String requestTokenHeader = request.getHeader("Authorization");

            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                Role role = jwtUserDetailsService.getUserRole(username);
                if (!role.equals(Role.ADMIN)){
                    log.warn("A Non-admin user: " + username + " try to create flight.");
                    System.out.println("Save finished: " + new java.util.Date().getTime());
                    return new ResponseEntity("Only admin user can create new flights.", HttpStatus.UNAUTHORIZED);
                }
            }
            return ResponseEntity.ok(flightService.createNewFlight(newFlightRoute));
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
    @RequestMapping(value = "/api/getFlightRoutes", method = RequestMethod.GET)
    public ResponseEntity getAvailableFlightRoutesController(){
        try{
            return new ResponseEntity(flightService.getAllAvailableFlightRoutes(), HttpStatus.OK);
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

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/getFlightRoute", method = RequestMethod.GET)
    public ResponseEntity getAvailableFlightRouteController(long flightNumber){
        try{
            return new ResponseEntity(flightService.getFlightRoute(flightNumber), HttpStatus.OK);
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

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/getFlightsByFlightNumber", method = RequestMethod.GET)
    public ResponseEntity getAvailableFlightsByFlightNumberController(long flightNumber){
        try{
            return new ResponseEntity(flightService.getAllAvailableFlightsByFlightNumber(flightNumber), HttpStatus.OK);
        }
//        catch (ServerException e){
//            log.error("URL: getFlights, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
//            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
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

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/updateFlight", method = RequestMethod.POST)
    public ResponseEntity updateFlightController(HttpServletRequest request, @RequestBody FlightRoute newFlightRoute){
        try{
            final String requestTokenHeader = request.getHeader("Authorization");

            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                Role role = jwtUserDetailsService.getUserRole(username);
                if (!role.equals(Role.ADMIN)){
                    log.warn("A Non-admin user: " + username + " try to update flight.");
                    return new ResponseEntity("Only admin user can update new flights.", HttpStatus.UNAUTHORIZED);
                }
            }
            if (newFlightRoute == null){
                log.error("Http Code: 400  URL: createFlight  update flight information is empty");
                return ResponseEntity.badRequest().body("update flight information is empty");
            }
            else if (newFlightRoute.getFlightNumber() == 0){
                log.error("Http Code: 400  URL: updateFlight  flight number is empty or invalid.");
                return ResponseEntity.badRequest().body("flight number is empty or invalid.");
            }
            return ResponseEntity.ok(flightService.updateFlight(newFlightRoute));
        }
        catch (ServerException e){
            log.error("URL: updateFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (ClientException e){
            log.error("URL: updateFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            log.info("Create entity in customer info table is failed, rolling back in user table");
            return new ResponseEntity("URL: createFlight, Http Code: 500: Create a new flight failed because of server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("URL: updateFlight, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/cancelFlightRoute", method = RequestMethod.POST)
    public ResponseEntity cancelFlightRouteController(HttpServletRequest request, @RequestBody FlightNumberRequest flightNumber){
        try{
            final String requestTokenHeader = request.getHeader("Authorization");

            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                Role role = jwtUserDetailsService.getUserRole(username);
                if (!role.equals(Role.ADMIN)){
                    log.warn("A Non-admin user: " + username + " try to cancel flight route.");
                    return new ResponseEntity("Only admin user can cancel flight route.", HttpStatus.UNAUTHORIZED);
                }
            }
            if (flightNumber == null || flightNumber.getFlightNumber() == null){
                log.error("Http Code: 400  URL: cancelFlightRoute  flight number is empty");
                return ResponseEntity.badRequest().body("Flight number is empty.");
            }
            return ResponseEntity.ok(flightService.cancelFlightRoute(flightNumber.getFlightNumber()));
        }
        catch (ServerException e){
            log.error("URL: cancelFlightRoute, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (ClientException e){
            log.error("URL: cancelFlightRoute, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            log.info("URL: cancelFlightRoute database error");
            return new ResponseEntity("URL: cancelFlightRoute, Http Code: 500: Cancel flight route failed because of database error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("URL: cancelFlightRoute, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/api/cancelFlight", method = RequestMethod.POST)
    public ResponseEntity cancelFlightController(HttpServletRequest request, @RequestBody FlightRequest flightRequest){
        try{
            final String requestTokenHeader = request.getHeader("Authorization");

            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                Role role = jwtUserDetailsService.getUserRole(username);
                if (!role.equals(Role.ADMIN)){
                    log.warn("A Non-admin user: " + username + " try to cancel flight.");
                    return new ResponseEntity("Only admin user can cancel flight.", HttpStatus.UNAUTHORIZED);
                }
            }
            if (flightRequest == null){
                log.error("Http Code: 400  URL: cancelFlight  flight information are empty");
                return ResponseEntity.badRequest().body("Flight information are empty.");
            }
            else if (flightRequest.getFlightNumber() == null || flightRequest.getFlightDate() == null){
                log.error("Http Code: 400  URL: cancelFlight  flight number or date are empty");
                return ResponseEntity.badRequest().body("Flight number or date are empty.");
            }
            return ResponseEntity.ok(flightService.cancelFlight(flightRequest.getFlightNumber(),flightRequest.getFlightDate()));
        }
        catch (ServerException e){
            log.error("URL: cancelFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (ClientException e){
            log.error("URL: cancelFlight, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            log.info("URL: cancelFlight database error");
            return new ResponseEntity("URL: cancelFlight, Http Code: 500: Cancel flight failed because of database error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("URL: cancelFlight, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
