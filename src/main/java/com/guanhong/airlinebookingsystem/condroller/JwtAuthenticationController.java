package com.guanhong.airlinebookingsystem.condroller;

import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
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


import com.guanhong.airlinebookingsystem.entity.User;

@RestController
@CrossOrigin
@Slf4j
public class JwtAuthenticationController {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity createAuthenticationToken(@RequestBody User user) throws Exception {
        try{
            if (user.getUsername() == null || user.getPassword() == null){
                log.error("Http Code: 400  URL: authenticate  username or password is null");
                return ResponseEntity.badRequest().body("Username or password cannot be empty");
            }
            return ResponseEntity.ok(jwtUserDetailsService.authUser(user));

        }
        catch (Exception e){
            log.error("URL: authenticate, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }



    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody AccountInfo newUserInfo) throws Exception {
        try{
            if (newUserInfo.getUsername() == null || newUserInfo.getPassword() == null || newUserInfo.getRole() == null){
                log.error("Http Code: 400  URL: register  username, password, or role is null");
                return ResponseEntity.badRequest().body("Username, password, or role cannot be empty");
            }
            return ResponseEntity.ok(jwtUserDetailsService.createAccount(newUserInfo));
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
