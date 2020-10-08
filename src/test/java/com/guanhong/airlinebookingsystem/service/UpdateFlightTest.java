package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.FlightRequest;
import com.guanhong.airlinebookingsystem.repository.*;
import io.swagger.models.auth.In;
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
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class UpdateFlightTest {
    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRouteRepository flightRouteRepository;

    @Autowired
    private UnavailableSeatInfoRepository unavailableSeatInfoRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TicketRepository ticketRepository;

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
        int defaultFlightsNum = 20;
        for (int i = 0; i < defaultFlightsNum; i++) {
            long flightNumber = constants.getNextAvailableFlightNumber();
            defaultFlights.add(flightNumber);
            String departureCity = "YYZ";
            String destinationCity = "YVR";
            Time departureTime = Time.valueOf("10:05:00");
            Time arrivalTime = Time.valueOf("12:00:00");
            int aircraftId = 900;
            BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
            Date startDate = constants.datePlusSomeDays(constants.today(), 80);
            Date endDate = constants.datePlusSomeDays(constants.today(), 100);
            int availableTicket = 80;
            FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                    aircraftId, overbooking, startDate, endDate);
            flightService.createNewFlight(newFlightRoute);
            FlightRoute returnedFlightRoute = flightRouteRepository.findFlightByflightNumber(newFlightRoute.getFlightNumber());
            assertNotNull(returnedFlightRoute);
        }
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
            for (int j = 0; j < flights.size(); j++) {
                assertEquals(emptyList, unavailableSeatInfoRepository.findAllByFlightId(flights.get(j).getFlightId()));
            }
        }
    }

    @Test
    @Transactional
    void updateFlight_ArrivalTime_Success() throws Exception {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(0));
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        FlightRoute newFlightRoute = new FlightRoute(flightRoute);
        int availableTicket = 80;

        // Arrival time is 00:00:00
        newFlightRoute.setArrivalTime(Time.valueOf("00:00:00"));
        expectedRoute.setArrivalTime(Time.valueOf("00:00:00"));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Arrival time is 23:59:00
        newFlightRoute.setArrivalTime(Time.valueOf("23:59:00"));
        expectedRoute.setArrivalTime(Time.valueOf("23:59:00"));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Arrival time is 23:59:00
        newFlightRoute.setArrivalTime(Time.valueOf("12:28:00"));
        expectedRoute.setArrivalTime(Time.valueOf("12:28:00"));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);
    }

    @Test
    @Transactional
    void updateFlight_ArrivalTime_Failed() {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(1));

        // Arrival time is null
        flightRoute.setArrivalTime(null);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("The arrival time should not be empty.", exception.getMessage());
    }

    @Test
    @Transactional
    void updateFlight_DepartureTime_Success() throws Exception {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(2));
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        FlightRoute newFlightRoute = new FlightRoute(flightRoute);
        int availableTicket = 80;

        // Departure time is 00:00:00
        newFlightRoute.setDepartureTime(Time.valueOf("00:00:00"));
        expectedRoute.setDepartureTime(Time.valueOf("00:00:00"));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Departure time is 23:59:00
        newFlightRoute.setDepartureTime(Time.valueOf("23:59:00"));
        expectedRoute.setDepartureTime(Time.valueOf("23:59:00"));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Departure time is 23:59:00
        newFlightRoute.setDepartureTime(Time.valueOf("12:28:00"));
        expectedRoute.setDepartureTime(Time.valueOf("12:28:00"));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);
    }

    @Test
    @Transactional
    void updateFlight_DepartureTime_Failed() {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(3));

        // Arrival time is null
        flightRoute.setDepartureTime(null);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("The departure time should not be empty.", exception.getMessage());
    }

    @Test
    @Transactional
    void updateFlight_DepartureCity_Success() throws Exception {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(4));
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        FlightRoute newFlightRoute = new FlightRoute(flightRoute);
        int availableTicket = 80;

        // Departure City's length is 1
        newFlightRoute.setDepartureCity("A");
        expectedRoute.setDepartureCity("A");
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Departure City's lenght is 255
        newFlightRoute.setDepartureCity("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        expectedRoute.setDepartureCity("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Departure City's lenght is random
        newFlightRoute.setDepartureCity("Montreal");
        expectedRoute.setDepartureCity("Montreal");
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);
    }

    @Test
    @Transactional
    void updateFlight_DepartureCity_Failed() {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(5));

        // Departure City is null
        flightRoute.setDepartureCity(null);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("The departure city should not be empty.", exception.getMessage());

        // Departure City's lenght is more than 255
        flightRoute.setDepartureCity("1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("The length of departure city cannot excess than 255.", exception.getMessage());
    }

    @Test
    @Transactional
    void updateFlight_DestinationCity_Success() throws Exception {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(6));
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        FlightRoute newFlightRoute = new FlightRoute(flightRoute);
        int availableTicket = 80;

        // Departure City's length is 1
        newFlightRoute.setDestinationCity("A");
        expectedRoute.setDestinationCity("A");
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Departure City's lenght is 255
        newFlightRoute.setDestinationCity("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        expectedRoute.setDestinationCity("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Departure City's lenght is random
        newFlightRoute.setDestinationCity("Montreal");
        expectedRoute.setDestinationCity("Montreal");
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);
    }

    @Test
    @Transactional
    void updateFlight_DestinationCity_Failed() {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(7));

        // Departure City is null
        flightRoute.setDestinationCity(null);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("The destination city should not be empty.", exception.getMessage());

        // Departure City's lenght is more than 255
        flightRoute.setDestinationCity("1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("The length of destination city cannot excess than 255.", exception.getMessage());
    }

    //TODO: Aircraft Success
    @Test
    @Transactional
    void updateFlight_Aircraft_Success() throws Exception {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(8));
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        FlightRoute newFlightRoute = new FlightRoute(flightRoute);
        int availableTicket = 80;

        // Aircraft CRJ200
        newFlightRoute.setAircraftId(200);
        expectedRoute.setAircraftId(200);
        availableTicket = 53;
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Aircraft Air bus 320
        newFlightRoute.setAircraftId(320);
        expectedRoute.setAircraftId(320);
        availableTicket = 154;
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Aircraft 737
        newFlightRoute.setAircraftId(737);
        expectedRoute.setAircraftId(737);
        availableTicket = 179;
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Aircraft 777
        newFlightRoute.setAircraftId(777);
        expectedRoute.setAircraftId(777);
        availableTicket = 424;
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Aircraft 900
        newFlightRoute.setAircraftId(900);
        expectedRoute.setAircraftId(900);
        availableTicket = 80;
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        //
    }

    @Test
    @Transactional
    void updateFlight_Aircraft_Failed() {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(9));

        // aircraft is null
        flightRoute.setAircraftId(null);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("Flight's aircraft cannot be empty.", exception.getMessage());

        // aircraft is 52
        flightRoute.setAircraftId(52);
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("Flight's aircraft is invalid.", exception.getMessage());
    }

    @Test
