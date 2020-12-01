package com.guanhong.airlinebookingsystem.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.*;
import com.guanhong.airlinebookingsystem.repository.*;
import com.guanhong.airlinebookingsystem.service.BatchService;
import com.guanhong.airlinebookingsystem.service.Constants;
import com.guanhong.airlinebookingsystem.service.FlightService;
import com.guanhong.airlinebookingsystem.service.JwtUserDetailsService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ConcurrentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRouteRepository flightRouteRepository;

    @Autowired
    private UnavailableSeatInfoRepository unavailableSeatInfoRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private FlightService flightService;



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
        int userAmount = 15;
        for (int i = 0; i < userAmount; i++) {
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
        }


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
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;
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
            for (int j = 0; j < flights.size(); j++) {
                assertEquals(emptyList, unavailableSeatInfoRepository.findAllByFlightId(flights.get(j).getFlightId()));
            }
        }
    }


    @Test
    void bookFlight_Concurrent_Success() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        int flightIndex = 1;
        int totalAvailableSeat = availableFlights.get(flightIndex).getAvailableTickets();

        String requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";

        // Set up builders
        List<RequestBuilder> builders = new ArrayList<>();
        for (int i = 0; i < defaultCustomerUsernames.size(); i++) {
            String jwt = getJWTByUsername(defaultCustomerUsernames.get(i), constants.CUSTOMER_USER_PASSWORD_0);
            RequestBuilder builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                    accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
            builders.add(builder);
        }

        // Start threads to book flight
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < defaultCustomerUsernames.size(); i++) {
            int finalI0 = i;
            int finalI1 = i;
            Thread thread = new Thread() {
                public void run() {
                    try {
                        System.out.println(this.getName() + ": Start thread " + finalI1);
                        MvcResult result = mockMvc.perform(builders.get(finalI0)).andReturn();
                        System.out.println(this.getName() + ": Finish thread " + finalI1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };


            thread.start();
            threadList.add(thread);
        }

        try {
            for (Thread thread : threadList) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify the left ticket + booked ticket is full amount

        int bookedTicketNum = ticketRepository.findTicketsByFlightId(availableFlights.get(flightIndex).getFlightId()).size();
        int newAvailableTicket = flightRepository.findFlightByFlightId(availableFlights.get(flightIndex).getFlightId()).getAvailableTickets();
        System.out.println("Booked Ticket Num: " + bookedTicketNum);
        System.out.println("New Available Ticket: " + newAvailableTicket);
        // The flight still available, so the bookedTicketNum has to greater than 0
        assertNotEquals(0, bookedTicketNum);
        assertEquals(totalAvailableSeat, bookedTicketNum + newAvailableTicket);
    }

    @Test
    void bookSeat_Concurrent_Same_Seat_Success() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        int flightIndex = 2;
        int totalAvailableSeat = availableFlights.get(flightIndex).getAvailableTickets();

        // Set up flightBuilders
        String requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";
        List<RequestBuilder> flightBuilders = new ArrayList<>();
        // Change ticket amount to change how many user try to book the same seat at the same time
        int ticketAmount = 2;
        for (int i = 0; i < ticketAmount; i++) {
            String jwt = getJWTByUsername(defaultCustomerUsernames.get(i), constants.CUSTOMER_USER_PASSWORD_0);
            RequestBuilder builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                    accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
            flightBuilders.add(builder);
        }

        // book flight
        for (int i = 0; i < flightBuilders.size(); i++) {
            MvcResult result = mockMvc.perform(flightBuilders.get(i)).andReturn();
            String content = result.getResponse().getContentAsString();
            String validJSON = "{\n" +
                    "    \"flightId\": " + availableFlights.get(flightIndex).getFlightId() + ",\n" +
                    "    \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate() + "\"\n" +
                    "}";
            JSONAssert.assertEquals(validJSON, content, JSONCompareMode.LENIENT);
        }

        // Verify the left ticket + booked ticket is full amount
        int bookedTicketNum = ticketRepository.findTicketsByFlightId(availableFlights.get(flightIndex).getFlightId()).size();
        int newAvailableTicket = flightRepository.findFlightByFlightId(availableFlights.get(flightIndex).getFlightId()).getAvailableTickets();
        System.out.println("Booked Ticket Num: " + bookedTicketNum);
        System.out.println("New Available Ticket: " + newAvailableTicket);
        assertEquals(totalAvailableSeat, bookedTicketNum + newAvailableTicket);

        // Set up seatBuilders
        List<RequestBuilder> seatBuilders = new ArrayList<>();
        for (int i = 0; i < ticketAmount; i++) {
            int selectSeatNumber = 1;
            requestJSON = "{\n" +
                    "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                    "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + ",\n" +
                    "  \"seatNumber\": " + selectSeatNumber + "\n" +
                    "}";
            String jwt = getJWTByUsername(defaultCustomerUsernames.get(i), constants.CUSTOMER_USER_PASSWORD_0);
            RequestBuilder builder = post("/api/bookSeat").header("Authorization", "Bearer " + jwt).
                    accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
            seatBuilders.add(builder);
            selectSeatNumber++;
        }

        // Start threads to book flight
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < seatBuilders.size(); i++) {
            int finalI0 = i;
            int finalI1 = i;
            Thread thread = new Thread() {
                public void run() {
                    try {
                        System.out.println(this.getName() + ": Start thread " + finalI1);
                        MvcResult result = mockMvc.perform(seatBuilders.get(finalI0)).andReturn();
                        System.out.println(this.getName() + ": Finish thread " + finalI1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
            threadList.add(thread);
        }

        try {
            for (Thread thread : threadList) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify seat reservation
        List<UnavailableSeatInfo> seatReservation = unavailableSeatInfoRepository.findAllByFlightId(availableFlights.get(flightIndex).getFlightId());
        assertEquals(1, seatReservation.size());
        // Verify Ticket
        List<Ticket> tickets = ticketRepository.findTicketsByFlightId(availableFlights.get(flightIndex).getFlightId());
        int seatAmount = 0;
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getSeatNumber() != null) {
                seatAmount++;
            }
        }
        assertEquals(1, seatAmount);
    }

    @Test
    void bookSeat_Concurrent_Different_Seat_Success() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        int flightIndex = 3;
        int totalAvailableSeat = availableFlights.get(flightIndex).getAvailableTickets();

        // Set up flightBuilders
        String requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";
        List<RequestBuilder> flightBuilders = new ArrayList<>();
        int ticketAmount = 10;
        for (int i = 0; i < ticketAmount; i++) {
            String jwt = getJWTByUsername(defaultCustomerUsernames.get(i), constants.CUSTOMER_USER_PASSWORD_0);
            RequestBuilder builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                    accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
            flightBuilders.add(builder);
        }

        // book flight
        for (int i = 0; i < flightBuilders.size(); i++) {
            MvcResult result = mockMvc.perform(flightBuilders.get(i)).andReturn();
            String content = result.getResponse().getContentAsString();
            String validJSON = "{\n" +
                    "    \"flightId\": " + availableFlights.get(flightIndex).getFlightId() + ",\n" +
                    "    \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate() + "\"\n" +
                    "}";
            JSONAssert.assertEquals(validJSON, content, JSONCompareMode.LENIENT);
        }

        // Verify the left ticket + booked ticket is full amount
        int bookedTicketNum = ticketRepository.findTicketsByFlightId(availableFlights.get(flightIndex).getFlightId()).size();
        int newAvailableTicket = flightRepository.findFlightByFlightId(availableFlights.get(flightIndex).getFlightId()).getAvailableTickets();
        System.out.println("Booked Ticket Num: " + bookedTicketNum);
        System.out.println("New Available Ticket: " + newAvailableTicket);
        assertEquals(totalAvailableSeat, bookedTicketNum + newAvailableTicket);

        // Set up seatBuilders
        List<RequestBuilder> seatBuilders = new ArrayList<>();
        for (int i = 0; i < ticketAmount; i++) {
            int selectSeatNumber = i + 1;
            requestJSON = "{\n" +
                    "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                    "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + ",\n" +
                    "  \"seatNumber\": " + selectSeatNumber + "\n" +
                    "}";
            String jwt = getJWTByUsername(defaultCustomerUsernames.get(i), constants.CUSTOMER_USER_PASSWORD_0);
            RequestBuilder builder = post("/api/bookSeat").header("Authorization", "Bearer " + jwt).
                    accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
            seatBuilders.add(builder);
            selectSeatNumber++;
        }

        // Start threads to book flight
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < seatBuilders.size(); i++) {
            int finalI0 = i;
            int finalI1 = i;
            Thread thread = new Thread() {
                public void run() {
                    try {
                        System.out.println(this.getName() + ": Start thread " + finalI1);
                        MvcResult result = mockMvc.perform(seatBuilders.get(finalI0)).andReturn();
                        System.out.println(this.getName() + ": Finish thread " + finalI1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
            threadList.add(thread);
        }

        try {
            for (Thread thread : threadList) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify seat reservation
        List<UnavailableSeatInfo> seatReservation = unavailableSeatInfoRepository.findAllByFlightId(availableFlights.get(flightIndex).getFlightId());
        assertEquals(ticketAmount, seatReservation.size());
        // Verify Ticket
        List<Ticket> tickets = ticketRepository.findTicketsByFlightId(availableFlights.get(flightIndex).getFlightId());
        int seatAmount = 0;
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getSeatNumber() != null) {
                seatAmount++;
            }
        }
        assertEquals(ticketAmount, seatAmount);
    }

    private String getJWTByUsername(String username, String password) {
        try {
            UserCredential user = new UserCredential(username, password);
            RequestBuilder builders = post("/authenticate").
                    accept(MediaType.APPLICATION_JSON).content(user.toJsonString()).
                    contentType(MediaType.APPLICATION_JSON);
            MvcResult jwtResult = mockMvc.perform(builders).andReturn();
            String jwtResultContent = jwtResult.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            UserLoginResponse jwt = mapper.readValue(jwtResultContent, UserLoginResponse.class);
            return jwt.getJwttoken();
        } catch (Exception e) {
            System.out.println("Class: Constants, Method: getJWTFromController: " + e.getMessage());
            assertFalse(true);
            return null;
        }
    }


    private void validFlightInfo(FlightRoute expectedFlightRoute, long actualFlightNumber, int availableTickets,
                                 boolean isSkipSeatList) {
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
        if (isSkipSeatList == false) {
            for (int i = 0; i < returnedFlights.size(); i++) {
                Flight flight = returnedFlights.get(i);
                assertEquals(expectedDate, flight.getFlightDate());
                assertEquals(availableTickets, flight.getAvailableTickets());
                expectedDate = constants.datePlusSomeDays(expectedDate, 1);
                // Verify Flight Seat Info
                List<UnavailableSeatInfo> unavailableSeatInfos = unavailableSeatInfoRepository.findAllByFlightId(flight.getFlightId());
                assertEquals(availableTickets, unavailableSeatInfos.size());
            }
        }

    }

    private boolean validFlightExistInList(List<FlightRoute> flightRouteList, long flightNumber) {
        for (int i = 0; i < flightRouteList.size(); i++) {
            if (flightRouteList.get(i).getFlightNumber() == flightNumber) {
                return true;
            }
        }
        return false;
    }

}
