package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
class CreateFlightTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRouteRepository flightRouteRepository;

    @Autowired
    private UnavailableSeatInfoRepository unavailableSeatInfoRepository;

    @Autowired
    private FlightRepository flightRepository;

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
        int aircraftId = 200;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraftId, overbooking, startDate, endDate);
        flightService.createNewFlight(newFlightRoute);
        FlightRoute returnedFlightRoute = flightRouteRepository.findFlightByflightNumber(newFlightRoute.getFlightNumber());
        assertNotNull(returnedFlightRoute);
        System.out.println("Before All finished.");
    }

    @AfterAll
    static void deleteDefaultAccount(@Autowired UserRepository userRepository,
                                     @Autowired CustomerInfoRepository customerInfoRepository,
                                     @Autowired FlightRouteRepository flightRouteRepository,
                                     @Autowired FlightRepository flightRepository,
                                     @Autowired UnavailableSeatInfoRepository unavailableSeatInfoRepository) throws Exception {

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
            List<Flight> flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
            FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(flightNumber);
            flightRouteRepository.delete(flightRoute);
            assertNull(flightRouteRepository.findFlightByflightNumber(flightNumber));
            List<Flight> emptyFlights = new ArrayList<>();
            assertEquals(emptyFlights, flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber));
            List<UnavailableSeatInfo> emptyList = new ArrayList<>();
            for (int j = 0; j < flights.size(); j++){
                assertEquals(emptyList, unavailableSeatInfoRepository.findAllByFlightId(flights.get(j).getFlightId()));
            }
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;
        // Test1: FlightNumber is 1
        flightNumber = constants.FLIGHT_NUMBER_1;
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Test2: FlightNumber is 9999
        flightNumber = constants.FLIGHT_NUMBER_9999;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Test3: FlightNumber is 99
        flightNumber = constants.FLIGHT_NUMBER_99;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Test4: FlightNumber is 999
        flightNumber = constants.FLIGHT_NUMBER_999;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);

        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;
        // Test1: FlightNumber is -1
        flightNumber = -1;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("The flight number should not excess 4 digits.", exception.getMessage());

        // Test2: FlightNumber is 0
        flightNumber = 0;
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("The flight number should not excess 4 digits.", exception.getMessage());

        // Test3: FlightNumber is 10000
        flightNumber = 10000;
        FlightRoute newFlightRoute3 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute3));
        assertEquals("The flight number should not excess 4 digits.", exception.getMessage());

        // Test4: FlightNumber is default
        flightNumber = defaultFlights.get(0);
        FlightRoute newFlightRoute4 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // Departure City's length is 1
        departureCity = "A";
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Departure City's lenght is 255
        flightNumber = constants.getNextAvailableFlightNumber();
        departureCity = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Departure City's lenght is random
        flightNumber = constants.getNextAvailableFlightNumber();
        departureCity = "Ottawa";
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);
    }

    @Test
    @Transactional
    void createNewFlight_DepartureCity_Failed() {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // Departure City is null
        departureCity = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("The departure city should not be empty.", exception.getMessage());

        // Departure City's lenght is more than 255
        flightNumber = constants.getNextAvailableFlightNumber();
        departureCity = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        //Departure City's length is 1
        destinationCity = "A";
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        //Departure City's lenght is 255
        flightNumber = constants.getNextAvailableFlightNumber();
        destinationCity = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        //Departure City's lenght is random
        flightNumber = constants.getNextAvailableFlightNumber();
        destinationCity = "Ottawa";
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        //Departure City is null
        destinationCity = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("The destination city should not be empty.", exception.getMessage());

        //Departure City's lenght is more than 255
        flightNumber = constants.getNextAvailableFlightNumber();
        destinationCity = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("The length of destination city cannot excess than 255.", exception.getMessage());
    }

    //TODO: Re-write it
    @Test
    @Transactional
    void createNewFlight_aircraft_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // Flight aircraft is 200
        flightNumber = constants.getNextAvailableFlightNumber();
        aircraft = 200;
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 53);

        // Flight aircraft is 737
        flightNumber = constants.getNextAvailableFlightNumber();
        aircraft = 737;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 179);

        // Flight aircraft is 777
        flightNumber = constants.getNextAvailableFlightNumber();
        aircraft = 777;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 424);

        // Flight aircraft is 320
        flightNumber = constants.getNextAvailableFlightNumber();
        aircraft = 320;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 154);

        // Flight aircraft is 900
        flightNumber = constants.getNextAvailableFlightNumber();
        aircraft = 900;
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, 80);
    }

    //TODO: Re-write it
    @Test
    @Transactional
    void createNewFlight_aircraft_Failed() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long flightNumber = constants.getNextAvailableFlightNumber();
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        Integer aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);

        // aircraft is null
        aircraft = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("Flight's aircraft cannot be empty.", exception.getMessage());

        // aircraft is 888
        flightNumber = constants.getNextAvailableFlightNumber();
        aircraft = 888;
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("Flight's aircraft is invalid.", exception.getMessage());
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);


        // Overbooking allowance is 0
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(0);
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightRoute.setOverbooking(validFlightRoute.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlightRoute, flightNumber, 50);

        // Overbooking allowance is 10
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(10);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightRoute.setOverbooking(validFlightRoute.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlightRoute, flightNumber, 55);

        // Overbooking allowance is 8.56
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(8.56);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightRoute.setOverbooking(validFlightRoute.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlightRoute, flightNumber, 54);

        // Overbooking allowance is 5.887
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(5.887);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightRoute.setOverbooking(validFlightRoute.getOverbooking().setScale(2, RoundingMode.FLOOR));
        validFlightInfo(validFlightRoute, flightNumber, 52);
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // overbooking is null
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("Flight's overbook allowance cannot be empty.", exception.getMessage());

        // overbooking is 10.01
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(10.01);
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("Flight's overbooking allowance should between 0% to 10%", exception.getMessage());

        // overbooking is -0.01
        flightNumber = constants.getNextAvailableFlightNumber();
        overbooking = BigDecimal.valueOf(-0.01);
        FlightRoute newFlightRoute3 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute3));
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // Start Date is Tomorrow
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Start Date and end date is tomorrow
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        endDate = constants.tomorrow();
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Start Date is tomorrow and end date is 1 days after start date
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        endDate = constants.datePlusSomeDays(startDate, 1);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Start Date is tomorrow and end date is 365 days after start date
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.tomorrow();
        endDate = constants.datePlusSomeDays(startDate, 365);
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Start date is today
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.today();
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // Start date is null
        flightNumber = constants.getNextAvailableFlightNumber();
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, null, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // End date is null
        flightNumber = constants.getNextAvailableFlightNumber();
        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, null);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute2));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // Start date is yesterday
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.datePlusSomeDays(constants.today(), -1);
        FlightRoute newFlightRoute4 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute4));
        assertEquals("The start of travel range should not before today.", exception.getMessage());

        // end date is before start date 1 day
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.datePlusSomeDays(constants.tomorrow(), 1);
        endDate = constants.tomorrow();
        FlightRoute newFlightRoute5 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute5));
        assertEquals("The end of travel range should not before the start of travel range.", exception.getMessage());

        // end date is before start date 30 day
        flightNumber = constants.getNextAvailableFlightNumber();
        startDate = constants.datePlusSomeDays(constants.tomorrow(), 31);
        endDate = constants.datePlusSomeDays(constants.tomorrow(), 1);
        FlightRoute newFlightRoute6 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // Departure time is 00:00:00
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = Time.valueOf("00:00:00");
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Departure time is 23:59:00
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = Time.valueOf("23:59:00");
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Departure time is 12:35:00
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = Time.valueOf("12:35:00");
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // Departure time is null
        flightNumber = constants.getNextAvailableFlightNumber();
        departureTime = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.createNewFlight(newFlightRoute1));
        assertEquals("The departure time should not be empty.", exception.getMessage());
    }

    @Test
    @Transactional
    void createNewFlight_ArrivalTime_Success() throws Exception {
        System.out.println("Thread Info: " + this.getClass() + ": " + Thread.currentThread().getName());
        long flightNumber;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // Arrival time is 00:00:00
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = Time.valueOf("00:00:00");
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Arrival time is 23:59:00
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = Time.valueOf("23:59:00");
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);

        // Arrival time is 12:35:00
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = Time.valueOf("12:35:00");
        newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
        validFlightRoute = new FlightRoute(newFlightRoute);
        flightService.createNewFlight(newFlightRoute);
        validFlightInfo(validFlightRoute, flightNumber, availableTicket);
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
        int aircraft = 200;
        BigDecimal overbooking = constants.getOverbookingByNumber(6);
        Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        Date endDate = constants.datePlusSomeDays(constants.today(), 100);
        int availableTicket = 53;

        // Arrival time is null
        flightNumber = constants.getNextAvailableFlightNumber();
        arrivalTime = null;
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
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

    private void validFlightInfo(FlightRoute expectedFlightRoute, long actualFlightNumber, int availableTickets) {
        assertEquals(expectedFlightRoute.getFlightNumber(), actualFlightNumber);
        FlightRoute returnedFlightRoute = flightRouteRepository.findFlightByflightNumber(actualFlightNumber);
        assertNotNull(returnedFlightRoute);
        assertEquals(expectedFlightRoute.getDepartureCity(), returnedFlightRoute.getDepartureCity());
        assertEquals(expectedFlightRoute.getDestinationCity(), returnedFlightRoute.getDestinationCity());
        assertEquals(expectedFlightRoute.getDepartureTime(), returnedFlightRoute.getDepartureTime());
        assertEquals(expectedFlightRoute.getArrivalTime(), returnedFlightRoute.getArrivalTime());
        assertEquals(expectedFlightRoute.getAircraftId(), returnedFlightRoute.getAircraftId());
        assertEquals(expectedFlightRoute.getOverbooking(), returnedFlightRoute.getOverbooking());
        assertTrue(expectedFlightRoute.getStartDate().equals(returnedFlightRoute.getStartDate()));
        assertTrue(expectedFlightRoute.getEndDate().equals(returnedFlightRoute.getEndDate()));

        //Verify Flights in flight table
        List<Flight> returnedFlights = assertDoesNotThrow(() -> flightRepository.findAllByFlightNumberOrderByFlightDate(returnedFlightRoute.getFlightNumber()));
        Date expectedDate = returnedFlightRoute.getStartDate();
        
        for (int i = 0; i < returnedFlights.size(); i++) {
            Flight flight = returnedFlights.get(i);
            assertEquals(expectedDate, flight.getFlightDate());
            assertEquals(availableTickets, flight.getAvailableTickets());
            expectedDate = constants.datePlusSomeDays(expectedDate, 1);
            // Verify Flight Seat Info
            List<UnavailableSeatInfo> unavailableSeatInfos = unavailableSeatInfoRepository.findAllByFlightId(flight.getFlightId());
            assertEquals(0, unavailableSeatInfos.size());
        }
        assertEquals(constants.datePlusSomeDays(expectedDate, -1), returnedFlightRoute.getEndDate());
    }


}
