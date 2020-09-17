package com.guanhong.airlinebookingsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.repository.CustomerInfoRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightSeatInfoRepository;
import com.guanhong.airlinebookingsystem.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class CreateFlightTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRepository flightRepository;

    private static long DEFAULT_FLIGHT_NUMBER = 26;


    @BeforeAll
    static void createDefaultAccount(@Autowired JwtUserDetailsService jwtUserDetailsService,
                                     @Autowired UserRepository userRepository,
                                     @Autowired CustomerInfoRepository customerInfoRepository,
                                     @Autowired FlightService flightService,
                                     @Autowired FlightRepository flightRepository) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

//        String testAdminUsername = "autoadmin1";
//        userRepository.delete(userRepository.findUserByUsername(testAdminUsername));
//        assertNull(userRepository.findUserByUsername(testAdminUsername));
//        testAdminUsername = "autoadmin2";
//        userRepository.delete(userRepository.findUserByUsername(testAdminUsername));
//        assertNull(userRepository.findUserByUsername(testAdminUsername));
//        //Delete default customer user
//        String testCustomerUsername = "auto1@test.com";
//        User customer = userRepository.findUserByUsername(testCustomerUsername);
//        userRepository.delete(customer);
//        assertNull(userRepository.findUserByUsername(testCustomerUsername));
//        assertNull(customerInfoRepository.findAccountById(customer.getId()));
//        testCustomerUsername = "auto2@test.com";
//        customer = userRepository.findUserByUsername(testCustomerUsername);
//        userRepository.delete(customer);
//        assertNull(userRepository.findUserByUsername(testCustomerUsername));
//        assertNull(customerInfoRepository.findAccountById(customer.getId()));

        // Create default admin user 1
        String testAdminUsername = "autoadmin1";
        AccountInfo newUserInfo = new AccountInfo(testAdminUsername,"adminadmin1", Role.ADMIN);
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testAdminUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        testAdminUsername = "autoadmin2";
        newUserInfo = new AccountInfo(testAdminUsername,"adminadmin2", Role.ADMIN);
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testAdminUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        // Create default customer user
        String testCustomerUsername = "auto1@test.com";
        newUserInfo = new AccountInfo(testCustomerUsername,"useruser1", Role.USER,"test", Gender.male,"2000-01-01");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testCustomerUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        CustomerInfo customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

        testCustomerUsername = "auto2@test.com";
        newUserInfo = new AccountInfo(testCustomerUsername,"useruser2", Role.USER,"test", Gender.male,"2000-01-01");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testCustomerUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

        // Create default flight 26
        long flightNumber = DEFAULT_FLIGHT_NUMBER;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        Flight returnedFlight = flightRepository.findFlightByflightNumber(newFlight.getFlightNumber());
        assertNotNull(returnedFlight);
    }

    @AfterAll
    static void deleteDefaultAccount(@Autowired UserRepository userRepository,
                                     @Autowired CustomerInfoRepository customerInfoRepository,
                                     @Autowired FlightRepository flightRepository,
                                     @Autowired FlightSeatInfoRepository flightSeatInfoRepository) throws Exception {
        // Delete default admin user
        String testAdminUsername = "autoadmin1";
        userRepository.delete(userRepository.findUserByUsername(testAdminUsername));
        assertNull(userRepository.findUserByUsername(testAdminUsername));
        testAdminUsername = "autoadmin2";
        userRepository.delete(userRepository.findUserByUsername(testAdminUsername));
        assertNull(userRepository.findUserByUsername(testAdminUsername));
        //Delete default customer user
        String testCustomerUsername = "auto1@test.com";
        User customer = userRepository.findUserByUsername(testCustomerUsername);
        userRepository.delete(customer);
        assertNull(userRepository.findUserByUsername(testCustomerUsername));
        assertNull(customerInfoRepository.findCustomerInfoById(customer.getId()));
        testCustomerUsername = "auto2@test.com";
        customer = userRepository.findUserByUsername(testCustomerUsername);
        userRepository.delete(customer);
        assertNull(userRepository.findUserByUsername(testCustomerUsername));
        assertNull(customerInfoRepository.findCustomerInfoById(customer.getId()));
        // Delete default fligh
        int flightNumber = 26;
        Flight flight = flightRepository.findFlightByflightNumber(flightNumber);
        flightRepository.delete(flight);
        assertNull(flightRepository.findFlightByflightNumber(flightNumber));
        assertNull(flightSeatInfoRepository.findFlightSeatInfoByFlightNumber(flightNumber));

    }

    @Test
    @Transactional
    void createNewFlight_FlightNumber_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        long flightNumber = 0;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;
        // Test1: FlightNumber is 1
        flightNumber = 1;
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Test2: FlightNumber is 9999
        flightNumber = 9999;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Test3: FlightNumber is 35
        flightNumber = 35;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Test4: FlightNumber is 185
        flightNumber = 185;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);
    }

    @Test
    @Transactional
    void createNewFlight_FlightNumber_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        long flightNumber = 0;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;
        // Test1: FlightNumber is -1
        flightNumber = -1;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The flight number should not excess 4 digits.", exception.getMessage());

        // Test2: FlightNumber is 0
        flightNumber = 0;
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("The flight number should not excess 4 digits.",exception.getMessage());

        // Test3: FlightNumber is 10000
        flightNumber = 10000;
        Flight newFlight3 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight3));
        assertEquals("The flight number should not excess 4 digits.",exception.getMessage());

        // Test4: FlightNumber is default
        flightNumber = DEFAULT_FLIGHT_NUMBER;
        Flight newFlight4 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight4));
        assertEquals("The flight number already be used.",exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_DepartureCity_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 1;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        //Departure City's length is 1
        departureCity = "A";
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        //Departure City's lenght is 255
        flightNumber = 2;
        departureCity = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        //Departure City's lenght is random
        flightNumber = 3;
        departureCity = "Ottawa";
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);
    }

    @Test
    @Transactional
    void createNewFlight_DepartureCity_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 1;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        //Departure City is null
        departureCity = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The departure city should not be empty.", exception.getMessage());

        //Departure City's lenght is more than 255
        flightNumber = 2;
        departureCity = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("The length of departure city cannot excess than 255.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_DestinationCity_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 1;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        //Departure City's length is 1
        destinationCity = "A";
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        //Departure City's lenght is 255
        flightNumber = 2;
        destinationCity = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        //Departure City's lenght is random
        flightNumber = 3;
        destinationCity = "Ottawa";
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);
    }

    @Test
    @Transactional
    void createNewFlight_DestinationCity_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 1;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        //Departure City is null
        destinationCity = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The destination city should not be empty.", exception.getMessage());

        //Departure City's lenght is more than 255
        flightNumber = 2;
        destinationCity = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("The length of destination city cannot excess than 255.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_Capacity_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 0;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Flight capacity is 1
        flightNumber = 1;
        capacity = 1;
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 1);

        // Flight capacity is 10
        flightNumber = 2;
        capacity = 10;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 10);

        // Flight capacity is 100
        flightNumber = 3;
        capacity = 100;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 106);

        // Flight capacity is 243
        flightNumber = 4;
        capacity = 243;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 257);
    }

    @Test
    @Transactional
    void createNewFlight_Capacity_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 1;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        Integer capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Capacity is null
        capacity = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("Flight's capacity cannot be empty.", exception.getMessage());

        // Capacity is 0
        capacity = 0;
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("Flight's capacity cannot be zero.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_Overbooking_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 0;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Overbooking allowance is 0
        flightNumber = 1;
        overbooking = BigDecimal.valueOf(0);
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 148);

        // Overbooking allowance is 10
        flightNumber = 2;
        overbooking = BigDecimal.valueOf(10);
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 162);

        // Overbooking allowance is 8
        flightNumber = 3;
        overbooking = BigDecimal.valueOf(8);
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 159);
    }

    @Test
    @Transactional
    void createNewFlight_Overbooking_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 1;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Capacity is null
        overbooking = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("Flight's overbook allowance cannot be empty.", exception.getMessage());

        // Capacity is 10
        overbooking = BigDecimal.valueOf(11);
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("Flight's overbooking allowance should between 0% to 10%", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_TravelDate_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 0;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Start Date is Tomorrow
        flightNumber = 1;
        startDate = tomorrow();
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Start Date and end date is tomorrow
        flightNumber = 2;
        startDate = tomorrow();
        endDate = tomorrow();
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Start Date is tomorrow and end date is 1 days after start date
        flightNumber = 3;
        startDate = tomorrow();
        endDate = datePlusSomeDays(startDate, 1);
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Start Date is tomorrow and end date is 365 days after start date
        flightNumber = 4;
        startDate = tomorrow();
        endDate = datePlusSomeDays(startDate, 365);
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);
    }

    @Test
    @Transactional
    void createNewFlight_TravelDate_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 1;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Start date is null
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,null,new java.sql.Date(endDate.getTime()),
                availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // End date is null
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),null,
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // Start date is today
        startDate = new Date();
        Flight newFlight3 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight3));
        assertEquals("The start of travel range should not before today.", exception.getMessage());

        // Start date is yesterday
        startDate = datePlusSomeDays(new Date(), -1);
        Flight newFlight4 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight4));
        assertEquals("The start of travel range should not before today.", exception.getMessage());

        // end date is before start date 1 day
        startDate = datePlusSomeDays(tomorrow(),1);
        endDate = tomorrow();
        Flight newFlight5 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight5));
        assertEquals("The end of travel range should not before the start of travel range.", exception.getMessage());

        // end date is before start date 30 day
        startDate = datePlusSomeDays(tomorrow(),31);
        endDate = datePlusSomeDays(tomorrow(),1);
        Flight newFlight6 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight6));
        assertEquals("The end of travel range should not before the start of travel range.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_DepartureTime_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 0;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Departure time is 00:00:00
        flightNumber = 1;
        departureTime = Time.valueOf("00:00:00");
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Departure time is 23:59:00
        flightNumber = 2;
        departureTime = Time.valueOf("23:59:00");
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Departure time is 12:35:00
        flightNumber = 3;
        departureTime = Time.valueOf("12:35:00");
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);
    }

    @Test
    @Transactional
    void createNewFlight_DepartureTime_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 0;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Departure time is null
        flightNumber = 1;
        departureTime = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The departure time should not be empty.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_ArrivalTime_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 0;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Arrival time is 00:00:00
        flightNumber = 1;
        arrivalTime = Time.valueOf("00:00:00");
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Arrival time is 23:59:00
        flightNumber = 2;
        arrivalTime = Time.valueOf("23:59:00");
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);

        // Arrival time is 12:35:00
        flightNumber = 3;
        arrivalTime = Time.valueOf("12:35:00");
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        flightService.createNewFlight(newFlight);
        validFlightInfo(newFlight, 156);
    }

    @Test
    @Transactional
    void createNewFlight_ArrivalTime_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int flightNumber = 0;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6);
        String date = "2021-10-02";
        Date startDate = dateFormat.parse(date);
        date = "2021-12-28";
        Date endDate = dateFormat.parse(date);
        Integer availableSeat = null;

        // Departure time is null
        flightNumber = 1;
        arrivalTime = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The arrival time should not be empty.", exception.getMessage());
    }


    @Test
    void testFlightJson() throws JsonProcessingException {
        Calendar test1 = Calendar.getInstance();
        test1.set(2020,10,2);
        Date startDate = test1.getTime();

        Calendar test2 = Calendar.getInstance();
        test2.set(2020,10,2);
        Date endDate = test2.getTime();
        Flight newFlight = new Flight(11,"PVG","YYZ",Time.valueOf("00:00:00"),Time.valueOf("23:59:29"),
                10, BigDecimal.valueOf(6),new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime()), 12);

        System.out.println(newFlight.toString());
        System.out.println(newFlight.toJsonString());
    }

    private void validFlightInfo(Flight flight, int availableSeats){
        Flight returnedFlight = flightRepository.findFlightByflightNumber(flight.getFlightNumber());
        assertNotNull(returnedFlight);
        assertEquals(flight.getDepartureCity(), returnedFlight.getDepartureCity());
        assertEquals(flight.getDestinationCity(), returnedFlight.getDestinationCity());
        assertEquals(flight.getDepartureTime(), returnedFlight.getDepartureTime());
        assertEquals(flight.getArrivalTime(), returnedFlight.getArrivalTime());
        assertEquals(flight.getCapacity(), returnedFlight.getCapacity());
        assertEquals(flight.getOverbooking(), returnedFlight.getOverbooking());
        assertEquals(flight.getStartDate(), returnedFlight.getStartDate());
        assertEquals(flight.getEndDate(), returnedFlight.getEndDate());
        assertEquals(availableSeats, returnedFlight.getAvailableSeat());

    }

    private Date tomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        return calendar.getTime();
    }

    private Date datePlusSomeDays(Date date, int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return calendar.getTime();
    }
}
