package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.FlightRequest;
import com.guanhong.airlinebookingsystem.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class BookFlightTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

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
        int aircraft = 200;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        Date startDate = constants.datePlusSomeDays(constants.today(), 0);
        Date endDate = constants.datePlusSomeDays(constants.today(), 10);
        Integer availableSeat = null;
        FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                aircraft, overbooking, startDate, endDate);
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
    void booFlight_Success() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        User customer = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(0));

        // Book the flight for today
        int flightIndex = 0;
        FlightRequest selectedFlight1 = new FlightRequest(availableFlights.get(flightIndex).getFlightNumber(), availableFlights.get(flightIndex).getFlightDate());
        int initAvailableSeats = availableFlights.get(flightIndex).getAvailableTickets();
        Ticket returnedTicked = assertDoesNotThrow(() -> ticketService.bookFlight(selectedFlight1, customer.getId()));
        assertNotNull(returnedTicked.getTicketId());
        assertNull(returnedTicked.getSeatNumber());
        assertEquals(customer.getId(), returnedTicked.getCustomerId());
        assertEquals(availableFlights.get(flightIndex).getFlightId(), returnedTicked.getFlightId());
        assertEquals(selectedFlight1.getFlightDate(), returnedTicked.getFlightDate());
        assertEquals(initAvailableSeats - 1, flightRepository.findFlightByFlightId(availableFlights.get(flightIndex).getFlightId()).getAvailableTickets());

        // Book the last flight date in this flight
        flightIndex = availableFlights.size()-1;
        FlightRequest selectedFlight2 = new FlightRequest(availableFlights.get(flightIndex).getFlightNumber(),
                availableFlights.get(flightIndex).getFlightDate());
        initAvailableSeats = availableFlights.get(flightIndex).getAvailableTickets();
        returnedTicked = assertDoesNotThrow(() -> ticketService.bookFlight(selectedFlight2, customer.getId()));
        assertNotNull(returnedTicked.getTicketId());
        assertNull(returnedTicked.getSeatNumber());
        assertEquals(customer.getId(), returnedTicked.getCustomerId());
        assertEquals(availableFlights.get(flightIndex).getFlightId(), returnedTicked.getFlightId());
        assertEquals(selectedFlight2.getFlightDate(), returnedTicked.getFlightDate());
        assertEquals(initAvailableSeats - 1,
                flightRepository.findFlightByFlightId(availableFlights.get(flightIndex).getFlightId()).getAvailableTickets());

        // Book the flight in the middle of travel date range
        flightIndex = 4;
        FlightRequest selectedFlight3 = new FlightRequest(availableFlights.get(flightIndex).getFlightNumber(),
                availableFlights.get(flightIndex).getFlightDate());
        initAvailableSeats = availableFlights.get(flightIndex).getAvailableTickets();
        returnedTicked = assertDoesNotThrow(() -> ticketService.bookFlight(selectedFlight3, customer.getId()));
        assertNotNull(returnedTicked.getTicketId());
        assertNull(returnedTicked.getSeatNumber());
        assertEquals(customer.getId(), returnedTicked.getCustomerId());
        assertEquals(availableFlights.get(flightIndex).getFlightId(), returnedTicked.getFlightId());
        assertEquals(selectedFlight3.getFlightDate(), returnedTicked.getFlightDate());
        assertEquals(initAvailableSeats - 1, flightRepository.findFlightByFlightId(availableFlights.get(flightIndex).getFlightId()).getAvailableTickets());
    }

    @Test
    @Transactional
    void booFlight_FlightDate_Failed() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        User customer = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(0));

        // Book the flight for yesterday
        FlightRequest selectedFlight1 = new FlightRequest(availableFlights.get(0).getFlightNumber(),
                constants.datePlusSomeDays(availableFlights.get(0).getFlightDate(), -1));
        ClientException exception = assertThrows(ClientException.class, () -> ticketService.bookFlight(selectedFlight1, customer.getId()));
        String expectedMessage = "Selected Flight is not exist in the system. Please check the flight number and flight date again.";
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(expectedMessage, exception.getMessage());

        // Book the flight for the date after last date
        FlightRequest selectedFlight2 = new FlightRequest(availableFlights.get(availableFlights.size() - 1).getFlightNumber(),
                constants.datePlusSomeDays(availableFlights.get(availableFlights.size() - 1).getFlightDate(), 1));
        exception = assertThrows(ClientException.class, () -> ticketService.bookFlight(selectedFlight2, customer.getId()));
        expectedMessage = "Selected Flight is not exist in the system. Please check the flight number and flight date again.";
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(expectedMessage, exception.getMessage());

        // Book the flight with unavailable date
        FlightRequest selectedFlight3 = new FlightRequest(availableFlights.get(availableFlights.size() - 1).getFlightNumber(),
                constants.datePlusSomeDays(availableFlights.get(availableFlights.size() - 1).getFlightDate(), 20));
        exception = assertThrows(ClientException.class, () -> ticketService.bookFlight(selectedFlight3, customer.getId()));
        expectedMessage = "Selected Flight is not exist in the system. Please check the flight number and flight date again.";
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(expectedMessage, exception.getMessage());

        // Book the flight with unavailable date
        FlightRequest selectedFlight4 = new FlightRequest(availableFlights.get(0).getFlightNumber(),
                constants.datePlusSomeDays(availableFlights.get(0).getFlightDate(), -20));
        exception = assertThrows(ClientException.class, () -> ticketService.bookFlight(selectedFlight4, customer.getId()));
        expectedMessage = "Selected Flight is not exist in the system. Please check the flight number and flight date again.";
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Transactional
    void booFlight_FlightNumber_Failed() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        User customer = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(0));

        // Flight number is 0
        FlightRequest selectedFlight1 = new FlightRequest((long) 0, availableFlights.get(0).getFlightDate());
        ClientException exception = assertThrows(ClientException.class, () -> ticketService.bookFlight(selectedFlight1, customer.getId()));
        String expectedMessage = "Selected Flight is not exist in the system. Please check the flight number and flight date again.";
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(expectedMessage, exception.getMessage());

        // Flight number is 10000
        FlightRequest selectedFlight2 = new FlightRequest((long) 10000, availableFlights.get(0).getFlightDate());
        exception = assertThrows(ClientException.class, () -> ticketService.bookFlight(selectedFlight2, customer.getId()));
        expectedMessage = "Selected Flight is not exist in the system. Please check the flight number and flight date again.";
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(expectedMessage, exception.getMessage());

        // Flight number is 10000
        FlightRequest selectedFlight3 = new FlightRequest((long) 9748, availableFlights.get(0).getFlightDate());
        exception = assertThrows(ClientException.class, () -> ticketService.bookFlight(selectedFlight3, customer.getId()));
        expectedMessage = "Selected Flight is not exist in the system. Please check the flight number and flight date again.";
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Transactional
    void booFlight_AvailableSeat_Failed() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        User customer = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(0));

        // Change one flight's available seat as 0
        int flightIndex = 2;
        Flight targetFlight = availableFlights.get(flightIndex);
        targetFlight.setAvailableTickets(0);
        assertDoesNotThrow(() -> flightRepository.save(targetFlight));
        assertEquals(0, flightRepository.findFlightByFlightId(availableFlights.get(flightIndex).getFlightId()).getAvailableTickets());

        // Book the flight without any available seat

        FlightRequest selectedFlight1 = new FlightRequest(targetFlight.getFlightNumber(), targetFlight.getFlightDate());
        Ticket returnedTicket = assertDoesNotThrow(() -> ticketService.bookFlight(selectedFlight1, customer.getId()));
        int fullFlightTicketId = 0;
        assertEquals(fullFlightTicketId, returnedTicket.getTicketId());
        assertNull(returnedTicket.getFlightId());
        assertNull(returnedTicket.getSeatNumber());
        assertNull(returnedTicket.getFlightDate());
        assertNull(returnedTicket.getCustomerId());
        assertEquals(0, flightRepository.findFlightByFlightId(availableFlights.get(flightIndex).getFlightId()).getAvailableTickets());



    }

}
