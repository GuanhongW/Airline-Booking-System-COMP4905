package com.guanhong.airlinebookingsystem.condroller;

import com.guanhong.airlinebookingsystem.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.guanhong.airlinebookingsystem.entity.User;

@RestController
@Slf4j
public class SystemController {
    @Autowired
    UserService userService;

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public ResponseEntity getUserById(int id) {
        try{
            System.out.println(userService.getUserById(id).toString());
            return new ResponseEntity(userService.getUserById(id),HttpStatus.OK);
        }
        catch (Exception e){
            System.out.println(e);
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public ResponseEntity addUser(User user){
        try{
            System.out.println(user);
            User newUser = userService.addUser(user);
            return new ResponseEntity(newUser, HttpStatus.OK);
        }
        catch (Exception e){
            System.out.println(e);
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity test(){
        log.trace("Trace log Test 1");
        log.debug("Debug log test 2");
        log.info("Info log test 3");
        log.warn("Warn log test 4");
        log.error("Error log test 5");
        return new ResponseEntity("Test", HttpStatus.BAD_REQUEST);
    }

}
