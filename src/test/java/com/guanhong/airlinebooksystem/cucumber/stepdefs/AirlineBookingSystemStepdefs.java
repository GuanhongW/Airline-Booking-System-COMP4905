package com.guanhong.airlinebooksystem.cucumber.stepdefs;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guanhong.airlinebookingsystem.AirlineBookingSystemApplication;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.*;
import com.guanhong.airlinebookingsystem.repository.*;
import com.guanhong.airlinebookingsystem.service.FlightService;
import com.guanhong.airlinebookingsystem.service.JwtUserDetailsService;
import com.guanhong.airlinebookingsystem.service.TicketService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)

@AutoConfigureMockMvc
@CucumberContextConfiguration
@SpringBootTest(classes = AirlineBookingSystemApplication.class)
public class AirlineBookingSystemStepdefs {

    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionStatus transaction;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerInfoRepository customerInfoRepository;
    @Autowired
    private FlightService flightService;
    @Autowired
    private FlightRouteRepository flightRouteRepository;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private UnavailableSeatInfoRepository unavailableSeatInfoRepository;

    private static CucumberDataGenerator dataGenerator = CucumberDataGenerator.getInstance();


    private List<String> defaultAdminUsernames = new ArrayList<>();

    private List<String> defaultCustomerUsernames = new ArrayList<>();

    private List<Long> defaultFlights = new ArrayList<>();

    private List<Long> defaultCustomerID = new ArrayList<>();

    private int defaultFlightAmount = 5;

    private MvcResult requestResult;

    private String requestJSON = "";

    private String jwt = "";

    private long selectFlightNumber;

    private FlightRoute originalFlightRoute;

    @Before
    public void setUp() throws Exception {
        // Start transaction for teardown to clean all database modify
        transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Create default admin user

        String testUsername = dataGenerator.getNextAdminUsername();
        defaultAdminUsernames.add(testUsername);
        AccountInfo newUserInfo = new AccountInfo(testUsername, dataGenerator.ADMIN_USER_PASSWORD_0, Role.ADMIN);
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        testUsername = dataGenerator.getNextAdminUsername();
        defaultAdminUsernames.add(testUsername);
        newUserInfo = new AccountInfo(testUsername, dataGenerator.ADMIN_USER_PASSWORD_1, Role.ADMIN);
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        // Create default customer user
        testUsername = dataGenerator.getNextCustomerUsername();
        defaultCustomerUsernames.add(testUsername);
        newUserInfo = new AccountInfo(testUsername, dataGenerator.CUSTOMER_USER_PASSWORD_0, Role.USER, "test", Gender.female, "2000-01-01");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        defaultCustomerID.add(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        CustomerInfo customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.female, customerInfo.getGender());

        testUsername = dataGenerator.getNextCustomerUsername();
        defaultCustomerUsernames.add(testUsername);
        newUserInfo = new AccountInfo(testUsername, dataGenerator.CUSTOMER_USER_PASSWORD_1, Role.USER, "test", Gender.male, "2000-01-01");
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
            long flightNumber = dataGenerator.getNextAvailableFlightNumber();
            defaultFlights.add(flightNumber);
            String departureCity = "YYZ";
            String destinationCity = "YVR";
            Time departureTime = Time.valueOf("10:05:00");
            Time arrivalTime = Time.valueOf("12:00:00");
            int aircraftId = 900;
            BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
            Date startDate = dataGenerator.datePlusSomeDays(dataGenerator.today(), 80);
            Date endDate = dataGenerator.datePlusSomeDays(dataGenerator.today(), 100);
            int availableTicket = 80;
            FlightRoute newFlightRoute = new FlightRoute(flightNumber, departureCity, destinationCity, departureTime, arrivalTime,
                    aircraftId, overbooking, startDate, endDate);
            flightService.createNewFlight(newFlightRoute);
            FlightRoute returnedFlightRoute = flightRouteRepository.findFlightByflightNumber(newFlightRoute.getFlightNumber());
            assertNotNull(returnedFlightRoute);
        }

