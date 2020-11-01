package com.guanhong.airlinebookingsystem.condroller;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.UserCredential;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
public class JwtAuthenticationController {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity createAuthenticationTokenController(@RequestBody UserCredential userCredential){
        try{
            if (userCredential.getUsername() == null || userCredential.getPassword() == null){
                log.error("Http Code: 400  URL: authenticate  username or password is null");
                return ResponseEntity.badRequest().body("Username or password cannot be empty.");
            }
            return ResponseEntity.ok(jwtUserDetailsService.authUser(userCredential));

        }
        catch (Exception e){
            log.error("URL: authenticate, Http Code: 400: " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }



    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="apiKey") })
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity createUserController(@RequestBody AccountInfo newUserInfo) throws Exception {
        System.out.println(newUserInfo.getRole());
        try{
            if (newUserInfo.getUsername() == null || newUserInfo.getPassword() == null || newUserInfo.getRole() == null){
                log.error("Http Code: 400  URL: register  username, password, or role is null");
                return ResponseEntity.badRequest().body("Username, password, or role cannot be empty");
            }
            CreateUserResponse test = jwtUserDetailsService.createAccount(newUserInfo);
            return ResponseEntity.ok(test);
        }
        catch (ServerException e){
            log.error("URL: register, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (ClientException e){
            log.error("URL: register, Http Code: " + e.getHttpStatus() + ": " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            log.info("Create entity in customer info table is failed, rolling back in user table");
            return new ResponseEntity("Create a new flight failed because of server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e){
            log.error("URL: register, Http Code: 400: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
