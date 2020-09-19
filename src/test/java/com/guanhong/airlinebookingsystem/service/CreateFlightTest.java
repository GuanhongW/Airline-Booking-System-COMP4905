package com.guanhong.airlinebookingsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.Seat;
import com.guanhong.airlinebookingsystem.model.SeatList;
import com.guanhong.airlinebookingsystem.repository.CustomerInfoRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightSeatInfoRepository;
import com.guanhong.airlinebookingsystem.repository.UserRepository;
import org.apache.tomcat.util.bcel.Const;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
class CreateFlightTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightSeatInfoRepository flightSeatInfoRepository;

    @Autowired
    private MockMvc mockMvc;

    private static Constants constants = Constants.getInstance();


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

        // Create default flight
        long flightNumber = Constants.DEFAULT_FLIGHT_NUMBER;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        String date = "2021-10-02";
        Date startDate = new Date(dateFormat.parse(date).getTime());
        date = "2021-12-28";
        Date endDate = new Date(dateFormat.parse(date).getTime());
        Integer availableSeat = null;
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
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
        long flightNumber = Constants.DEFAULT_FLIGHT_NUMBER;
        Flight flight = flightRepository.findFlightByflightNumber(flightNumber);
        flightRepository.delete(flight);
        assertNull(flightRepository.findFlightByflightNumber(flightNumber));
        assertNull(flightSeatInfoRepository.findFlightSeatInfoByFlightNumber(flightNumber));

    }

    @Test
    @Transactional
    void createNewFlight_FlightNumber_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;
        // Test1: FlightNumber is 1
        flightNumber = 1;
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        Flight validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Test2: FlightNumber is 9999
        flightNumber = constants.FLIGHT_NUMBER_9999;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Test3: FlightNumber is 99
        flightNumber = constants.FLIGHT_NUMBER_99;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Test4: FlightNumber is 999
        flightNumber = constants.FLIGHT_NUMBER_999;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);
    }

    @Test
    @Transactional
    void createNewFlight_FlightNumber_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);

        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;
        // Test1: FlightNumber is -1
        flightNumber = -1;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The flight number should not excess 4 digits.", exception.getMessage());

        // Test2: FlightNumber is 0
        flightNumber = 0;
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("The flight number should not excess 4 digits.",exception.getMessage());

        // Test3: FlightNumber is 10000
        flightNumber = 10000;
        Flight newFlight3 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight3));
        assertEquals("The flight number should not excess 4 digits.",exception.getMessage());

        // Test4: FlightNumber is default
        flightNumber = Constants.DEFAULT_FLIGHT_NUMBER;
        Flight newFlight4 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight4));
        assertEquals("The flight number already be used.",exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_DepartureCity_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        //Departure City's length is 1
        departureCity = "A";
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        Flight validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        //Departure City's lenght is 255
        flightNumber = constants.getNextAvailableFlightNumber();
        departureCity = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        //Departure City's lenght is random
        flightNumber = constants.getNextAvailableFlightNumber();
        departureCity = "Ottawa";
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);
    }

    @Test
    @Transactional
    void createNewFlight_DepartureCity_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        //Departure City is null
        departureCity = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The departure city should not be empty.", exception.getMessage());

        //Departure City's lenght is more than 255
        flightNumber = constants.getNextAvailableFlightNumber();
        departureCity = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("The length of departure city cannot excess than 255.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_DestinationCity_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        //Departure City's length is 1
        destinationCity = "A";
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        Flight validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        //Departure City's lenght is 255
        flightNumber = constants.getNextAvailableFlightNumber();
        destinationCity = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        //Departure City's lenght is random
        flightNumber = constants.getNextAvailableFlightNumber();
        destinationCity = "Ottawa";
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);
    }

    @Test
    @Transactional
    void createNewFlight_DestinationCity_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate =constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        //Departure City is null
        destinationCity = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The destination city should not be empty.", exception.getMessage());

        //Departure City's lenght is more than 255
        flightNumber = constants.getNextAvailableFlightNumber();
        destinationCity = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("The length of destination city cannot excess than 255.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_Capacity_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Flight capacity is 1
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 1;
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        Flight validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber, 1);

        // Flight capacity is 10
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 10;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,10);

        // Flight capacity is 100
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 100;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,106);

        // Flight capacity is 243
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 243;
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,257);
    }

    @Test
    @Transactional
    void createNewFlight_Capacity_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        Integer capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Capacity is null
        capacity = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("Flight's capacity cannot be empty.", exception.getMessage());

        // Capacity is 0
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 0;
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("Flight's capacity cannot be zero.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_Overbooking_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Overbooking allowance is 0
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(0);
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        Flight validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlight.setOverbooking(validFlight.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlight, flightNumber,148);

        // Overbooking allowance is 10
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(10);
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlight.setOverbooking(validFlight.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlight, flightNumber,162);

        // Overbooking allowance is 8.56
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(8.56);
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlight.setOverbooking(validFlight.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlight, flightNumber,160);

        // Overbooking allowance is 8.794
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(8.794);
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlight.setOverbooking(validFlight.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlight, flightNumber,161);
    }

    @Test
    @Transactional
    void createNewFlight_Overbooking_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Capacity is null
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("Flight's overbook allowance cannot be empty.", exception.getMessage());

        // Capacity is 10
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(11);
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("Flight's overbooking allowance should between 0% to 10%", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_TravelDate_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 180);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Start Date is Tomorrow
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        Flight validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Start Date and end date is tomorrow
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        endDate = constants.tomorrow();
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Start Date is tomorrow and end date is 1 days after start date
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        endDate = constants.datePlusSomeDays(startDate, 1);
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Start Date is tomorrow and end date is 365 days after start date
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        endDate = constants.datePlusSomeDays(startDate, 365);
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);
    }

    @Test
    @Transactional
    void createNewFlight_TravelDate_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Start date is null
        flightNumber = constants.getNextAvailableFlightNumber();
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,null,endDate, availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // End date is null
        flightNumber = constants.getNextAvailableFlightNumber();
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,null, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight2));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // Start date is today
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.today();
        Flight newFlight3 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight3));
        assertEquals("The start of travel range should not before today.", exception.getMessage());

        // Start date is yesterday
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.datePlusSomeDays(constants.today(), -1);
        Flight newFlight4 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight4));
        assertEquals("The start of travel range should not before today.", exception.getMessage());

        // end date is before start date 1 day
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.datePlusSomeDays(constants.tomorrow(),1);
        endDate = constants.tomorrow();
        Flight newFlight5 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight5));
        assertEquals("The end of travel range should not before the start of travel range.", exception.getMessage());

        // end date is before start date 30 day
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.datePlusSomeDays(constants.tomorrow(),31);
        endDate = constants.datePlusSomeDays(constants.tomorrow(),1);
        Flight newFlight6 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight6));
        assertEquals("The end of travel range should not before the start of travel range.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_DepartureTime_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Departure time is 00:00:00
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = Time.valueOf("00:00:00");
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        Flight validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Departure time is 23:59:00
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = Time.valueOf("23:59:00");
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Departure time is 12:35:00
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = Time.valueOf("12:35:00");
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);
    }

    @Test
    @Transactional
    void createNewFlight_DepartureTime_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Departure time is null
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The departure time should not be empty.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_ArrivalTime_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber ;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Arrival time is 00:00:00
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = Time.valueOf("00:00:00");
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        Flight validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Arrival time is 23:59:00
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = Time.valueOf("23:59:00");
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);

        // Arrival time is 12:35:00
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = Time.valueOf("12:35:00");
        newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        validFlight = new Flight(newFlight);
        flightService.createNewFlight(newFlight);
        validFlightInfo(validFlight, flightNumber,156);
    }

    @Test
    @Transactional
    void createNewFlight_ArrivalTime_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;

        // Departure time is null
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = null;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, availableSeat);
        ClientException exception = assertThrows(ClientException.class, ()->flightService.createNewFlight(newFlight1));
        assertEquals("The arrival time should not be empty.", exception.getMessage());
    }



