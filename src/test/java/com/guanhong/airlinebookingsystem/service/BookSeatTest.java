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
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class BookSeatTest {
    @Autowired
    private FlightService flightService;

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
    void bookSeatTest_Success() throws Exception {
        // Let customer book the flight
        int flightRouteIndex = 0;
        int customerIndex = 0;
        int flightIndex = 8;

        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(flightRouteIndex));
        User customer = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(customerIndex));

        // Book the flight for today
        FlightRequest selectedFlight = new FlightRequest(availableFlights.get(flightIndex).getFlightNumber(), availableFlights.get(flightIndex).getFlightDate());
        Ticket originalTicket = assertDoesNotThrow(() -> ticketService.bookFlight(selectedFlight, customer.getId()));
        assertNull(originalTicket.getSeatNumber());

        // Test 1: Book the seat for the select flight (Does not book any seat)
        int selectSeatNumber = 17;
        Integer oldSeatNumber = null;
        BookSeatRequest bookSeatRequest1 = new BookSeatRequest(selectedFlight.getFlightNumber(),
                selectedFlight.getFlightDate(), selectSeatNumber);
        Ticket returnedTicket = assertDoesNotThrow(()->ticketService.bookSeat(bookSeatRequest1, customer.getId()));
        validTicket(originalTicket, returnedTicket.getTicketId(), selectSeatNumber);
        validSeatReservation(availableFlights.get(flightIndex).getFlightId(), selectSeatNumber, oldSeatNumber, SeatStatus.BOOKED);

        // Test 2: Change the current seat reservation
        originalTicket = ticketRepository.findTicketByCustomerIdAndFlightId(customer.getId(),
                availableFlights.get(flightIndex).getFlightId());
        assertEquals(selectSeatNumber, originalTicket.getSeatNumber());
        // Update new seatNumber
        oldSeatNumber = selectSeatNumber;
        selectSeatNumber = 12;
        BookSeatRequest bookSeatRequest2 = new BookSeatRequest(selectedFlight.getFlightNumber(),
                selectedFlight.getFlightDate(), selectSeatNumber);
        returnedTicket = assertDoesNotThrow(()->ticketService.bookSeat(bookSeatRequest2, customer.getId()));
        validTicket(originalTicket, returnedTicket.getTicketId(), selectSeatNumber);
        validSeatReservation(availableFlights.get(flightIndex).getFlightId(), selectSeatNumber, oldSeatNumber, SeatStatus.BOOKED);
    }

    @Test
    @Transactional
    void bookSeat_UnavailableSeat_Failed() throws Exception {
        // Let customer book the flight
        int flightRouteIndex = 0;
        int flightIndex = 1;

        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(flightRouteIndex));
        User customer1 = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(0));
        User customer2 = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(1));

        // Book the flight for today
        FlightRequest selectedFlight = new FlightRequest(availableFlights.get(flightIndex).getFlightNumber(), availableFlights.get(flightIndex).getFlightDate());
        Ticket originalTicket1 = assertDoesNotThrow(() -> ticketService.bookFlight(selectedFlight, customer1.getId()));
        assertNull(originalTicket1.getSeatNumber());
        Ticket originalTicket2 = assertDoesNotThrow(() -> ticketService.bookFlight(selectedFlight, customer2.getId()));
        assertNull(originalTicket2.getSeatNumber());

        // Test 1: selectSeat is booked
        // Customer 1 book the seat
        int selectSeatNumber = 10;
        Integer oldSeatNumber = null;
        BookSeatRequest bookSeatRequest1 = new BookSeatRequest(selectedFlight.getFlightNumber(),
                selectedFlight.getFlightDate(), selectSeatNumber);
        Ticket returnedTicket = assertDoesNotThrow(()->ticketService.bookSeat(bookSeatRequest1, customer1.getId()));
        validTicket(originalTicket1, returnedTicket.getTicketId(), selectSeatNumber);
        validSeatReservation(availableFlights.get(flightIndex).getFlightId(), selectSeatNumber, oldSeatNumber, SeatStatus.BOOKED);
        // Customer 2 book the same seat
        BookSeatRequest bookSeatRequest2 = new BookSeatRequest(selectedFlight.getFlightNumber(),
                selectedFlight.getFlightDate(), selectSeatNumber);
        ClientException exception = assertThrows(ClientException.class, ()-> ticketService.bookSeat(bookSeatRequest2, customer2.getId()));
        String expectedMessage = "The seat " + selectSeatNumber + " in the flight " + selectedFlight.getFlightNumber() + " is not available.";
        assertEquals(expectedMessage, exception.getMessage());
        // Verify the ticket does not update
        validTicket(originalTicket2, originalTicket2.getTicketId(), null);

        // Test 2: Select seat is unavailable
        selectSeatNumber = 15;
        // Update the selected seat as unavailable
        UnavailableSeatInfo unavailableSeatInfo = new UnavailableSeatInfo(availableFlights.get(flightIndex).getFlightId(), selectSeatNumber, SeatStatus.UNAVAILABLE);
        unavailableSeatInfoRepository.save(unavailableSeatInfo);
        UnavailableSeatInfo returnedSeatInfo = unavailableSeatInfoRepository.findUnavailableSeatInfoByFlightIdAndSeatNumber(availableFlights.get(flightIndex).getFlightId(),
                selectSeatNumber);
        assertEquals(SeatStatus.UNAVAILABLE, returnedSeatInfo.getSeatStatus());
        // Customer 1 book the select seat
        BookSeatRequest bookSeatRequest3 = new BookSeatRequest(selectedFlight.getFlightNumber(),
                selectedFlight.getFlightDate(), selectSeatNumber);
        exception = assertThrows(ClientException.class, ()-> ticketService.bookSeat(bookSeatRequest3, customer1.getId()));
        expectedMessage = "The seat " + selectSeatNumber + " in the flight " + selectedFlight.getFlightNumber() + " is not available.";
        assertEquals(expectedMessage, exception.getMessage());
        // Customer 2 book the select seat
        BookSeatRequest bookSeatRequest4 = new BookSeatRequest(selectedFlight.getFlightNumber(),
                selectedFlight.getFlightDate(), selectSeatNumber);
        exception = assertThrows(ClientException.class, ()-> ticketService.bookSeat(bookSeatRequest4, customer2.getId()));
        expectedMessage = "The seat " + selectSeatNumber + " in the flight " + selectedFlight.getFlightNumber() + " is not available.";
        assertEquals(expectedMessage, exception.getMessage());
        // Verify the ticket does not update
        validTicket(originalTicket2, originalTicket2.getTicketId(), null);

    }

    @Test
    @Transactional
    void bookSeat_UnreservedFlight_Failed() throws Exception {
        // Let customer book the flight
        int flightRouteIndex = 0;
        int customerIndex = 1;
        int flightIndex = 3;

        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(flightRouteIndex));
        User customer = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(customerIndex));

        // Book the flight for one day after index date
        FlightRequest selectedFlight = new FlightRequest(availableFlights.get(flightIndex+1).getFlightNumber(), availableFlights.get(flightIndex+1).getFlightDate());
        Ticket originalTicket1 = assertDoesNotThrow(() -> ticketService.bookFlight(selectedFlight, customer.getId()));
        assertNull(originalTicket1.getSeatNumber());

        // Test 1: The flight is not booked
        int selectSeatNumber = 10;
        BookSeatRequest bookSeatRequest = new BookSeatRequest(defaultFlights.get(flightRouteIndex),
                availableFlights.get(flightIndex).getFlightDate(), selectSeatNumber);
        ClientException exception = assertThrows(ClientException.class, ()-> ticketService.bookSeat(bookSeatRequest, customer.getId()));
        String expectedMessage = "You have to book the ticket before booking a seat.";
        assertEquals(expectedMessage, exception.getMessage());
        // Verify the ticket did not update
        Ticket returnedTicket = ticketRepository.findTicketByCustomerIdAndFlightId(customer.getId(),availableFlights.get(flightIndex).getFlightId());
        assertNull(returnedTicket);
        // Verify the seat reservation of select index flight is empty
        List<UnavailableSeatInfo> emptyList = new ArrayList<>();
        assertEquals(emptyList, unavailableSeatInfoRepository.findAllByFlightId(availableFlights.get(flightIndex).getFlightId()));

    }


    private void validTicket(Ticket originalTicket, long ticketId, Integer newSeatNumber){
        Ticket newTicket = ticketRepository.findTicketByTicketId(ticketId);
        assertEquals(originalTicket.getFlightId(), newTicket.getFlightId());
        assertEquals(originalTicket.getTicketId(), newTicket.getTicketId());
        assertEquals(originalTicket.getCustomerId(), newTicket.getCustomerId());
        assertTrue(originalTicket.getFlightDate().equals(newTicket.getFlightDate()));
        if (newSeatNumber != null){
            assertEquals(newSeatNumber, newTicket.getSeatNumber());
        }
        else {
            assertEquals(originalTicket.getSeatNumber(), newTicket.getSeatNumber());
        }
    }

    private void validSeatReservation(long flightId, int newSeatNumber, Integer oldSeatNumber,SeatStatus seatStatus){
        assertNull(unavailableSeatInfoRepository.findUnavailableSeatInfoByFlightIdAndSeatNumber(flightId, oldSeatNumber));
        UnavailableSeatInfo unavailableSeatInfo = unavailableSeatInfoRepository.
                findUnavailableSeatInfoByFlightIdAndSeatNumber(flightId, newSeatNumber);
        assertNotNull(unavailableSeatInfo);
        assertEquals(seatStatus, unavailableSeatInfo.getSeatStatus());
    }
}
