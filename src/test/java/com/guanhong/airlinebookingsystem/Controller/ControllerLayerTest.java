package com.guanhong.airlinebookingsystem.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.*;
import com.guanhong.airlinebookingsystem.repository.*;
import com.guanhong.airlinebookingsystem.service.Constants;
import com.guanhong.airlinebookingsystem.service.FlightService;
import com.guanhong.airlinebookingsystem.service.JwtUserDetailsService;
import com.guanhong.airlinebookingsystem.service.TicketService;
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

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerLayerTest {

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
    private FlightService flightService;

    private static Constants constants = Constants.getInstance();

    private static List<String> defaultAdminUsernames = new ArrayList<>();

    private static List<String> defaultCustomerUsernames = new ArrayList<>();

    private static List<Long> defaultFlights = new ArrayList<>();

    private static List<Long> defaultCustomerID = new ArrayList<>();

    private static int defaultFlightAmount = 10;

    private static int cancelFlightRouteIndex = 6;

    private static int cancelFlightIndex = 7;

    private static int cancelTicketIndex = 8;

    private static Date bookedFlightDate = constants.datePlusSomeDays(constants.today(), 90);


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
        for (int i = 0; i < defaultFlightAmount; i++) {
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
        Flight selectFlight;
        // This index should be same as the cancelFlightRouteTest_Controller_Success
        for (int i = 0; i < defaultCustomerID.size(); i++) {
            selectFlight = new Flight(defaultFlights.get(cancelFlightRouteIndex), bookedFlightDate);
            Ticket returnedTicket = ticketService.bookFlight(new FlightRequest(selectFlight), defaultCustomerID.get(i));
            assertNotNull(returnedTicket);
            BookSeatRequest bookSeatRequest1 = new BookSeatRequest(selectFlight.getFlightNumber(),
                    selectFlight.getFlightDate(), i+2);
            returnedTicket = ticketService.bookSeat(bookSeatRequest1, defaultCustomerID.get(i));
            assertNotNull(returnedTicket);
        }
        // This index should be same as the cancelFlightTest_Controller_Success
        for (int i = 0; i < defaultCustomerID.size(); i++) {
            selectFlight = new Flight(defaultFlights.get(cancelFlightIndex), bookedFlightDate);
            Ticket returnedTicket = ticketService.bookFlight(new FlightRequest(selectFlight), defaultCustomerID.get(i));
            assertNotNull(returnedTicket);
            BookSeatRequest bookSeatRequest1 = new BookSeatRequest(selectFlight.getFlightNumber(),
                    selectFlight.getFlightDate(), i+2);
            returnedTicket = ticketService.bookSeat(bookSeatRequest1, defaultCustomerID.get(i));
            assertNotNull(returnedTicket);
        }
        System.out.println("Booked seats for ticket");

        // This index should be same as the cancelFlightTest_Controller_Success
        for (int i = 0; i < defaultCustomerID.size(); i++) {
            selectFlight = new Flight(defaultFlights.get(cancelTicketIndex), bookedFlightDate);
            Ticket returnedTicket = ticketService.bookFlight(new FlightRequest(selectFlight), defaultCustomerID.get(i));
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
            List<UnavailableSeatInfo> emptyList = new ArrayList<>();
            List<Ticket> emptyTicket = new ArrayList<>();
            for (int j = 0; j < flights.size(); j++){
                assertEquals(emptyList, unavailableSeatInfoRepository.findAllByFlightId(flights.get(j).getFlightId()));
                assertEquals(emptyTicket, ticketRepository.findTicketsByFlightId(flights.get(j).getFlightId()));
            }
        }
    }

    @Test
    @Transactional
    void registerTest_Controller_Success() throws Exception {
        String requestJSON = "{\n" +
                "  \"password\": \"adminadmin\",\n" +
                "  \"role\": \"ADMIN\",\n" +
                "  \"username\": \"admincontrollertest\"\n" +
                "}";
        RequestBuilder builder = post("/register").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String resultContent = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        CreateUserResponse createUserResponse = mapper.readValue(resultContent, CreateUserResponse.class);
        assertEquals("admincontrollertest", createUserResponse.getUsername());

        requestJSON = "{\n" +
                "  \"birthDate\": \"1985-04-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"name\": \"Controller Test\",\n" +
                "  \"password\": \"useruser\",\n" +
                "  \"role\": \"USER\",\n" +
                "  \"username\": \"usercontrollertest@carleton.ca\"\n" +
                "}";
        builder = post("/register").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        resultContent = result.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        createUserResponse = mapper.readValue(resultContent, CreateUserResponse.class);
        assertEquals("usercontrollertest@carleton.ca", createUserResponse.getUsername());
    }

    @Test
    @Transactional
    void registerTest_Controller_Failed() throws Exception {
        // RequestJSON is null
        RequestBuilder builder = post("/register").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        String expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // username is null
        String requestJSON = "{\n" +
                "  \"password\": \"string\",\n" +
                "  \"role\": \"ADMIN\"\n" +
                "}";
        builder = post("/register").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Username, password, or role cannot be empty";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // password is null
        requestJSON = "{\n" +
                "  \"role\": \"ADMIN\",\n" +
                "  \"username\": \"string\"\n" +
                "}";
        builder = post("/register").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Username, password, or role cannot be empty";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // role is null
        requestJSON = "{\n" +
                "  \"password\": \"string\",\n" +
                "  \"username\": \"string\"\n" +
                "}";
        builder = post("/register").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Username, password, or role cannot be empty";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());
    }

    @Test
    void authenticateTest_Controller_Success() throws Exception {
        // Login in with admin user
        String requestJSON = "{\n" +
                "  \"password\": \"" + constants.ADMIN_USER_PASSWORD_0 + "\",\n" +
                "  \"username\": \"" + defaultAdminUsernames.get(0) + "\"\n" +
                "}";
        RequestBuilder builder = post("/authenticate").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String resultContent = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        UserLoginResponse userLoginResponse = mapper.readValue(resultContent, UserLoginResponse.class);
        assertEquals(defaultAdminUsernames.get(0), userLoginResponse.getUsername());
        assertNotNull(userLoginResponse.getAccountId());
        assertNotNull(userLoginResponse.getJwttoken());

        // Login in with customer user
        requestJSON = "{\n" +
                "  \"password\": \"" + constants.CUSTOMER_USER_PASSWORD_0 + "\",\n" +
                "  \"username\": \"" + defaultCustomerUsernames.get(0) + "\"\n" +
                "}";
        builder = post("/authenticate").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        resultContent = result.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        userLoginResponse = mapper.readValue(resultContent, UserLoginResponse.class);
        assertEquals(defaultCustomerUsernames.get(0), userLoginResponse.getUsername());
        assertNotNull(userLoginResponse.getAccountId());
        assertNotNull(userLoginResponse.getJwttoken());
    }

    @Test
    void authenticateTest_Controller_Failed() throws Exception {
        // missed password
        String requestJSON = "{\n" +
                "  \"username\": \"autoadmin1\"\n" +
                "}";
        RequestBuilder builder = post("/authenticate").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        String resultContent = result.getResponse().getContentAsString();
        String expectedJSON = "Username or password cannot be empty.";
        assertEquals(expectedJSON, resultContent);

        // missed username
        requestJSON = "{\n" +
                "  \"password\": \"useruser1\"\n" +
                "}";
        builder = post("/authenticate").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        resultContent = result.getResponse().getContentAsString();
        expectedJSON = "Username or password cannot be empty.";
        assertEquals(expectedJSON, resultContent);
    }

    @Test
    @Transactional
    void createNewFlightTest_Controller_Success() throws Exception {
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        String requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"aircraftId\": 200,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"" + constants.datePlusSomeDays(constants.today(),50) + "\",\n" +
                "\t\"flightNumber\": " + constants.FLIGHT_NUMBER_9995 + ",\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"" + constants.datePlusSomeDays(constants.today(),30) + "\"\n" +
                "}";
        System.out.println(requestJSON);
        RequestBuilder builder = post("/api/createFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String expectedJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"aircraftId\": 200,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"" + constants.datePlusSomeDays(constants.today(),50) + "\",\n" +
                "\t\"flightNumber\": " + constants.FLIGHT_NUMBER_9995 + ",\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"" + constants.datePlusSomeDays(constants.today(),30) + "\"\n" +
                "}";
        JSONAssert.assertEquals(expectedJSON, result.getResponse().getContentAsString(), false);
    }

    @Test
    void createNewFlightTest_Controller_Failed() throws Exception {
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);

        // input newFlight is null
        String requestJSON = null;
        RequestBuilder builder = post("/api/createFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        String expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // start date format is not valid
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2022-09-04\",\n" +
                "\t\"flightNumber\": 9995,\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021\"\n" +
                "}";
        builder = post("/api/createFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // end date format is not valid
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"09-04\",\n" +
                "\t\"flightNumber\": 9995,\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-05-04\"\n" +
                "}";
        builder = post("/api/createFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // departureTime format is not valid
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2021-09-04\",\n" +
                "\t\"flightNumber\": 9995,\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-05-04\"\n" +
                "}";
        builder = post("/api/createFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // arrival time format is not valid
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2021-09-04\",\n" +
                "\t\"flightNumber\": 9995,\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-05-04\"\n" +
                "}";
        builder = post("/api/createFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // non-admin user try to create flights
        jwt = getJWTByUsername(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2022-09-04\",\n" +
                "\t\"flightNumber\": " + constants.FLIGHT_NUMBER_9995 + ",\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-04-06\"\n" +
                "}";
        builder = post("/api/createFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(401, result.getResponse().getStatus());
        expectedMessage = "Only admin user can create new flights.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());
    }


    @Test
    @Transactional
    void getAvailableFlightRoutesTest_Controller_Success() throws Exception {
        // TODO: Update flight by API without like now use repositity.save()
        long flightNumber = constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 200;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        Date startDate = constants.datePlusSomeDays(constants.today(), 5);
        Date endDate = constants.datePlusSomeDays(constants.today(), 35);


        // Create a flight the end date is before
        flightNumber = constants.FLIGHT_NUMBER_EXPIRED;

        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute returnedFlightRoute = assertDoesNotThrow(() -> flightService.createNewFlight(newFlightRoute1));
        newFlightRoute1.setStartDate(constants.datePlusSomeDays(constants.today(), -100));
        newFlightRoute1.setEndDate(constants.datePlusSomeDays(constants.today(), 0));
        assertDoesNotThrow(() -> flightRouteRepository.save(newFlightRoute1));
        validFlightInfo(newFlightRoute1, flightNumber, 156, true);

        // Create a flight router both start date and end date are after today
        flightNumber = constants.FLIGHT_NUMBER_START_DATE_EXPIRED;

        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        returnedFlightRoute = assertDoesNotThrow(() -> flightService.createNewFlight(newFlightRoute2));
        newFlightRoute2.setStartDate(constants.datePlusSomeDays(constants.today(), -5));
        newFlightRoute2.setEndDate(constants.datePlusSomeDays(constants.today(), 8));
        assertDoesNotThrow(() -> flightRouteRepository.save(newFlightRoute2));
        validFlightInfo(newFlightRoute2, flightNumber, 156, true);

        // Create a flight router both start date and end date are after today
        flightNumber = constants.FLIGHT_NUMBER_AVAILABLE;

        FlightRoute newFlightRoute3 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        returnedFlightRoute = assertDoesNotThrow(() -> flightService.createNewFlight(newFlightRoute3));
        newFlightRoute3.setStartDate(constants.datePlusSomeDays(constants.today(), 0));
        newFlightRoute3.setEndDate(constants.datePlusSomeDays(constants.today(), 8));
        assertDoesNotThrow(() -> flightRouteRepository.save(newFlightRoute3));
        validFlightInfo(newFlightRoute3, flightNumber, 156, true);

        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = get("/api/getFlightRoutes").header("Authorization", "Bearer " + jwt);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<FlightRoute> flightRoutes = mapper.readValue(content, new TypeReference<List<FlightRoute>>() {
        });

        assertFalse(validFlightExistInList(flightRoutes, constants.FLIGHT_NUMBER_EXPIRED));
        assertFalse(validFlightExistInList(flightRoutes, constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT));
        assertTrue(validFlightExistInList(flightRoutes, constants.FLIGHT_NUMBER_START_DATE_EXPIRED));
    }

    @Test
    @Transactional
    void getAllAvailableFlightsByFlightNumber_Controller_Success() throws Exception {
        long flightNumber = constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 200;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        Date startDate = constants.datePlusSomeDays(constants.today(), 0);
        Date endDate = constants.datePlusSomeDays(constants.today(), 5);
        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                capacity, overbooking, startDate, endDate);
        FlightRoute returnedFlightRoute = assertDoesNotThrow(() -> flightService.createNewFlight(newFlightRoute1));
        FlightRoute validFlightRoute = new FlightRoute(newFlightRoute1);
        validFlightInfo(validFlightRoute, flightNumber, 53, false);


        List<Flight> flights = flightRepository.findAllByFlightNumberOrderByFlightDate(flightNumber);
        // Update one flight's available seats as 0
        flights.get(1).setAvailableTickets(0);
        flightRepository.save(flights.get(1));
        assertEquals(0, flightRepository.findFlightByFlightId(flights.get(1).getFlightId()).getAvailableTickets());
        // Update one flight's start date is yesterday
        flights.get(3).setFlightDate(constants.datePlusSomeDays(constants.today(), -1));
        flightRepository.save(flights.get(3));
        assertEquals(constants.datePlusSomeDays(constants.today(), -1), flightRepository.findFlightByFlightId(flights.get(3).getFlightId()).getFlightDate());

        // Verify the JSON response
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = get("/api/getFlightsByFlightNumber").header("Authorization", "Bearer " + jwt).
                param("flightNumber", String.valueOf(flightNumber));
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        String validJSON = "[{\n" +
                "\t\"flightNumber\": 9998,\n" +
                "\t\"availableTickets\": 53\n" +
                "}, {\n" +
                "\t\"flightNumber\": 9998,\n" +
                "\t\"availableTickets\": 53\n" +
                "}, {\n" +
                "\t\"flightNumber\": 9998,\n" +
                "\t\"availableTickets\": 53\n" +
                "}, {\n" +
                "\t\"flightNumber\": 9998,\n" +
                "\t\"availableTickets\": 53\n" +
                "}]";
        JSONAssert.assertEquals(validJSON, content, JSONCompareMode.LENIENT);
    }

    @Test
    @Transactional
    void getFlightRoute_Controller_Success() throws Exception {
        long flightNumber = defaultFlights.get(0);

        // Verify the JSON response
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = get("/api/getFlightRoute").header("Authorization", "Bearer " + jwt).
                param("flightNumber", String.valueOf(flightNumber));
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        String validJSON = "{\n" +
                "\t\"arrivalTime\": \"12:00:00\",\n" +
                "\t\"aircraftId\": 900,\n" +
                "\t\"departureCity\": \"YYZ\",\n" +
                "\t\"departureTime\": \"10:05:00\",\n" +
                "\t\"destinationCity\": \"YVR\",\n" +
                "\t\"endDate\": \"" + constants.datePlusSomeDays(constants.today(),100) + "\",\n" +
                "\t\"flightNumber\": " + flightNumber + ",\n" +
                "\t\"overbooking\": 6.0,\n" +
                "\t\"startDate\": \"" + constants.datePlusSomeDays(constants.today(),80) + "\"\n" +
                "}";
        JSONAssert.assertEquals(validJSON, content, JSONCompareMode.LENIENT);
    }

    @Test
    @Transactional
    void bookFlight_Controller_Success() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        int flightIndex = 3;

        String requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";
        String jwt = getJWTByUsername(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        User customer = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(0));
        String validJSON = "{\n" +
                "    \"customerId\": "+ customer.getId() +",\n" +
                "    \"flightId\": " + availableFlights.get(flightIndex).getFlightId() + ",\n" +
                "    \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + ",\n" +
                "    \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate() + "\"\n" +
                "}";
        JSONAssert.assertEquals(validJSON, content, JSONCompareMode.LENIENT);

    }

    @Test
    @Transactional
    void bookFlight_Controller_Failed() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        int flightIndex = 3;

        // Admin user tries to book the flight
        String requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals(401, result.getResponse().getStatus());
        String expectedMessage = "Only customer user can book new flights.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // request json is null
        jwt = getJWTByUsername(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight number is null
        String requestJSON1 = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\"\n" +
                "}";
        RequestBuilder builder1 = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON1).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder1).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "flight number or flight date is empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight date is null
        requestJSON = "{\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";
        builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "flight number or flight date is empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight number is invalid
        requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": 0\n" +
                "}";
        builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Selected Flight is not exist in the system. Please check the flight number and flight date again.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

    }

    @Test
    @Transactional
    void updateFlightTest_Controller_Success() throws Exception {
        long flightNumber = defaultFlights.get(1);
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        String requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:00:00\",\n" +
                "\t\"aircraftId\": 737,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"12:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"" + constants.datePlusSomeDays(constants.today(),90) + "\",\n" +
                "\t\"flightNumber\": " + flightNumber + ",\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"" + constants.datePlusSomeDays(constants.today(),70) + "\"\n" +
                "}";
        System.out.println(requestJSON);
        RequestBuilder builder = post("/api/updateFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String expectedJSON = "{\n" +
                "\t\"arrivalTime\": \"13:00:00\",\n" +
                "\t\"aircraftId\": 737,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"12:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"" + constants.datePlusSomeDays(constants.today(),90) + "\",\n" +
                "\t\"flightNumber\": " + flightNumber + ",\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"" + constants.datePlusSomeDays(constants.today(),70) + "\"\n" +
                "}";
        JSONAssert.assertEquals(expectedJSON, result.getResponse().getContentAsString(), false);
    }



    @Test
    @Transactional
    void updateFlight_Controller_Failed() throws Exception {
        // Get all flight by Default flight number
        long flightNumber = defaultFlights.get(1);
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(flightNumber);
        int flightIndex = 3;

        // Customer user tries to updateFlight
        String requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";
        String jwt = getJWTByUsername(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/updateFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals(401, result.getResponse().getStatus());
        String expectedMessage = "Only admin user can update new flights.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // request json is null
        jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        builder = post("/api/updateFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // start date format is not valid
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2022-09-04\",\n" +
                "\t\"flightNumber\": 9995,\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021\"\n" +
                "}";
        builder = post("/api/updateFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // end date format is not valid
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"09-04\",\n" +
                "\t\"flightNumber\": 9995,\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-05-04\"\n" +
                "}";
        builder = post("/api/updateFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // departureTime format is not valid
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2021-09-04\",\n" +
                "\t\"flightNumber\": 9995,\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-05-04\"\n" +
                "}";
        builder = post("/api/updateFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // arrival time format is not valid
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2021-09-04\",\n" +
                "\t\"flightNumber\": 9995,\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-05-04\"\n" +
                "}";
        builder = post("/api/updateFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight number is null (Auto convert to 0)
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"15:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2021-09-04\",\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-05-04\"\n" +
                "}";
        builder = post("/api/updateFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Flight number is empty or invalid.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    void bookSeat_Controller_Success() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        int flightIndex = 4;
        int customerIndex = 0;

        // Set up flightBuilders
        String requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";
        String jwt = getJWTByUsername(defaultCustomerUsernames.get(customerIndex), constants.CUSTOMER_USER_PASSWORD_0);
        RequestBuilder flightBuilder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        // Book flight
        MvcResult result = mockMvc.perform(flightBuilder).andReturn();
        String content = result.getResponse().getContentAsString();
        String validJSON = "{\n" +
                "    \"flightId\": " + availableFlights.get(flightIndex).getFlightId() + ",\n" +
                "    \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate() + "\"\n" +
                "}";
        JSONAssert.assertEquals(validJSON, content, JSONCompareMode.LENIENT);

        // Set up seatBuilder
        int selectSeatNumber = 11;
        requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + ",\n" +
                "  \"seatNumber\": " + selectSeatNumber + "\n" +
                "}";
        RequestBuilder seatBuilder = post("/api/bookSeat").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(seatBuilder).andReturn();
        content = result.getResponse().getContentAsString();
        validJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightId\": " + availableFlights.get(flightIndex).getFlightId() + ",\n" +
                "  \"seatNumber\": " + selectSeatNumber + "\n" +
                "}";
        JSONAssert.assertEquals(validJSON, content, JSONCompareMode.LENIENT);


    }

    @Test
    @Transactional
    void bookSeat_Controller_Failed() throws Exception {
        // Get all flight by Default flight number
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        int flightIndex = 5;

        // Admin user tries to book the flight
        int selectSeatNumber = 1;
        String requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + ",\n" +
                "  \"seatNumber\": " + selectSeatNumber + "\n" +
                "}";
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/bookSeat").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals(401, result.getResponse().getStatus());
        String expectedMessage = "Only customer user can book new flights.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // request json is null
        jwt = getJWTByUsername(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight number is null
        String requestJSON1 = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\"\n" +
                "}";
        RequestBuilder builder1 = post("/api/bookSeat").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON1).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder1).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Flight number or flight date is empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight date is null
        requestJSON = "{\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + ",\n" +
                "  \"seatNumber\": " + selectSeatNumber + "\n" +
                "}";
        builder = post("/api/bookSeat").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Flight number or flight date is empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight number is null
        requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"seatNumber\": " + selectSeatNumber + "\n" +
                "}";
        builder = post("/api/bookSeat").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Flight number or flight date is empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Seat number is null
        requestJSON = "{\n" +
                "  \"flightDate\": \"" + availableFlights.get(flightIndex).getFlightDate().toString() + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";
        builder = post("/api/bookSeat").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        content = result.getResponse().getContentAsString();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Seat number is empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    void cancelFlightRouteTest_Controller_Success() throws Exception {
        long flightNumber = defaultFlights.get(cancelFlightRouteIndex);
        String requestJSON = "{\n" +
                "  \"flightNumber\": " + flightNumber + "\n" +
                "}";
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/cancelFlightRoute").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("true", content);
    }

    @Test
    @Transactional
    void cancelFlightRouteTest_Controller_Failed() throws Exception {
        long flightNumber = constants.NON_EXISTENT_FLIGHT_NUMBER;
        // Customer user try to cancel flight route
        String requestJSON = "{\n" +
                "  \"flightNumber\": " + flightNumber + "\n" +
                "}";
        String jwt = getJWTByUsername(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/cancelFlightRoute").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(401, result.getResponse().getStatus());
        String expectedMessage = "Only admin user can cancel flight route.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight number is not existed
        requestJSON = "{\n" +
                "  \"flightNumber\": " + flightNumber + "\n" +
                "}";
        jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        builder = post("/api/cancelFlightRoute").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "The flight route is unavailable in the system.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight number is null
        requestJSON = "{\n" +
                "  \"flightNumber\": " + null + "\n" +
                "}";
        builder = post("/api/cancelFlightRoute").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Flight number is empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // FlightNumberRequest json is null
        builder = post("/api/cancelFlightRoute").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    void cancelFlightTest_Controller_Success() throws Exception {
        long flightNumber = defaultFlights.get(cancelFlightIndex);
        String requestJSON = "{\n" +
                "  \"flightNumber\": " + flightNumber + ",\n" +
                "  \"flightDate\": \"" + bookedFlightDate.toString() + "\"\n" +
                "}";
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/cancelFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("true", content);
    }

    @Test
    @Transactional
    void cancelFlightTest_Controller_Failed() throws Exception {
        long flightNumber = defaultFlights.get(cancelFlightIndex);
        // Customer user try to cancel flight route
        String requestJSON = "{\n" +
                "  \"flightNumber\": " + flightNumber + ",\n" +
                "  \"flightDate\": \"" + bookedFlightDate.toString() + "\"\n" +
                "}";
        String jwt = getJWTByUsername(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/cancelFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(401, result.getResponse().getStatus());
        String expectedMessage = "Only admin user can cancel flight.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());


        // Flight number is null
        requestJSON = "{\n" +
                "  \"flightNumber\": " + null + ",\n" +
                "  \"flightDate\": \"" + bookedFlightDate.toString() + "\"\n" +
                "}";
        jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        builder = post("/api/cancelFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Flight number or date are empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight date is null
        requestJSON = "{\n" +
                "  \"flightNumber\": " + flightNumber + "\n" +
                "}";
        builder = post("/api/cancelFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Flight number or date are empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // FlightNumberRequest json is null
        builder = post("/api/cancelFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    void cancelTicketTest_Controller_Success() throws Exception {
        long flightNumber = defaultFlights.get(cancelTicketIndex);
        String requestJSON = "{\n" +
                "  \"flightNumber\": " + flightNumber + ",\n" +
                "  \"flightDate\": \"" + bookedFlightDate.toString() + "\"\n" +
                "}";
        String jwt = getJWTByUsername(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/cancelTicket").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals("true", content);
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

    @Test
    @Transactional
    void cancelTicketTest_Controller_Failed() throws Exception {
        long flightNumber = defaultFlights.get(cancelFlightIndex);
        // Customer user try to cancel flight route
        String requestJSON = "{\n" +
                "  \"flightNumber\": " + flightNumber + ",\n" +
                "  \"flightDate\": \"" + bookedFlightDate.toString() + "\"\n" +
                "}";
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/cancelTicket").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(401, result.getResponse().getStatus());
        String expectedMessage = "Only customer user can cancel existent ticket.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());


        // Flight number is null
        requestJSON = "{\n" +
                "  \"flightNumber\": " + null + ",\n" +
                "  \"flightDate\": \"" + bookedFlightDate.toString() + "\"\n" +
                "}";
        jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        builder = post("/api/cancelTicket").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Flight number or flight date is empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // Flight date is null
        requestJSON = "{\n" +
                "  \"flightNumber\": " + flightNumber + "\n" +
                "}";
        builder = post("/api/cancelTicket").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Flight number or flight date is empty.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());

        // FlightNumberRequest json is null
        builder = post("/api/cancelTicket").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    void getTicketByCustomer_Controller_Success() throws Exception {
        // Book a new flight for default customer
        List<Flight> availableFlights = flightService.getAllAvailableFlightsByFlightNumber(defaultFlights.get(0));
        int flightIndex = 2;

        String requestJSON = "{\n" +
                "  \"flightDate\": \"" + constants.datePlusSomeDays(bookedFlightDate, flightIndex) + "\",\n" +
                "  \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + "\n" +
                "}";
        String jwt = getJWTByUsername(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        RequestBuilder builder = post("/api/bookFlight").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        User customer = jwtUserDetailsService.getUserByUsername(defaultCustomerUsernames.get(0));
        String validJSON = "{\n" +
                "    \"customerId\": "+ customer.getId() +",\n" +
                "    \"flightNumber\": " + availableFlights.get(flightIndex).getFlightNumber() + ",\n" +
                "    \"flightDate\": \"" + constants.datePlusSomeDays(bookedFlightDate, flightIndex) + "\"\n" +
                "}";
        JSONAssert.assertEquals(validJSON, content, JSONCompareMode.LENIENT);



        builder = get("/api/getTicketByCustomer").header("Authorization", "Bearer " + jwt).
                accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        content = result.getResponse().getContentAsString();
        validJSON = "[{\n" +
                "\t\"flightDate\": " + bookedFlightDate.toString() +
                "}, {\n" +
                "\t\"flightDate\": " + bookedFlightDate.toString() +
                "}, {\n" +
                "\t\"flightDate\": " + bookedFlightDate.toString() +
                "}, {\n" +
                "\t\"flightDate\": " + constants.datePlusSomeDays(bookedFlightDate, flightIndex).toString() +
                "}]";
        JSONAssert.assertEquals(validJSON, content, JSONCompareMode.LENIENT);
    }

    private void validFlightInfo(FlightRoute expectedFlightRoute, long actualFlightNumber, int availableTicket,
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
                assertEquals(availableTicket, flight.getAvailableTickets());
                expectedDate = constants.datePlusSomeDays(expectedDate, 1);
                // Verify Flight Seat Info
                List<UnavailableSeatInfo> unavailableSeatInfos = unavailableSeatInfoRepository.findAllByFlightId(flight.getFlightId());
                assertEquals(0, unavailableSeatInfos.size());
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