//    @Test
//    void testFlightJson() throws JsonProcessingException {
//        Constants constants = Constants.getInstance();
//        System.out.println(constants.getNextAvailableFlightNumber());
//        System.out.println(constants.getNextAvailableFlightNumber());
//        Calendar test1 = Calendar.getInstance();
//        test1.set(2020,10,2);
//        Date startDate = test1.getTime();
//
//        Calendar test2 = Calendar.getInstance();
//        test2.set(2020,10,2);
//        Date endDate = test2.getTime();
//        Flight newFlight = new Flight(11,"PVG","YYZ",Time.valueOf("00:00:00"),Time.valueOf("23:59:29"),
//                10, constants.getOverbookingByNumber(6);,new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime()), 12);
//
//        System.out.println(newFlight.toString());
//        System.out.println(newFlight.toJsonString());
//    }

    private void validFlightInfo(Flight expectedFlight, long actualFlightNumber, int availableSeats) {
        assertEquals(expectedFlight.getFlightNumber(), actualFlightNumber);
        Flight returnedFlight = flightRepository.findFlightByflightNumber(actualFlightNumber);
        assertNotNull(returnedFlight);
        assertEquals(expectedFlight.getDepartureCity(), returnedFlight.getDepartureCity());
        assertEquals(expectedFlight.getDestinationCity(), returnedFlight.getDestinationCity());
        assertEquals(expectedFlight.getDepartureTime(), returnedFlight.getDepartureTime());
        assertEquals(expectedFlight.getArrivalTime(), returnedFlight.getArrivalTime());
        assertEquals(expectedFlight.getCapacity(), returnedFlight.getCapacity());
        assertEquals(expectedFlight.getOverbooking(), returnedFlight.getOverbooking());
        assertTrue(expectedFlight.getStartDate().equals(returnedFlight.getStartDate()));
        assertTrue(expectedFlight.getEndDate().equals(returnedFlight.getEndDate()));
        assertEquals(availableSeats, returnedFlight.getAvailableSeat());
        FlightSeatInfo flightSeatInfo = assertDoesNotThrow(()->flightSeatInfoRepository.findFlightSeatInfoByFlightNumber(expectedFlight.getFlightNumber()));
        SeatList seatList = assertDoesNotThrow(()->flightSeatInfo.getSeatListByJson());
        assertEquals(expectedFlight.getCapacity(), seatList.getSize());
    }


}