        System.out.println("Before All finished.");

    }

    @After
    public void teardown() {
        transactionManager.rollback(transaction);

//        // Delete default admin user
//        String testUsername;
//        for (int i = 0; i < defaultAdminUsernames.size(); i++) {
//            testUsername = defaultAdminUsernames.get(i);
//            User user = userRepository.findUserByUsername(testUsername);
//            userRepository.delete(user);
//            assertNull(userRepository.findUserByUsername(testUsername));
//            assertNull(customerInfoRepository.findCustomerInfoById(user.getId()));
//        }
//
//        // Delete default customer user
//        for (int i = 0; i < defaultCustomerUsernames.size(); i++) {
//            testUsername = defaultCustomerUsernames.get(i);
//            User user = userRepository.findUserByUsername(testUsername);
//            userRepository.delete(user);
//            assertNull(userRepository.findUserByUsername(testUsername));
//            assertNull(customerInfoRepository.findCustomerInfoById(user.getId()));
//        }
    }

    @Given("Printing the thread info for feature {string} and scenario {string}")
    public void thread_Info(String feature, String scenario) {
        System.out.format("Thread ID - %2d - feature file: %s, scenario: %s.\n",
                Thread.currentThread().getId(), feature, scenario);
    }

    @Given("{string} user {int} logs in to the system")
    public void default_user_login_system(String role, int number) throws Exception {
        String username = "";
        String password = "";
        if (role.equals("Admin")) {
            username = defaultAdminUsernames.get(number - 1);
            if (number == 2) {
                password = dataGenerator.ADMIN_USER_PASSWORD_1;
            } else {
                password = dataGenerator.ADMIN_USER_PASSWORD_0;
            }
        } else if (role.equals("Customer")) {
            username = defaultCustomerUsernames.get(number - 1);
            if (number == 2) {
                password = dataGenerator.CUSTOMER_USER_PASSWORD_1;
            } else {
                password = dataGenerator.CUSTOMER_USER_PASSWORD_0;
            }
        } else {
            System.out.println("Role is invalid!");
            assertFalse(true);
        }
        requestJSON = "{\n" +
                "  \"password\": \"" + password + "\",\n" +
                "  \"username\": \"" + username + "\"\n" +
                "}";
        click_button("Log In");
        verify_status_code(200);
        verify_JWT("not empty");
    }

    @When("^User enters following credentials in log in page$")
    public void login_request(DataTable dt) {
        Map<String, String> credential = dt.asMap(String.class, String.class);
        requestJSON = "{\n" +
                "  \"password\": \"" + credential.get("password") + "\",\n" +
                "  \"username\": \"" + credential.get("username") + "\"\n" +
                "}";
//        System.out.println("Thread ID: " + Thread.currentThread().getId() + ": " + defaultAdminUsernames.size());
    }

    @When("^User enters following credentials in register page$")
    public void register_request(DataTable dt) {
        Map<String, String> credential = dt.asMap(String.class, String.class);
        if (credential.get("role").equals("ADMIN")) {
            requestJSON = "{\n" +
                    "  \"password\": \"" + credential.get("password") + "\",\n" +
                    "  \"role\": \"" + credential.get("role") + "\",\n" +
                    "  \"username\": \"" + credential.get("username") + "\"\n" +
                    "}";
        } else if (credential.get("role").equals("USER")) {
            requestJSON = "{\n" +
                    "  \"birthDate\": \"" + credential.get("birthDate") + "\",\n" +
                    "  \"gender\": \"" + credential.get("gender") + "\",\n" +
                    "  \"name\": \"" + credential.get("name") + "\",\n" +
                    "  \"password\": \"" + credential.get("password") + "\",\n" +
                    "  \"role\": \"" + credential.get("role") + "\",\n" +
                    "  \"username\": \"" + credential.get("username") + "\"\n" +
                    "}";
        } else {
            System.out.println("The credential's role is invalid!");
            assertFalse(true);
        }
    }

    @When("^Admin user enters following information of a new flight route in create flight page$")
    public void create_flight_route_request(DataTable dt) {
        Map<String, String> flightInfo = dt.asMap(String.class, String.class);
        selectFlightNumber = getSelectFlightNumber(flightInfo.get("flightNumber"));
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"" + flightInfo.get("arrivalTime") + "\",\n" +
                "\t\"aircraftId\": " + flightInfo.get("aircraftId") + ",\n" +
                "\t\"departureCity\": \"" + flightInfo.get("departureCity") + "\",\n" +
                "\t\"departureTime\": \"" + flightInfo.get("departureTime") + "\",\n" +
                "\t\"destinationCity\": \"" + flightInfo.get("destinationCity") + "\",\n" +
                "\t\"endDate\": \"" + dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("endDate"))) + "\",\n" +
                "\t\"flightNumber\": " + selectFlightNumber + ",\n" +
                "\t\"overbooking\": " + flightInfo.get("overbooking") + ",\n" +
                "\t\"startDate\": \"" + dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("startDate"))) + "\"\n" +
                "}";
    }

    @When("^Admin user enters following information of a existent flight route in update flight page$")
    public void update_flight_route_request(DataTable dt) {
        Map<String, String> flightInfo = dt.asMap(String.class, String.class);
        selectFlightNumber = getSelectFlightNumber(flightInfo.get("flightNumber"));
        requestJSON = "{\n" +
                "\t\"arrivalTime\": \"" + flightInfo.get("arrivalTime") + "\",\n" +
                "\t\"aircraftId\": " + flightInfo.get("aircraftId") + ",\n" +
                "\t\"departureCity\": \"" + flightInfo.get("departureCity") + "\",\n" +
                "\t\"departureTime\": \"" + flightInfo.get("departureTime") + "\",\n" +
                "\t\"destinationCity\": \"" + flightInfo.get("destinationCity") + "\",\n" +
                "\t\"endDate\": \"" + dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("endDate"))) + "\",\n" +
                "\t\"flightNumber\": " + selectFlightNumber + ",\n" +
                "\t\"overbooking\": " + flightInfo.get("overbooking") + ",\n" +
                "\t\"startDate\": \"" + dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("startDate"))) + "\"\n" +
                "}";
    }

    @When("User clicks {string} in side menu")
    public void click_side_menu(String menu) throws Exception {
        String url = "";
        switch (menu) {
            case "Cancel Flight":
                url = "/api/getFlightRoutes";
                break;
            case "Update Flight":
                url = "/api/getFlightRoutes";
                break;
            default:
                System.out.println("The side menu is undefined!");
                assertFalse(true);

        }
        RequestBuilder builder = get(url).accept(MediaType.APPLICATION_JSON).
                header("Authorization", "Bearer " + jwt);
        requestResult = mockMvc.perform(builder).andReturn();
    }

    @When("^Admin user enters following information to cancel new flight route in cancel flight page$")
    public void cancel_flight_route_request(DataTable dt) {
        Map<String, String> flightInfo = dt.asMap(String.class, String.class);
        selectFlightNumber = getSelectFlightNumber(flightInfo.get("flightNumber"));
        requestJSON = "{\n" +
                "  \"flightNumber\": " + selectFlightNumber + "\n" +
                "}";
    }

    @And("User clicks the {string} button")
    public void click_button(String button) throws Exception {
        String url = "";
        switch (button) {
            case "Log In":
                url = "/authenticate";
                break;
            case "Register":
                url = "/register";
                break;
            case "Create Flight":
                url = "/api/createFlight";
                break;
            case "Cancel Flight":
                url = "/api/cancelFlightRoute";
                break;
            case "Update Flight":
                url = "/api/updateFlight";
                break;
            default:
                System.out.println("The button is undefined!");
                assertFalse(true);
        }
        RequestBuilder builder = post(url).accept(MediaType.APPLICATION_JSON).
                header("Authorization", "Bearer " + jwt).
                content(requestJSON).contentType(MediaType.APPLICATION_JSON);
        requestResult = mockMvc.perform(builder).andReturn();
    }


    @Then("The server return status code of {int}")
    public void verify_status_code(int httpCode) {
        assertEquals(httpCode, requestResult.getResponse().getStatus());
    }

    @Then("The JWT token is {string}")
    public void verify_JWT(String status) throws Exception {
        String resultContent = requestResult.getResponse().getContentAsString();
        if (status.equals("not empty")) {
            ObjectMapper mapper = new ObjectMapper();
            UserLoginResponse userLoginResponse = mapper.readValue(resultContent, UserLoginResponse.class);
            assertNotNull(userLoginResponse.getJwttoken());
            jwt = userLoginResponse.getJwttoken();
        } else if (status.equals("empty")) {
            assertEquals("INVALID_CREDENTIALS", resultContent);
        } else {
            assertFalse(true);
            System.out.println("Invalid status.");
        }
    }

    @Then("^The server return the following response for register request$")
    public void verify_response_json_register(DataTable dt) throws Exception {
        Map<String, String> credential = dt.asMap(String.class, String.class);
        String resultContent = requestResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        CreateUserResponse createUserResponse = mapper.readValue(resultContent, CreateUserResponse.class);
        assertEquals(credential.get("username"), createUserResponse.getUsername());
        assertNotNull(createUserResponse.getAccountId());
    }

    @Then("^The server return the following message$")
    public void verify_response_message(DataTable dt) throws Exception {
        Map<String, String> message = dt.asMap(String.class, String.class);
        String resultContent = requestResult.getResponse().getContentAsString();
        assertEquals(message.get("expectedMessage"), resultContent);
    }

    @Then("^The server return the following response for create flight request$")
    public void verify_response_json_create_flight(DataTable dt) throws Exception {
        Map<String, String> flightInfo = dt.asMap(String.class, String.class);
        String expectedJSON = "{\n" +
                "\t\"arrivalTime\": \"" + flightInfo.get("arrivalTime") + "\",\n" +
                "\t\"aircraftId\": " + flightInfo.get("aircraftId") + ",\n" +
                "\t\"departureCity\": \"" + flightInfo.get("departureCity") + "\",\n" +
                "\t\"departureTime\": \"" + flightInfo.get("departureTime") + "\",\n" +
                "\t\"destinationCity\": \"" + flightInfo.get("destinationCity") + "\",\n" +
                "\t\"endDate\": \"" + dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("endDate"))) + "\",\n" +
                "\t\"flightNumber\": " + selectFlightNumber + ",\n" +
                "\t\"overbooking\": " + flightInfo.get("overbooking") + ",\n" +
                "\t\"startDate\": \"" + dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("startDate"))) + "\"\n" +
                "}";
        JSONAssert.assertEquals(expectedJSON, requestResult.getResponse().getContentAsString(), false);
    }

    @Then("^The server return the following response for update flight request$")
    public void verify_response_json_update_flight(DataTable dt) throws Exception {
        Map<String, String> flightInfo = dt.asMap(String.class, String.class);
        String expectedJSON = "{\n" +
                "\t\"arrivalTime\": \"" + flightInfo.get("arrivalTime") + "\",\n" +
                "\t\"aircraftId\": " + flightInfo.get("aircraftId") + ",\n" +
                "\t\"departureCity\": \"" + flightInfo.get("departureCity") + "\",\n" +
                "\t\"departureTime\": \"" + flightInfo.get("departureTime") + "\",\n" +
                "\t\"destinationCity\": \"" + flightInfo.get("destinationCity") + "\",\n" +
                "\t\"endDate\": \"" + dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("endDate"))) + "\",\n" +
                "\t\"flightNumber\": " + selectFlightNumber + ",\n" +
                "\t\"overbooking\": " + flightInfo.get("overbooking") + ",\n" +
                "\t\"startDate\": \"" + dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("startDate"))) + "\"\n" +
                "}";
        JSONAssert.assertEquals(expectedJSON, requestResult.getResponse().getContentAsString(), false);
    }

    @Then("^The select flight exist in the system")
    public void verify_selectFlight_exist() {
        assertNotNull(flightRouteRepository.findFlightByflightNumber(selectFlightNumber));
    }

    @Then("^The select flight does not exist in the system")
    public void verify_selectFlight_not_exist() {
        assertNull(flightRouteRepository.findFlightByflightNumber(selectFlightNumber));
    }

    @Then("The flight {string} includes in get flight routes request")
    public void verify_default_flight(String flightNumbers) throws Exception {
        List<Long> flightList = new ArrayList<>();

        if (flightNumbers.equals("DEFAULT")) {
            flightList.addAll(defaultFlights);
        } else {
            try {
                String[] flights = flightNumbers.split(", ");
                for (String flight : flights) {
                    flightList.add(Long.parseLong(flight));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                assertFalse(true);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        List<FlightRoute> flightRoutes = mapper.readValue(requestResult.getResponse().getContentAsString()
                , new TypeReference<List<FlightRoute>>() {
                });
        for (Long flightNumber : flightList) {
            assertTrue(validFlightExistInList(flightRoutes, flightNumber));
        }
    }

    @Then("^The selected flight in the database have the following information$")
    public void verify_selectFlight_info(DataTable dt) {
        Map<String, String> flightInfo = dt.asMap(String.class, String.class);
        FlightRoute expectedFlightRoute = new FlightRoute(selectFlightNumber,
                flightInfo.get("departureCity"),
                flightInfo.get("destinationCity"),
                Time.valueOf(flightInfo.get("departureTime")),
                Time.valueOf(flightInfo.get("arrivalTime")),
                Integer.parseInt(flightInfo.get("aircraftId")),
                BigDecimal.valueOf(Integer.parseInt(flightInfo.get("overbooking"))).setScale(2),
                dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("startDate"))),
                dataGenerator.datePlusSomeDays(dataGenerator.today(), Integer.parseInt(flightInfo.get("endDate"))));
        validFlightInfo(expectedFlightRoute, selectFlightNumber, 0, false, false);

    }

    @And("User records the original flight information for flight {string}")
    public void record_original_flight_route(String flightNumberStr){
        long flightNumber = getSelectFlightNumber(flightNumberStr);
        FlightRoute returnedFlightRoute = flightRouteRepository.findFlightByflightNumber(flightNumber);
        originalFlightRoute = new FlightRoute(returnedFlightRoute);
    }

    @Then("The selected flight does not change")
    public void veirfy_flight_does_not_change(){
        validFlightInfo(originalFlightRoute, selectFlightNumber, 0,false,false);
    }

    private boolean validFlightExistInList(List<FlightRoute> flightRouteList, long flightNumber) {
        for (int i = 0; i < flightRouteList.size(); i++) {
            if (flightRouteList.get(i).getFlightNumber() == flightNumber) {
                return true;
            }
        }
        return false;
    }

    private long getSelectFlightNumber(String flightNumber) {
        if (flightNumber.contains("DEFAULT")) {
            String[] defaultFlightNumber = flightNumber.split(":");
            return defaultFlights.get(Integer.parseInt(defaultFlightNumber[1]) - 1);
        } else if (flightNumber.equals("NON_EXISTENT")) {
            return dataGenerator.NON_EXISTENT_FLIGHT_NUMBER;
        } else if (flightNumber.equals("NEXT")) {
            return dataGenerator.getNextAvailableFlightNumber();
        } else if (flightNumber.equals("DUPLICATED")) {
            return defaultFlights.get(0);
        } else {
            return Long.parseLong(flightNumber);
        }
    }

    private void validFlightInfo(FlightRoute expectedFlightRoute, long actualFlightNumber, int availableTicket,
                                 boolean isSkipSeatList, boolean isVerifyAvaialableTicket) {
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
                expectedDate = dataGenerator.datePlusSomeDays(expectedDate, 1);
                // Verify Flight Seat Info
                if (isVerifyAvaialableTicket) {
                    assertEquals(availableTicket, flight.getAvailableTickets());
                    List<UnavailableSeatInfo> unavailableSeatInfos = unavailableSeatInfoRepository.findAllByFlightId(flight.getFlightId());
                    assertEquals(0, unavailableSeatInfos.size());
                }
            }
        }

    }

//    @When("Test test API")
//    public void test_test_API() throws Exception{
//        System.out.format("Thread ID - %2d - from Test feature file.\n",Thread.currentThread().getId());
//        MvcResult result = mockMvc.perform(get("/test")).andReturn();
//        assertEquals(400, result.getResponse().getStatus());
//    }


}