//    @Transactional
    void updateFlight_StartDate_Success() throws Exception {
        // The original start date is 0+80  end date is 0+100  (0 is today)
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(10));
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        FlightRoute newFlightRoute = new FlightRoute(flightRoute);
        int availableTicket = 80;

        // Move start 5 day before the original date
        newFlightRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 75));
        expectedRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 75));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Move start 3 day after the original date
        newFlightRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 83));
        expectedRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 83));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);
    }

    @Test
//    @Transactional
    void updateFlight_EndDate_Success() throws Exception {
        // The original start date is 0+80  end date is 0+100  (0 is today)
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(11));
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        FlightRoute newFlightRoute = new FlightRoute(flightRoute);
        int availableTicket = 80;

        // Move start 10 day after the original date
        newFlightRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 110));
        expectedRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 110));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Move start 7 day before the original date
        newFlightRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 93));
        expectedRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 93));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);
    }

    @Test
//    @Transactional
    void updateFlight_TravelDate_Success() throws Exception {
        // The original start date is 0+80  end date is 0+100  (0 is today)
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(12));
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        FlightRoute newFlightRoute = new FlightRoute(flightRoute);
        int availableTicket = 80;

        // Move start date 10 days before the original date and end date 3 days after the original date
        newFlightRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 70));
        newFlightRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 103));
        expectedRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 70));
        expectedRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 103));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Move start date 6 days before the current date and end date 4 days before the current date
        newFlightRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 64));
        newFlightRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 99));
        expectedRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 64));
        expectedRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 99));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Move start date 8 days after the current date and end date 8 days after the current date
        newFlightRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 72));
        newFlightRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 107));
        expectedRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 72));
        expectedRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 107));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // Move start date 10 days after the current date and end date 12 days before the current date
        newFlightRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 82));
        newFlightRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 95));
        expectedRoute.setStartDate(constants.datePlusSomeDays(constants.today(), 82));
        expectedRoute.setEndDate(constants.datePlusSomeDays(constants.today(), 95));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);
    }

    @Test
    @Transactional
    void updatFlight_TravelDate_Failed() {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(13));

        // Start date is null
        flightRoute.setStartDate(null);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // End date is null
        flightRoute.setEndDate(null);
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("Flight's range of travel date cannot be empty.", exception.getMessage());

        // Start date is yesterday
        flightRoute.setStartDate(constants.datePlusSomeDays(constants.today(), -1));
        flightRoute.setEndDate(constants.tomorrow());
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("The start of travel range should not before today.", exception.getMessage());

        // End date is before start date 1 day
        flightRoute.setStartDate(constants.datePlusSomeDays(constants.tomorrow(), 1));
        flightRoute.setEndDate(constants.tomorrow());
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("The end of travel range should not before the start of travel range.", exception.getMessage());

        // End date is before start date 20 days
        flightRoute.setStartDate(constants.datePlusSomeDays(constants.tomorrow(), 31));
        flightRoute.setEndDate(constants.datePlusSomeDays(constants.tomorrow(), 11));
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("The end of travel range should not before the start of travel range.", exception.getMessage());
    }


    @Test
    @Transactional
    void updateFlight_Overbooking_Success() throws Exception {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(14));
        FlightRoute newFlightRoute = new FlightRoute(flightRoute);
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        int availableTicket = 83;

        // overbooking allowance is 10%

        newFlightRoute.setOverbooking(BigDecimal.valueOf(10));
        expectedRoute.setOverbooking(BigDecimal.valueOf(10).setScale(2, RoundingMode.FLOOR));
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // overbooking allowance is 6.25%
        newFlightRoute.setOverbooking(BigDecimal.valueOf(6.25));
        expectedRoute.setOverbooking(BigDecimal.valueOf(6.25).setScale(2, RoundingMode.FLOOR));
        availableTicket = 80;
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // overbooking allowance is 6.24%
        newFlightRoute.setOverbooking(BigDecimal.valueOf(3.952));
        expectedRoute.setOverbooking(BigDecimal.valueOf(3.952).setScale(2, RoundingMode.FLOOR));
        availableTicket = 79;
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);

        // overbooking allowance is 0%
        newFlightRoute.setOverbooking(BigDecimal.valueOf(0));
        expectedRoute.setOverbooking(BigDecimal.valueOf(0).setScale(2, RoundingMode.FLOOR));
        availableTicket = 76;
        flightService.updateFlight(newFlightRoute);
        validFlightInfo(expectedRoute, flightRoute.getFlightNumber(), availableTicket, true);
    }

    @Test
    @Transactional
    void updateFlight_Overbooking_Failed() {
        // Get Flight Route from default list
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(defaultFlights.get(15));

        // Overbooking is null
        flightRoute.setOverbooking(null);
        ClientException exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("Flight's overbook allowance cannot be empty.", exception.getMessage());

        // Overbooking is 10.01
        flightRoute.setOverbooking(BigDecimal.valueOf(10.01));
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("Flight's overbooking allowance should between 0% to 10%", exception.getMessage());

        // Overbooking is -0.01
        flightRoute.setOverbooking(BigDecimal.valueOf(-0.01));
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(flightRoute));
        assertEquals("Flight's overbooking allowance should between 0% to 10%", exception.getMessage());
    }

    @Test
    void updateFlight_Complex_Success(){
        // Change some init available ticket before update.
        // Only the booked ticket less than the new available ticket number, update will be success

        // Init aircraft is CRJ900 which has 80 available tickets with the 6% overbooking

        //Get Flight Route from default list
        long flightNumber = defaultFlights.get(16);
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(flightNumber);
        List<Flight> flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        FlightRoute expectedRoute = new FlightRoute(flightRoute);

        // Test 1: Change one flight's available ticket as 1 and reduce overbooking to 5% (80 -> 79)
        int specialFlightIndex = 14;
        flights.get(specialFlightIndex).setAvailableTickets(1);
        flightRepository.saveAll(flights);
        HashMap<Long, Integer> expectedFlights = new HashMap<Long, Integer>();
        for (int i = 0; i < flights.size(); i ++){
            expectedFlights.put(flights.get(i).getFlightId(), flights.get(i).getAvailableTickets()-1);
        }
        FlightRoute newFlightRoute1 = new FlightRoute(flightRoute);
        newFlightRoute1.setOverbooking(BigDecimal.valueOf(5));
        expectedRoute.setOverbooking(BigDecimal.valueOf(5).setScale(2, RoundingMode.FLOOR));
        assertDoesNotThrow(()->flightService.updateFlight(newFlightRoute1));
        validFlightInfo(expectedRoute, flightNumber, 0, false);
        //Verify new available seat
        validFlights(expectedFlights, flightNumber,0,21);

        // Test 2: Change one flight's available ticket as 3 and reduce overbooking to 0% (79 -> 76)
        flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        flights.get(specialFlightIndex).setAvailableTickets(3);
        flightRepository.saveAll(flights);
        expectedFlights.clear();
        expectedFlights = new HashMap<Long, Integer>();
        for (int i = 0; i < flights.size(); i ++){
            expectedFlights.put(flights.get(i).getFlightId(), flights.get(i).getAvailableTickets()-3);
        }
        FlightRoute newFlightRoute2 = new FlightRoute(flightRoute);
        newFlightRoute2.setOverbooking(BigDecimal.valueOf(0));
        expectedRoute = new FlightRoute(flightRoute);
        expectedRoute.setOverbooking(BigDecimal.valueOf(0).setScale(2, RoundingMode.FLOOR));
        assertDoesNotThrow(()->flightService.updateFlight(newFlightRoute2));
        validFlightInfo(expectedRoute, flightNumber, 0, false);
        //Verify new available seat
        validFlights(expectedFlights, flightNumber, 0,21);

        // Test 3: Change flights' available ticket as 23, 24,25,
        // and set aircraft as CRJ200 reduce overbooking to 8% (76 -> 54)
        flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        flights.get(specialFlightIndex).setAvailableTickets(23);
        flights.get(specialFlightIndex+1).setAvailableTickets(24);
        flights.get(specialFlightIndex+2).setAvailableTickets(25);
        flightRepository.saveAll(flights);
        expectedFlights.clear();
        expectedFlights = new HashMap<Long, Integer>();
        for (int i = 0; i < flights.size(); i ++){
            expectedFlights.put(flights.get(i).getFlightId(), flights.get(i).getAvailableTickets()- 22);
        }
        FlightRoute newFlightRoute3 = new FlightRoute(flightRoute);
        newFlightRoute3.setOverbooking(BigDecimal.valueOf(8));
        newFlightRoute3.setAircraftId(200);
        expectedRoute = new FlightRoute(flightRoute);
        expectedRoute.setOverbooking(BigDecimal.valueOf(8).setScale(2, RoundingMode.FLOOR));
        expectedRoute.setAircraftId(200);
        assertDoesNotThrow(()->flightService.updateFlight(newFlightRoute3));
        validFlightInfo(expectedRoute, flightNumber, 0, false);
        //Verify new available seat
        validFlights(expectedFlights, flightNumber, 0,21);

        // Test 4: Change flights' available ticket as 10, 15, 28,
        // and set aircraft as 320 and reduce overbooking to 2% (54 -> 148)
        flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        flights.get(specialFlightIndex).setAvailableTickets(10);
        flights.get(specialFlightIndex+1).setAvailableTickets(15);
        flights.get(specialFlightIndex+2).setAvailableTickets(28);
        flightRepository.saveAll(flights);
        expectedFlights.clear();
        expectedFlights = new HashMap<Long, Integer>();
        for (int i = 0; i < flights.size(); i ++){
            expectedFlights.put(flights.get(i).getFlightId(), flights.get(i).getAvailableTickets()+94);
        }
        FlightRoute newFlightRoute4 = new FlightRoute(flightRoute);
        newFlightRoute4.setOverbooking(BigDecimal.valueOf(2));
        newFlightRoute4.setAircraftId(320);
        expectedRoute = new FlightRoute(flightRoute);
        expectedRoute.setOverbooking(BigDecimal.valueOf(2).setScale(2, RoundingMode.FLOOR));
        expectedRoute.setAircraftId(320);
        assertDoesNotThrow(()->flightService.updateFlight(newFlightRoute4));
        validFlightInfo(expectedRoute, flightNumber, 0, false);
        //Verify new available seat
        validFlights(expectedFlights, flightNumber, 0,21);

        // Test 5: Change flights' available ticket as 68, 69, 90,
        // and set aircraft as CRJ900 and reduce overbooking to 6% (148 -> 80)
        // and extend travel date
        flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        flights.get(specialFlightIndex).setAvailableTickets(68);
        flights.get(specialFlightIndex+1).setAvailableTickets(69);
        flights.get(specialFlightIndex+2).setAvailableTickets(90);
        flightRepository.saveAll(flights);
        expectedFlights.clear();
        expectedFlights = new HashMap<Long, Integer>();
        for (int i = 0; i < flights.size(); i ++){
            expectedFlights.put(flights.get(i).getFlightId(), flights.get(i).getAvailableTickets()-68);
        }
        FlightRoute newFlightRoute5 = new FlightRoute(flightRoute);
        newFlightRoute5.setOverbooking(BigDecimal.valueOf(6));
        newFlightRoute5.setAircraftId(900);
        newFlightRoute5.setStartDate(constants.datePlusSomeDays(newFlightRoute5.getStartDate(), -5));
        newFlightRoute5.setEndDate(constants.datePlusSomeDays(newFlightRoute5.getEndDate(), 10));
        expectedRoute = new FlightRoute(flightRoute);
        expectedRoute.setOverbooking(BigDecimal.valueOf(6).setScale(2, RoundingMode.FLOOR));
        expectedRoute.setAircraftId(900);
        expectedRoute.setStartDate(constants.datePlusSomeDays(expectedRoute.getStartDate(), -5));
        expectedRoute.setEndDate(constants.datePlusSomeDays(expectedRoute.getEndDate(), 10));
        assertDoesNotThrow(()->flightService.updateFlight(newFlightRoute5));
        validFlightInfo(expectedRoute, flightNumber, 0, false);
        //Verify new available seat
        validFlights(expectedFlights, flightNumber, 80,21+15);

        // Test 5: Change flights' available ticket as 0,51,46,
        // and set aircraft as 777 and reduce overbooking to 3% (80 -> 412)
        // and reduce travel date
        flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        flights.get(specialFlightIndex).setAvailableTickets(0);
        System.out.println(flights.get(specialFlightIndex).getFlightDate());
        flights.get(specialFlightIndex+1).setAvailableTickets(51);
        flights.get(specialFlightIndex+2).setAvailableTickets(46);
        flightRepository.saveAll(flights);
        expectedFlights.clear();
        expectedFlights = new HashMap<Long, Integer>();
        for (int i = 0; i < flights.size(); i ++){
            expectedFlights.put(flights.get(i).getFlightId(), flights.get(i).getAvailableTickets()+332);
        }
        FlightRoute newFlightRoute6 = new FlightRoute(flightRoute);
        newFlightRoute6.setOverbooking(BigDecimal.valueOf(3));
        newFlightRoute6.setAircraftId(777);
        newFlightRoute6.setStartDate(constants.datePlusSomeDays(newFlightRoute6.getStartDate(), 5));
        newFlightRoute6.setEndDate(constants.datePlusSomeDays(newFlightRoute6.getEndDate(), -3));
        expectedRoute = new FlightRoute(flightRoute);
        expectedRoute.setOverbooking(BigDecimal.valueOf(3).setScale(2, RoundingMode.FLOOR));
        expectedRoute.setAircraftId(777);
        expectedRoute.setStartDate(constants.datePlusSomeDays(expectedRoute.getStartDate(), 5));
        expectedRoute.setEndDate(constants.datePlusSomeDays(expectedRoute.getEndDate(), -3));
        assertDoesNotThrow(()->flightService.updateFlight(newFlightRoute6));
        validFlightInfo(expectedRoute, flightNumber, 0, false);
        //Verify new available seat
        validFlights(expectedFlights, flightNumber, 0,21-8);
    }

    @Test
    void updateFlight_Complex_Failed() {
        // If there is one or more flight's booked ticket is more than the available ticket of new aircraft,
        // Update flight will throw exception

        // Init aircraft is CRJ900 which has 80 available tickets with the 6% overbooking

        //Get Flight Route from default list
        long flightNumber = defaultFlights.get(17);
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(flightNumber);
        List<Flight> flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        // Change some flights' available ticket to 28 and lower
        flights.get(2).setAvailableTickets(30);
        flights.get(3).setAvailableTickets(29);
        flights.get(4).setAvailableTickets(27);
        flights.get(5).setAvailableTickets(26);
        flights.get(7).setAvailableTickets(23);
        flights.get(10).setAvailableTickets(25);
        flights.get(11).setAvailableTickets(4);
        flights.get(12).setAvailableTickets(3);
        flights.get(13).setAvailableTickets(2);
        flights.get(14).setAvailableTickets(1);
        flights.get(15).setAvailableTickets(0);
        flightRepository.saveAll(flights);

        // Test 1: Update the overbooking to 5% (available tickets 80 -> 79)
        FlightRoute newFlightRoute1 = new FlightRoute(flightRoute);
        newFlightRoute1.setOverbooking(BigDecimal.valueOf(5));
        ClientException exception = assertThrows(ClientException.class, () -> flightService.updateFlight(newFlightRoute1));
        String expectedMessage = "The flight " + flightNumber + " on these date: " +
                constants.datePlusSomeDays(constants.today(), 80 + 15) +
                " are not available for updating because the remaining available seats are not enough";
        assertEquals(expectedMessage, exception.getMessage());

        // Test 2: Update the overbooking to 3.9% (available tickets 80 -> 78)
        FlightRoute newFlightRoute2 = new FlightRoute(flightRoute);
        newFlightRoute2.setOverbooking(BigDecimal.valueOf(3.9));
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(newFlightRoute2));
        expectedMessage = "The flight " + flightNumber + " on these date: " +
                constants.datePlusSomeDays(constants.today(), 80 + 14) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 15) +
                " are not available for updating because the remaining available seats are not enough";
        assertEquals(expectedMessage, exception.getMessage());

        // Test 3: Update the overbooking to 2.5% (available tickets 80 -> 77)
        FlightRoute newFlightRoute3 = new FlightRoute(flightRoute);
        newFlightRoute3.setOverbooking(BigDecimal.valueOf(2.5));
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(newFlightRoute3));
        expectedMessage = "The flight " + flightNumber + " on these date: " +
                constants.datePlusSomeDays(constants.today(), 80 + 13) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 14) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 15) +
                " are not available for updating because the remaining available seats are not enough";
        assertEquals(expectedMessage, exception.getMessage());

        // Test 4: Update the overbooking to 0% (available tickets 80 -> 76)
        FlightRoute newFlightRoute4 = new FlightRoute(flightRoute);
        newFlightRoute4.setOverbooking(BigDecimal.valueOf(0));
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(newFlightRoute4));
        expectedMessage = "The flight " + flightNumber + " on these date: " +
                constants.datePlusSomeDays(constants.today(), 80 + 12) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 13) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 14) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 15) +
                " are not available for updating because the remaining available seats are not enough";
        assertEquals(expectedMessage, exception.getMessage());

        // Test 5: Change aircraft to CRJ200 (available tickets 80 -> 52)
        FlightRoute newFlightRoute5 = new FlightRoute(flightRoute);
        newFlightRoute5.setAircraftId(200);
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(newFlightRoute5));
        expectedMessage = "The flight " + flightNumber + " on these date: " +
                constants.datePlusSomeDays(constants.today(), 80 + 5) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 7) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 10) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 11) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 12) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 13) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 14) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 15) +
                " are not available for updating because the remaining available seats are not enough";
        assertEquals(expectedMessage, exception.getMessage());

        // Test 6: Change aircraft to CRJ200 and overbooking as 2.5% (available tickets 80 -> 51)
        FlightRoute newFlightRoute6 = new FlightRoute(flightRoute);
        newFlightRoute6.setAircraftId(200);
        newFlightRoute6.setOverbooking(BigDecimal.valueOf(2.5));
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(newFlightRoute6));
        expectedMessage = "The flight " + flightNumber + " on these date: " +
                constants.datePlusSomeDays(constants.today(), 80 + 4) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 5) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 7) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 10) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 11) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 12) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 13) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 14) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 15) +
                " are not available for updating because the remaining available seats are not enough";
        assertEquals(expectedMessage, exception.getMessage());

        // Test 7: Change aircraft to CRJ200 and overbooking as 2% (available tickets 80 -> 50)
        FlightRoute newFlightRoute7 = new FlightRoute(flightRoute);
        newFlightRoute7.setAircraftId(200);
        newFlightRoute7.setOverbooking(BigDecimal.valueOf(0));
        exception = assertThrows(ClientException.class, () -> flightService.updateFlight(newFlightRoute7));
        expectedMessage = "The flight " + flightNumber + " on these date: " +
                constants.datePlusSomeDays(constants.today(), 80 + 3) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 4) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 5) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 7) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 10) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 11) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 12) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 13) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 14) + ", " +
                constants.datePlusSomeDays(constants.today(), 80 + 15) +
                " are not available for updating because the remaining available seats are not enough";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
