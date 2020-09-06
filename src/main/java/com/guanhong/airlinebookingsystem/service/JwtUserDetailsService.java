package com.guanhong.airlinebookingsystem.service;

import java.util.ArrayList;

import com.guanhong.airlinebookingsystem.config.JwtTokenUtil;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.UserLoginResponse;
import com.guanhong.airlinebookingsystem.repository.UserRepository;
import com.guanhong.airlinebookingsystem.entity.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

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
     * @param user
     * @return
     */
    public CreateUserResponse save(User user) throws Exception {
        User isNewUser = userRepository.findUserByUsername(user.getUsername());
        if (isNewUser == null){
            user.setPassword(bcryptEncoder.encode(user.getPassword()));
            User newUser = userRepository.save(user);
            CreateUserResponse res = new CreateUserResponse(newUser.getUsername(), newUser.getId());
            return res;
        }
        throw new Exception("The user already exits in system");
    }

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
}
