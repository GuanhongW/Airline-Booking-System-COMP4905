package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.BookSeatRequest;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.FlightRequest;
import com.guanhong.airlinebookingsystem.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CancelFlightRouteTest {
    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRouteRepository flightRouteRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    UnavailableSeatInfoRepository unavailableSeatInfoRepository;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private static Constants constants = Constants.getInstance();

    private static List<String> defaultAdminUsernames = new ArrayList<>();

    private static List<String> defaultCustomerUsernames = new ArrayList<>();

    private static List<Long> defaultFlights = new ArrayList<>();

    private static List<Long> defaultCustomerID = new ArrayList<>();


    @BeforeAll
    static void createDefaultAccount(@Autowired JwtUserDetailsService jwtUserDetailsService,
                                     @Autowired UserRepository userRepository,
                                     @Autowired CustomerInfoRepository customerInfoRepository,
                                     @Autowired FlightService flightService,
                                     @Autowired FlightRouteRepository flightRouteRepository,
                                     @Autowired TicketService ticketService) throws Exception {
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
        defaultCustomerID.add(res.getAccountId());
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
        defaultCustomerID.add(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

        // Create default flight
        int defaultFlightsNum = 10;
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

        // Customer book flights and seat
        FlightRequest selectFlight;
        for (int i = 0; i < defaultCustomerID.size(); i++) {
            selectFlight = new FlightRequest(defaultFlights.get(i + 1), constants.datePlusSomeDays(constants.today(), i + 80));
            Ticket returnedTicket = ticketService.bookFlight(selectFlight, defaultCustomerID.get(i));
            assertNotNull(returnedTicket);
            BookSeatRequest bookSeatRequest1 = new BookSeatRequest(selectFlight.getFlightNumber(),
                    selectFlight.getFlightDate(), i+2);
            returnedTicket = ticketService.bookSeat(bookSeatRequest1, defaultCustomerID.get(i));
            assertNotNull(returnedTicket);
        }
        System.out.println("Booked seats for ticket");
    }

    @AfterAll
    static void deleteDefaultAccount(@Autowired UserRepository userRepository,
                                     @Autowired CustomerInfoRepository customerInfoRepository,
                                     @Autowired FlightRouteRepository flightRouteRepository,
                                     @Autowired FlightRepository flightRepository,
                                     @Autowired UnavailableSeatInfoRepository unavailableSeatInfoRepository,
                                     @Autowired TicketRepository ticketRepository) throws Exception {

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
            List<UnavailableSeatInfo> emptySeatReservation = new ArrayList<>();
            List<Ticket> emptyTicket = new ArrayList<>();
            for (int j = 0; j < flights.size(); j++) {
                assertEquals(emptySeatReservation, unavailableSeatInfoRepository.findAllByFlightId(flights.get(j).getFlightId()));
                assertEquals(emptyTicket, ticketRepository.findTicketsByFlightId(flights.get(j).getFlightId()));
            }
        }
    }

    @Test
    @Transactional
    void cancelFlightRouteTest_Success() {
        long selectedFlightNumber = defaultFlights.get(0);

        // Cancel the selectFlightNumber
        List<Flight> flights = flightRepository.findAllByFlightNumberOrderByFlightDate(selectedFlightNumber);
        List<Long> flightIds = new ArrayList<>();
        for (int i = 0; i < flights.size(); i++) {
            flightIds.add(flights.get(i).getFlightId());
        }
        boolean isSuccess = assertDoesNotThrow(() -> flightService.cancelFlightRoute(selectedFlightNumber));
        assertTrue(isSuccess);
        verifyFlightRouteIsCanceled(selectedFlightNumber, flightIds);
    }

    @Test
    @Transactional
    void cancelFlightRouteTest_Failed() {

        // Cancel the non-existent flight
        List<Flight> flights = flightRepository.findAllByFlightNumberOrderByFlightDate(constants.NON_EXISTENT_FLIGHT_NUMBER);
        List<Long> flightIds = new ArrayList<>();
        for (int i = 0; i < flights.size(); i++) {
            flightIds.add(flights.get(i).getFlightId());
        }
        ClientException exception = assertThrows(ClientException.class,() -> flightService.cancelFlightRoute(constants.NON_EXISTENT_FLIGHT_NUMBER));
        String expectedMessage = "The flight route is unavailable in the system.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void verifyFlightRouteIsCanceled(long flightNumber, List<Long> flightIds) {
        // Verify Flight Route table
        assertNull(flightRouteRepository.findFlightByflightNumber(flightNumber));

        // Verify Flight table
        List<Flight> emptyFlights = new ArrayList<>();
        assertEquals(emptyFlights, flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber));

        // Verify unavailable Seat Info table and Ticket Table
        List<UnavailableSeatInfo> emptySeatReservation = new ArrayList<>();
        List<Ticket> emptyTicket = new ArrayList<>();
        for (int i = 0; i < flightIds.size(); i++) {
            assertEquals(emptySeatReservation, unavailableSeatInfoRepository.findAllByFlightId(flightIds.get(i)));
            assertEquals(emptyTicket, ticketRepository.findTicketsByFlightId(flightIds.get(i)));
        }

    }

}
