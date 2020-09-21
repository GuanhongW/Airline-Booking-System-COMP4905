package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.SeatList;
import com.guanhong.airlinebookingsystem.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
class CreateFlightTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRouteRepository flightRouteRepository;

    @Autowired
    private FlightSeatInfoRepository flightSeatInfoRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private MockMvc mockMvc;

    private static Constants constants = Constants.getInstance();

    private static List<String> defaultAdminUsernames = new ArrayList<>();

    private static List<String> defaultCustomerUsernames = new ArrayList<>();

    private static List<Long> defaultFlights = new ArrayList<>();


    @BeforeAll
    static void createDefaultAccount(@Autowired JwtUserDetailsService jwtUserDetailsService,
                                     @Autowired UserRepository userRepository,
                                     @Autowired CustomerInfoRepository customerInfoRepository,
                                     @Autowired FlightService flightService,
                                     @Autowired FlightRouteRepository flightRouteRepository) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Create default admin user 1

        String testUsername = constants.getNextAdminUsername();
        defaultAdminUsernames.add(testUsername);
        AccountInfo newUserInfo = new AccountInfo(testUsername, constants.ADMIN_USER_PASSWORD_0, Role.ADMIN);
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        testUsername = constants.getNextAdminUsername();
        defaultAdminUsernames.add(testUsername);
        newUserInfo = new AccountInfo(testUsername, constants.ADMIN_USER_PASSWORD_1, Role.ADMIN);
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        // Create default customer user
        testUsername = constants.getNextCustomerUsername();
        defaultCustomerUsernames.add(testUsername);
        newUserInfo = new AccountInfo(testUsername, constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "test", Gender.male, "2000-01-01");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        CustomerInfo customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

        testUsername = constants.getNextCustomerUsername();
        defaultCustomerUsernames.add(testUsername);
        newUserInfo = new AccountInfo(testUsername, constants.CUSTOMER_USER_PASSWORD_1, Role.USER, "test", Gender.male, "2000-01-01");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

        // Create default flight
        long flightNumber = constants.getNextAvailableFlightNumber();
        defaultFlights.add(flightNumber);
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        flightService.createNewFlight(newFlightRoute);
        FlightRoute returnedFlightRoute = flightRouteRepository.findFlightByflightNumber(newFlightRoute.getFlightNumber());
        assertNotNull(returnedFlightRoute);
        System.out.println("Before All finished.");
    }

    @AfterAll
    static void deleteDefaultAccount(@Autowired UserRepository userRepository,
                                     @Autowired CustomerInfoRepository customerInfoRepository,
                                     @Autowired FlightRouteRepository flightRouteRepository,
                                     @Autowired FlightSeatInfoRepository flightSeatInfoRepository) throws Exception {

        // Delete default admin user
        String testUsername;
        for (int i = 0; i < defaultAdminUsernames.size(); i++) {
            testUsername = defaultAdminUsernames.get(i);
            User user = userRepository.findUserByUsername(testUsername);
            userRepository.delete(user);
            assertNull(userRepository.findUserByUsername(testUsername));
            assertNull(customerInfoRepository.findCustomerInfoById(user.getId()));
        }

        // Delete default customer user
        for (int i = 0; i < defaultCustomerUsernames.size(); i++) {
            testUsername = defaultCustomerUsernames.get(i);
            User user = userRepository.findUserByUsername(testUsername);
            userRepository.delete(user);
            assertNull(userRepository.findUserByUsername(testUsername));
            assertNull(customerInfoRepository.findCustomerInfoById(user.getId()));
        }

        // Delete default flight
        long flightNumber;
        for (int i = 0; i < defaultFlights.size(); i++) {
            flightNumber = defaultFlights.get(i);
            FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(flightNumber);
            flightRouteRepository.delete(flightRoute);
            assertNull(flightRouteRepository.findFlightByflightNumber(flightNumber));
            assertNull(flightSeatInfoRepository.findFlightSeatInfoByFlightId(flightNumber));
        }
    }

    @Test
    @Transactional
    void createNewFlight_FlightNumber_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;
        // Test1: FlightNumber is 1
        flightNumber = constants.FLIGHT_NUMBER_1;
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Test2: FlightNumber is 9999
        flightNumber = constants.FLIGHT_NUMBER_9999;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Test3: FlightNumber is 99
        flightNumber = constants.FLIGHT_NUMBER_99;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Test4: FlightNumber is 999
        flightNumber = constants.FLIGHT_NUMBER_999;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);
    }

    @Test
    @Transactional
    void createNewFlight_FlightNumber_Failed() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);

        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;
        // Test1: FlightNumber is -1
        flightNumber = -1;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("The flight number should not excess 4 digits.", exception.getMessage());

        // Test2: FlightNumber is 0
        flightNumber = 0;
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("The flight number should not excess 4 digits.", exception.getMessage());

        // Test3: FlightNumber is 10000
        flightNumber = 10000;
        FlightRoute newFlightRoute3 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute3));
        assertEquals("The flight number should not excess 4 digits.", exception.getMessage());

        // Test4: FlightNumber is default
        flightNumber = defaultFlights.get(0);
        FlightRoute newFlightRoute4 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute4));
        assertEquals("The flight number already be used.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_DepartureCity_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        //Departure City's length is 1
        departureCity = "A";
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        //Departure City's lenght is 255
        flightNumber = constants.getNextAvailableFlightNumber();
        departureCity = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        //Departure City's lenght is random
        flightNumber = constants.getNextAvailableFlightNumber();
        departureCity = "Ottawa";
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);
    }

    @Test
    @Transactional
    void createNewFlight_DepartureCity_Failed() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        //Departure City is null
        departureCity = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("The departure city should not be empty.", exception.getMessage());

        //Departure City's lenght is more than 255
        flightNumber = constants.getNextAvailableFlightNumber();
        departureCity = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("The length of departure city cannot excess than 255.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_DestinationCity_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        //Departure City's length is 1
        destinationCity = "A";
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        //Departure City's lenght is 255
        flightNumber = constants.getNextAvailableFlightNumber();
        destinationCity = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        //Departure City's lenght is random
        flightNumber = constants.getNextAvailableFlightNumber();
        destinationCity = "Ottawa";
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);
    }

    @Test
    @Transactional
    void createNewFlight_DestinationCity_Failed() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        //Departure City is null
        destinationCity = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("The destination city should not be empty.", exception.getMessage());

        //Departure City's lenght is more than 255
        flightNumber = constants.getNextAvailableFlightNumber();
        destinationCity = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("The length of destination city cannot excess than 255.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_Capacity_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Flight capacity is 1
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 1;
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 1);

        // Flight capacity is 10
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 10;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 10);

        // Flight capacity is 100
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 100;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 106);

        // Flight capacity is 243
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 243;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 257);
    }

    @Test
    @Transactional
    void createNewFlight_Capacity_Failed() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        Integer capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Capacity is null
        capacity = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("Flight's capacity cannot be empty.", exception.getMessage());

        // Capacity is 0
        flightNumber = constants.getNextAvailableFlightNumber();
        capacity = 0;
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("Flight's capacity cannot be zero.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_Overbooking_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Overbooking allowance is 0
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(0);
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightRoute.setOverbooking(validFlightRoute.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlightRoute, flightNumber, 148);

        // Overbooking allowance is 10
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(10);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightRoute.setOverbooking(validFlightRoute.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlightRoute, flightNumber, 162);

        // Overbooking allowance is 8.56
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(8.56);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightRoute.setOverbooking(validFlightRoute.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlightRoute, flightNumber, 160);

        // Overbooking allowance is 8.794
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(8.794);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightRoute.setOverbooking(validFlightRoute.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlightRoute, flightNumber, 161);
    }

    @Test
    @Transactional
    void createNewFlight_Overbooking_Failed() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Capacity is null
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("Flight's overbook allowance cannot be empty.", exception.getMessage());

        // Capacity is 10
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(11);
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("Flight's overbooking allowance should between 0% to 10%", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_TravelDate_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Start Date is Tomorrow
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Start Date and end date is tomorrow
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        endDate = constants.tomorrow();
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Start Date is tomorrow and end date is 1 days after start date
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        endDate = constants.datePlusSomeDays(startDate, 1);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Start Date is tomorrow and end date is 365 days after start date
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        endDate = constants.datePlusSomeDays(startDate, 365);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Start date is today
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.today();
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);
    }

    @Test
    @Transactional
    void createNewFlight_TravelDate_Failed() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Start date is null
        flightNumber = constants.getNextAvailableFlightNumber();
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, null, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // End date is null
        flightNumber = constants.getNextAvailableFlightNumber();
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, null);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // Start date is yesterday
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.datePlusSomeDays(constants.today(), -1);
        FlightRoute newFlightRoute4 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute4));
        assertEquals("The start of travel range should not before today.", exception.getMessage());

        // end date is before start date 1 day
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.datePlusSomeDays(constants.tomorrow(), 1);
        endDate = constants.tomorrow();
        FlightRoute newFlightRoute5 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute5));
        assertEquals("The end of travel range should not before the start of travel range.", exception.getMessage());

        // end date is before start date 30 day
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.datePlusSomeDays(constants.tomorrow(), 31);
        endDate = constants.datePlusSomeDays(constants.tomorrow(), 1);
        FlightRoute newFlightRoute6 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute6));
        assertEquals("The end of travel range should not before the start of travel range.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_DepartureTime_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Departure time is 00:00:00
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = Time.valueOf("00:00:00");
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Departure time is 23:59:00
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = Time.valueOf("23:59:00");
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Departure time is 12:35:00
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = Time.valueOf("12:35:00");
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);
    }

    @Test
    @Transactional
    void createNewFlight_DepartureTime_Failed() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Departure time is null
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("The departure time should not be empty.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_ArrivalTime_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Arrival time is 00:00:00
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = Time.valueOf("00:00:00");
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Arrival time is 23:59:00
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = Time.valueOf("23:59:00");
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);

        // Arrival time is 12:35:00
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = Time.valueOf("12:35:00");
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 156);
    }

    @Test
    @Transactional
    void createNewFlight_ArrivalTime_Failed() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        Integer availableSeat = null;

        // Departure time is null
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
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

    private void validFlightInfo(FlightRoute expectedFlightRoute, long actualFlightNumber, int availableSeats) {
        assertEquals(expectedFlightRoute.getFlightNumber(), actualFlightNumber);
        FlightRoute returnedFlightRoute = flightRouteRepository.findFlightByflightNumber(actualFlightNumber);
        assertNotNull(returnedFlightRoute);
        assertEquals(expectedFlightRoute.getDepartureCity(), returnedFlightRoute.getDepartureCity());
        assertEquals(expectedFlightRoute.getDestinationCity(), returnedFlightRoute.getDestinationCity());
        assertEquals(expectedFlightRoute.getDepartureTime(), returnedFlightRoute.getDepartureTime());
        assertEquals(expectedFlightRoute.getArrivalTime(), returnedFlightRoute.getArrivalTime());
        assertEquals(expectedFlightRoute.getCapacity(), returnedFlightRoute.getCapacity());
        assertEquals(expectedFlightRoute.getOverbooking(), returnedFlightRoute.getOverbooking());
        assertTrue(expectedFlightRoute.getStartDate().equals(returnedFlightRoute.getStartDate()));
        assertTrue(expectedFlightRoute.getEndDate().equals(returnedFlightRoute.getEndDate()));

        //Verify Flights in flight table
        List<Flight> returnedFlights = assertDoesNotThrow(() -> flightRepository.findAllByFlightNumberOrderByFlightDate(returnedFlightRoute.getFlightNumber()));
        Date expectedDate = returnedFlightRoute.getStartDate();

        SeatList seatList;
        for (int i = 0; i < returnedFlights.size(); i++) {
            Flight flight = returnedFlights.get(i);
            assertEquals(expectedDate, flight.getFlightDate());
            assertEquals(availableSeats, flight.getAvailableSeats());
            expectedDate = constants.datePlusSomeDays(expectedDate, 1);
            // Verify Flight Seat Info
            FlightSeatInfo flightSeatInfo = assertDoesNotThrow(() -> flightSeatInfoRepository.findFlightSeatInfoByFlightId(flight.getFlightId()));
            seatList = assertDoesNotThrow(() -> flightSeatInfo.getSeatListByJson());
            assertEquals(expectedFlightRoute.getCapacity(), seatList.getSize());
        }
    }


}
