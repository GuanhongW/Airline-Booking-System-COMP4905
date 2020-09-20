package com.guanhong.airlinebookingsystem.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.*;
import com.guanhong.airlinebookingsystem.repository.*;
import com.guanhong.airlinebookingsystem.service.Constants;
import com.guanhong.airlinebookingsystem.service.FlightService;
import com.guanhong.airlinebookingsystem.service.JwtUserDetailsService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
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
    private FlightSeatInfoRepository flightSeatInfoRepository;

    @Autowired
    private FlightRepository flightRepository;

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
        AccountInfo newUserInfo = new AccountInfo(testUsername,constants.ADMIN_USER_PASSWORD_0, Role.ADMIN);
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        testUsername = constants.getNextAdminUsername();
        defaultAdminUsernames.add(testUsername);
        newUserInfo = new AccountInfo(testUsername,constants.ADMIN_USER_PASSWORD_1, Role.ADMIN);
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        // Create default customer user
        testUsername = constants.getNextCustomerUsername();
        defaultCustomerUsernames.add(testUsername);
        newUserInfo = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER,"test", Gender.male,"2000-01-01");
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
        newUserInfo = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_1, Role.USER,"test", Gender.male,"2000-01-01");
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
        Date endDate = constants.datePlusSomeDays(constants.today(), 180);
        Integer availableSeat = null;
        FlightRoute newFlightRoute = new FlightRoute(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
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
        for (int i = 0; i < defaultAdminUsernames.size(); i++){
            testUsername = defaultAdminUsernames.get(i);
            User user = userRepository.findUserByUsername(testUsername);
            userRepository.delete(user);
            assertNull(userRepository.findUserByUsername(testUsername));
            assertNull(customerInfoRepository.findCustomerInfoById(user.getId()));
        }

        // Delete default customer user
        for (int i = 0; i < defaultCustomerUsernames.size(); i++){
            testUsername = defaultCustomerUsernames.get(i);
            User user = userRepository.findUserByUsername(testUsername);
            userRepository.delete(user);
            assertNull(userRepository.findUserByUsername(testUsername));
            assertNull(customerInfoRepository.findCustomerInfoById(user.getId()));
        }

        // Delete default flight
        long flightNumber;
        for (int i = 0; i < defaultFlights.size(); i++){
            flightNumber = defaultFlights.get(i);
            FlightRoute flightRoute = flightRouteRepository.findFlightByflightNumber(flightNumber);
            flightRouteRepository.delete(flightRoute);
            assertNull(flightRouteRepository.findFlightByflightNumber(flightNumber));
            assertNull(flightSeatInfoRepository.findFlightSeatInfoByFlightId(flightNumber));
        }
    }

    @Test
    @Transactional
    void registerTest_Controller_Success() throws Exception{
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
    void registerTest_Controller_Failed() throws Exception{
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
    void authenticateTest_Controller_Success() throws Exception{
        // Login in with admin user
        String requestJSON = "{\n" +
                "  \"password\": \""+  constants.ADMIN_USER_PASSWORD_0 +"\",\n" +
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
                "  \"password\": \""+  constants.CUSTOMER_USER_PASSWORD_0 +"\",\n" +
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
    void authenticateTest_Controller_Failed() throws Exception{
        // missed password
        String requestJSON = "{\n"  +
                "  \"username\": \"autoadmin1\"\n" +
                "}";
        RequestBuilder builder = post("/authenticate").accept(MediaType.APPLICATION_JSON).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        String resultContent = result.getResponse().getContentAsString();
        String expectedJSON = "Username or password cannot be empty.";
        assertEquals(expectedJSON,resultContent);

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
        assertEquals(expectedJSON,resultContent);
    }

    @Test
    @Transactional
    void createNewFlightTest_Controller_Success() throws Exception{
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        String requestJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2022-09-04\",\n" +
                "\t\"flightNumber\": "+ constants.FLIGHT_NUMBER_9995 +",\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-04-06\"\n" +
                "}";
        RequestBuilder builder = post("/createFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String expectedJSON = "{\n" +
                "\t\"arrivalTime\": \"13:05:00\",\n" +
                "\t\"capacity\": 120,\n" +
                "\t\"departureCity\": \"YOW\",\n" +
                "\t\"departureTime\": \"14:05:00\",\n" +
                "\t\"destinationCity\": \"YYZ\",\n" +
                "\t\"endDate\": \"2022-09-04\",\n" +
                "\t\"flightNumber\": "+ constants.FLIGHT_NUMBER_9995 +",\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-04-06\",\n" +
                "\t\"availableSeat\": 126\n" +
                "}";
        JSONAssert.assertEquals(expectedJSON, result.getResponse().getContentAsString(),false);
    }

    @Test
    void createNewFlightTest_Controller_Failed() throws Exception{
        String jwt = getJWTByUsername(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);

        // input newFlight is null
        String requestJSON = null;
        RequestBuilder builder = post("/createFlight").header("Authorization", "Bearer " + jwt)
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
        builder = post("/createFlight").header("Authorization", "Bearer " + jwt)
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
        builder = post("/createFlight").header("Authorization", "Bearer " + jwt)
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
        builder = post("/createFlight").header("Authorization", "Bearer " + jwt)
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
        builder = post("/createFlight").header("Authorization", "Bearer " + jwt)
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
                "\t\"flightNumber\": "+ constants.FLIGHT_NUMBER_9995 +",\n" +
                "\t\"overbooking\": 5,\n" +
                "\t\"startDate\": \"2021-04-06\"\n" +
                "}";
        builder = post("/createFlight").header("Authorization", "Bearer " + jwt)
                .accept(MediaType.APPLICATION_JSON).content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(builder).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        expectedMessage = "Only admin user can create new flights.";
        assertEquals(expectedMessage, result.getResponse().getContentAsString());
    }


    @Test
    @Transactional
    void getAvailableFlightTest_Controller_Success() throws Exception {
        // TODO: Update flight by API without like now use repositity.save()
        //Create a flight without any available seats
        long flightNumber = constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT;
        String departureCity = "YYZ";
        String destinationCity = "YVR";
        Time departureTime = Time.valueOf("10:05:00");
        Time arrivalTime = Time.valueOf("12:00:00");
        int capacity = 148;
        BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
        Date startDate = constants.datePlusSomeDays(constants.today(), 5);;
        Date endDate = constants.datePlusSomeDays(constants.today(), 35);

        FlightRoute newFlightRoute1 = new FlightRoute(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate, null);
        FlightRoute returnedFlightRoute = assertDoesNotThrow(()-> flightService.createNewFlight(newFlightRoute1));
        newFlightRoute1.setAvailableSeat(0);
        assertDoesNotThrow(()->flightRouteRepository.save(newFlightRoute1));
        validFlightInfo(newFlightRoute1,flightNumber,0, true);


        // Create a flight the end date is before
        flightNumber = constants.FLIGHT_NUMBER_EXPIRED;

        FlightRoute newFlightRoute2 = new FlightRoute(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,null);
        returnedFlightRoute = assertDoesNotThrow(()-> flightService.createNewFlight(newFlightRoute2));
        newFlightRoute2.setStartDate(constants.datePlusSomeDays(constants.today(), -100));
        newFlightRoute2.setEndDate(constants.datePlusSomeDays(constants.today(),  0));
        assertDoesNotThrow(()->flightRouteRepository.save(newFlightRoute2));
        validFlightInfo(newFlightRoute2,flightNumber,156, true);


        flightNumber = constants.FLIGHT_NUMBER_AVAILABLE;

        FlightRoute newFlightRoute3 = new FlightRoute(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                null);
        returnedFlightRoute = assertDoesNotThrow(()-> flightService.createNewFlight(newFlightRoute3));
        newFlightRoute3.setStartDate(constants.datePlusSomeDays(constants.today(), 1));
        newFlightRoute3.setEndDate(constants.datePlusSomeDays(constants.today(),  8));
        assertDoesNotThrow(()->flightRouteRepository.save(newFlightRoute3));
        validFlightInfo(newFlightRoute3,flightNumber,156, true);

        String jwt = getJWTByUsername(defaultAdminUsernames.get(0),constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = get("/getFlights").header("Authorization", "Bearer " + jwt);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<FlightRoute> flightRoutes = mapper.readValue(content, new TypeReference<List<FlightRoute>>() {});
        assertTrue(validFlightExistInList(flightRoutes, constants.FLIGHT_NUMBER_AVAILABLE));
        assertFalse(validFlightExistInList(flightRoutes, constants.FLIGHT_NUMBER_EXPIRED));
        assertFalse(validFlightExistInList(flightRoutes, constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT));
    }

    private String getJWTByUsername(String username, String password){
        try{
            UserCredential user = new UserCredential(username, password);
            RequestBuilder builders= post("/authenticate").
                    accept(MediaType.APPLICATION_JSON).content(user.toJsonString()).
                    contentType(MediaType.APPLICATION_JSON);
            MvcResult jwtResult = mockMvc.perform(builders).andReturn();
            String jwtResultContent = jwtResult.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            UserLoginResponse jwt = mapper.readValue(jwtResultContent, UserLoginResponse.class);
            return jwt.getJwttoken();
        }
        catch (Exception e){
            System.out.println("Class: Constants, Method: getJWTFromController: " +e.getMessage());
            assertFalse(true);
            return null;
        }
    }

    private void validFlightInfo(FlightRoute expectedFlightRoute, long actualFlightNumber, int availableSeats,
                                 boolean isSkipSeatList) {
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
        assertEquals(availableSeats, returnedFlightRoute.getAvailableSeat());
        //Verify Flights in flight table
        List<Flight> returnedFlights = assertDoesNotThrow(()->flightRepository.findAllByFlightNumberOrderByFlightDate(returnedFlightRoute.getFlightNumber()));
        Date expectedDate = returnedFlightRoute.getStartDate();
        if (isSkipSeatList == false){
            SeatList seatList;
            for (int i = 0; i < returnedFlights.size(); i++){
                Flight flight = returnedFlights.get(i);
                assertEquals(expectedDate, flight.getFlightDate());
                expectedDate = constants.datePlusSomeDays(expectedDate, 1);
                // Verify Flight Seat Info
                FlightSeatInfo flightSeatInfo = assertDoesNotThrow(()->flightSeatInfoRepository.findFlightSeatInfoByFlightId(flight.getFlightId()));
                seatList = assertDoesNotThrow(()->flightSeatInfo.getSeatListByJson());
                assertEquals(expectedFlightRoute.getCapacity(), seatList.getSize());
            }
        }

    }

    private boolean validFlightExistInList(List<FlightRoute> flightRouteList, long flightNumber){
        for (int i = 0; i < flightRouteList.size(); i++){
            if (flightRouteList.get(i).getFlightNumber() == flightNumber){
                return true;
            }
        }
        return false;
    }

}
