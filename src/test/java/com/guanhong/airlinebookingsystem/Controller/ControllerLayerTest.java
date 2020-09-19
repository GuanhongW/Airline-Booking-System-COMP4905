package com.guanhong.airlinebookingsystem.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.*;
import com.guanhong.airlinebookingsystem.repository.CustomerInfoRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightSeatInfoRepository;
import com.guanhong.airlinebookingsystem.repository.UserRepository;
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
    private FlightRepository flightRepository;

    @Autowired FlightSeatInfoRepository flightSeatInfoRepository;

    private static Constants constants = Constants.getInstance();

    private static List<String> defaultAdminUsernames = new ArrayList<>();

    private static List<String> defaultCustomerUsernames = new ArrayList<>();

    private static List<Long> defaultFlights = new ArrayList<>();


    @BeforeAll
    static void createDefaultAccount(@Autowired JwtUserDetailsService jwtUserDetailsService,
                                     @Autowired UserRepository userRepository,
                                     @Autowired CustomerInfoRepository customerInfoRepository,
                                     @Autowired FlightService flightService,
                                     @Autowired FlightRepository flightRepository) throws Exception {
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
        Flight newFlight = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        flightService.createNewFlight(newFlight);
        Flight returnedFlight = flightRepository.findFlightByflightNumber(newFlight.getFlightNumber());
        assertNotNull(returnedFlight);
        System.out.println("Before All finished.");
    }

    @AfterAll
    static void deleteDefaultAccount(@Autowired UserRepository userRepository,
                                     @Autowired CustomerInfoRepository customerInfoRepository,
                                     @Autowired FlightRepository flightRepository,
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
            Flight flight = flightRepository.findFlightByflightNumber(flightNumber);
            flightRepository.delete(flight);
            assertNull(flightRepository.findFlightByflightNumber(flightNumber));
            assertNull(flightSeatInfoRepository.findFlightSeatInfoByFlightNumber(flightNumber));
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
    }


    @Test
    @Transactional
    void getAvailableFlightTest_Controller_Success() throws Exception {
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
        Integer availableSeat = 0;
        Flight newFlight1 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,startDate,endDate,
                availableSeat);
        Flight returnedFlight = assertDoesNotThrow(()->flightRepository.save(newFlight1));
        SeatList newSeatList = new SeatList(returnedFlight.getCapacity());
        FlightSeatInfo flightSeatInfo = new FlightSeatInfo(returnedFlight.getFlightNumber(),
                newSeatList.toJsonString());
        FlightSeatInfo returnedFlightSeatInfo = flightSeatInfoRepository.save(flightSeatInfo);
        validFlightInfo(newFlight1,flightNumber,0);


        // Create a flight the end date is before
        flightNumber = constants.FLIGHT_NUMBER_EXPIRED;
        startDate = constants.datePlusSomeDays(constants.today(), -100);
        endDate = constants.datePlusSomeDays(constants.today(),  0);
        availableSeat = 156;
        Flight newFlight2 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        returnedFlight = assertDoesNotThrow(()->flightRepository.save(newFlight2));
        newSeatList = new SeatList(returnedFlight.getCapacity());
        flightSeatInfo = new FlightSeatInfo(returnedFlight.getFlightNumber(),
                newSeatList.toJsonString());
        returnedFlightSeatInfo = flightSeatInfoRepository.save(flightSeatInfo);
        validFlightInfo(newFlight2,flightNumber,156);


        flightNumber = constants.FLIGHT_NUMBER_AVAILABLE;
        startDate = constants.datePlusSomeDays(constants.today(), 1);
        endDate = constants.datePlusSomeDays(constants.today(),  8);
        Flight newFlight3 = new Flight(flightNumber,departureCity,destinationCity,departureTime,arrivalTime,
                capacity,overbooking,new java.sql.Date(startDate.getTime()),new java.sql.Date(endDate.getTime()),
                availableSeat);
        returnedFlight = assertDoesNotThrow(()->flightRepository.save(newFlight3));
        newSeatList = new SeatList(returnedFlight.getCapacity());
        flightSeatInfo = new FlightSeatInfo(returnedFlight.getFlightNumber(),
                newSeatList.toJsonString());
        returnedFlightSeatInfo = flightSeatInfoRepository.save(flightSeatInfo);
        validFlightInfo(newFlight3,flightNumber,156);

        String jwt = getJWTByUsername(defaultAdminUsernames.get(0),constants.ADMIN_USER_PASSWORD_0);
        RequestBuilder builder = get("/getFlights").header("Authorization", "Bearer " + jwt);
        MvcResult result = mockMvc.perform(builder).andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<Flight> flights = mapper.readValue(content, new TypeReference<List<Flight>>() {});
        assertTrue(validFlightExistInList(flights, constants.FLIGHT_NUMBER_AVAILABLE));
        assertFalse(validFlightExistInList(flights, constants.FLIGHT_NUMBER_EXPIRED));
        assertFalse(validFlightExistInList(flights, constants.FLIGHT_NUMBER_NO_AVAILABLE_SEAT));
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

    private boolean validFlightExistInList(List<Flight> flightList, long flightNumber){
        for (int i = 0; i < flightList.size(); i++){
            if (flightList.get(i).getFlightNumber() == flightNumber){
                return true;
            }
        }
        return false;
    }

}