//    @Transactional
    void updateFlight_SeatReservation_Succsee() throws Exception {
        // Init aircraft is CRJ900 which has 80 available tickets with the 6% overbooking
        // After change the aircraft, all seat reservation will be cleared

        //Get Flight Route from default list
        long flightNumber = defaultFlights.get(18);
        FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(flightNumber);
        List<Flight> flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        // Manual setup the seat
        // TODO: Change to service layer method after implement book seat API
        List<UnavailableSeatInfo> seatReservations = new ArrayList<>();
        seatReservations.add(new UnavailableSeatInfo(flights.get(5).getFlightId(), 5, SeatStatus.BOOKED));
        seatReservations.add(new UnavailableSeatInfo(flights.get(5).getFlightId(), 6, SeatStatus.UNAVAILABLE));
        seatReservations.add(new UnavailableSeatInfo(flights.get(6).getFlightId(), 8, SeatStatus.UNAVAILABLE));
        seatReservations.add(new UnavailableSeatInfo(flights.get(7).getFlightId(), 1, SeatStatus.BOOKED));
        unavailableSeatInfoRepository.saveAll(seatReservations);
        // Verify Unavailable Seat Info Table
        List<UnavailableSeatInfo> returnedSeatReservation = unavailableSeatInfoRepository.findAllByFlightId(flights.get(5).getFlightId());
        returnedSeatReservation.addAll(unavailableSeatInfoRepository.findAllByFlightId(flights.get(6).getFlightId()));
        returnedSeatReservation.addAll(unavailableSeatInfoRepository.findAllByFlightId(flights.get(7).getFlightId()));
        assertEquals(4, returnedSeatReservation.size());
        // Default customer user book some flight and seat
        FlightRequest selectFlight1 = new FlightRequest(flights.get(5));
        FlightRequest selectFlight2 = new FlightRequest(flights.get(6));
        Ticket newTicket = ticketService.bookFlight(selectFlight1, jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(0)).getId());
        newTicket.setSeatNumber(5);
        ticketRepository.save(newTicket);
        ticketService.bookFlight(selectFlight1, jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(1)).getId());
        ticketService.bookFlight(selectFlight2, jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(0)).getId());
        FlightRequest selectFlight3 = new FlightRequest(flights.get(7));
        newTicket = ticketService.bookFlight(selectFlight3, jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(1)).getId());
        newTicket.setSeatNumber(1);
        ticketRepository.save(newTicket);

        // Update flight with new aircraft id
        flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        HashMap<Long, Integer> expectedFlights = new HashMap<Long, Integer>();
        for (int i = 0; i < flights.size(); i ++){
            expectedFlights.put(flights.get(i).getFlightId(), flights.get(i).getAvailableTickets()-27);
        }
        FlightRoute newFlightRoute1 = new FlightRoute(flightRoute);
        newFlightRoute1.setAircraftId(200);
        FlightRoute expectedRoute = new FlightRoute(flightRoute);
        expectedRoute.setAircraftId(200);
        assertDoesNotThrow(()->flightService.updateFlight(newFlightRoute1));
        validFlightInfo(expectedRoute, flightNumber, 0, false);
        //Verify new available seat
        validFlights(expectedFlights, flightNumber, 0,21);

        // Verify all seat reservation is cleared
        List<UnavailableSeatInfo> seatReservation;
        List<Ticket> bookedTicket;
        flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        for (int i = 0; i < flights.size(); i++){
            seatReservation = unavailableSeatInfoRepository.findAllByFlightId(flights.get(i).getFlightId());
            assertEquals(0, seatReservation.size());
            // Verify all ticket in this flight does not have a seat number
            bookedTicket = ticketRepository.findTicketsByFlightId(flights.get(i).getFlightId());

            for (int j = 0; j < bookedTicket.size(); j++){
                assertEquals(null, bookedTicket.get(j).getSeatNumber());
            }

        }

    }

    private void validFlightInfo(FlightRoute expectedFlightRoute, long actualFlightNumber, int availableTickets, boolean isVerifyFlights) {
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
        if (isVerifyFlights){
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
    
    private void validFlights(HashMap<Long, Integer> expectedFlights, long flightNumber,
                              int otherAvailableTickets, int flightAmount){
        List<Flight> returnedFlights = assertDoesNotThrow(() -> flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber));
        assertEquals(flightAmount, returnedFlights.size());
        for (int i = 0; i < returnedFlights.size(); i++){
            if (expectedFlights.get(returnedFlights.get(i).getFlightId()) != null ){
                assertEquals(expectedFlights.get(returnedFlights.get(i).getFlightId()),
                        returnedFlights.get(i).getAvailableTickets());
            }
            else {
                assertEquals(otherAvailableTickets, returnedFlights.get(i).getAvailableTickets());
            }

        }
    }

}
