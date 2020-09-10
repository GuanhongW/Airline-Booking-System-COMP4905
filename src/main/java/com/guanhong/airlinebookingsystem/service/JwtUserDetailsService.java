package com.guanhong.airlinebookingsystem.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.config.JwtTokenUtil;
import com.guanhong.airlinebookingsystem.entity.CustomerInfo;
import com.guanhong.airlinebookingsystem.entity.Gender;
import com.guanhong.airlinebookingsystem.entity.Role;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.UserLoginResponse;
import com.guanhong.airlinebookingsystem.repository.CustomerInfoRepository;
import com.guanhong.airlinebookingsystem.repository.UserRepository;
import com.guanhong.airlinebookingsystem.entity.User;

import com.sun.tools.javah.Gen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.validator.routines.EmailValidator;


@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerInfoRepository customerInfoRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    /**
     * Check if the username and password is correct.
     * If it is correct, return the JWT to front-end
     * @param user
     * @return UserLoginResponse res
     * @throws Exception
     */
    public UserLoginResponse authUser(User user) throws Exception {
        authenticate(user.getUsername(), user.getPassword());

        User newUser = userRepository.findUserByUsername(user.getUsername());
        final UserDetails userDetails = loadUser(newUser);

        final String token = jwtTokenUtil.generateToken(userDetails);

        UserLoginResponse res = new UserLoginResponse(newUser.getUsername(), newUser.getId(),token);
        return res;
    }

    /**
     * Create a new user if the user does not exist in the system
     * @param accountInfo
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public CreateUserResponse createAccount(AccountInfo accountInfo) throws Exception {
        if (verifyAccountInfo(accountInfo)){
            User newUser = new User(accountInfo.getUsername(), bcryptEncoder.encode(accountInfo.getPassword()), accountInfo.getRole());

            User returnedUser = userRepository.save(newUser);
            log.info("User: " + returnedUser.getUsername() + " create the account in the system");

            if (accountInfo.getRole() == Role.USER){
                CustomerInfo customerInfo = new CustomerInfo(returnedUser.getId(), accountInfo.getName(), convertStringToDate(accountInfo.getBirthDate()), accountInfo.getGender());
                CustomerInfo returnedInfo;
                try{
                    returnedInfo = customerInfoRepository.save(customerInfo);
                    log.info("Customer user: " + returnedUser.getUsername() + " create the profile in customer info table");
                }
                catch (Exception e){
                    // If create customer info is failed, rollback will be triggered

                    log.error(e.getMessage());
                    log.info("Create entity in customer info table is failed, rolling back in user table");
                    throw new ServerException("Error in creating entity in customer_info table.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            CreateUserResponse res = new CreateUserResponse(newUser.getUsername(), newUser.getId());
            return res;
        }
        throw new ServerException("Unknown Server Exception.", HttpStatus.INTERNAL_SERVER_ERROR);
    }



//Private function
    /**
     * Authenticate the username and password
     * @param username
     * @param password
     * @throws Exception
     */
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    /**
     * Load user and return the userdetails
     * @param user
     * @return
     * @throws UsernameNotFoundException
     */
    public UserDetails loadUser(User user) throws UsernameNotFoundException {
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + user.getUsername());
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }


    private boolean verifyAccountInfo(AccountInfo accountInfo) throws Exception {
        User isNewUser = userRepository.findUserByUsername(accountInfo.getUsername());
        // Check if the username already existed in the system
        if (isNewUser != null){
            throw new Exception("The user already exits in system.");
        }
        // Check if the password is at least 6 digits
        if (accountInfo.getPassword().length() < 6 || accountInfo.getPassword().length() >255){
            log.error("The password should be at least six digits and less than 255 digits.");
            throw new Exception("The password should be at least six digits and less than 255 digits.");
        }
        // If the role is admin, the system does not requires any more info
        if (accountInfo.getRole() == Role.ADMIN){
            return true;
        }
        // If the role is user, the system need to verify customer info
        else if (accountInfo.getRole() == Role.USER){
            // Check if username is a valid email address
            if (!isEmailValid(accountInfo.getUsername())){
                throw new Exception("The email format is invalid.");
            }
            // Check if birth date is valid format
            if (!isDateValid(accountInfo.getBirthDate())){
                throw new Exception("The birth date's format is invalid.");
            }
            return true;
        }
        throw new Exception("The account role is invalid.");
    }

    private boolean isEmailValid(String email){
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    private boolean isDateValid(String date){
        try{
            Date birthDate = convertStringToDate(date);
            Date today = new Date();
            if (today.before(birthDate)){
                throw new Exception("The birth date cannot after today.");
            }
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    private Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        return dateFormat.parse(date);
    }
}
