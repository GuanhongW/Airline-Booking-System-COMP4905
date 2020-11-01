package com.guanhong.airlinebooksystem.cucumber.stepdefs;

import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.BookSeatRequest;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.FlightRequest;
import com.guanhong.airlinebookingsystem.repository.CustomerInfoRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRouteRepository;
import com.guanhong.airlinebookingsystem.repository.UserRepository;
import com.guanhong.airlinebookingsystem.service.Constants;
import com.guanhong.airlinebookingsystem.service.FlightService;
import com.guanhong.airlinebookingsystem.service.JwtUserDetailsService;
import com.guanhong.airlinebookingsystem.service.TicketService;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CucumberDataGenerator {

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
    private static int defaultFlightAmount = 10;

    private static int cancelFlightRouteIndex = 6;

    private static int cancelFlightIndex = 7;

    private static int cancelTicketIndex = 8;

    private  Date bookedFlightDate = datePlusSomeDays(today(), 90);

    public static long FLIGHT_NUMBER_1 = 1;
    public static long FLIGHT_NUMBER_9999 = 9999;
    public static long FLIGHT_NUMBER_999 = 999;
    public static long FLIGHT_NUMBER_99 = 99;
    public static long FLIGHT_NUMBER_NO_AVAILABLE_SEAT = 9998;
    public static long FLIGHT_NUMBER_EXPIRED = 9997;
    public static long FLIGHT_NUMBER_AVAILABLE = 9996;
    public static long FLIGHT_NUMBER_9995 = 9995;
    public static long FLIGHT_NUMBER_START_DATE_EXPIRED = 9994;
    public static long NON_EXISTENT_FLIGHT_NUMBER = 9993;

    // Default admin user password
    public static String ADMIN_USER_PASSWORD_0 = "adminadmin1";
    public static String ADMIN_USER_PASSWORD_1 = "adminadmin2";

    // Default customer user password
    public static String CUSTOMER_USER_PASSWORD_0 = "useruser1";
    public static String CUSTOMER_USER_PASSWORD_1 = "useruser2";

    // Unique List
    public static List<Long> flightList = new ArrayList<>();
    public static LinkedHashMap<String, Long> adminUserList = new LinkedHashMap<>();
    public static LinkedHashMap<String, Long> customerUserList = new LinkedHashMap<>();

    public  List<Long> getFlightList() {
        return flightList;
    }

    public  void setFlightList(List<Long> flightList) {
        CucumberDataGenerator.flightList = flightList;
    }

    public  HashMap<String, Long> getAdminUserList() {
        return adminUserList;
    }

    public void setAdminUserList(LinkedHashMap<String, Long> adminUserList) {
        CucumberDataGenerator.adminUserList = adminUserList;
    }

    public HashMap<String, Long> getCustomerUserList() {
        return customerUserList;
    }

    public static void setCustomerUserList(LinkedHashMap<String, Long> customerUserList) {
        CucumberDataGenerator.customerUserList = customerUserList;
    }

    private static boolean isGetNextAdminUsername = false;
    private static boolean isGetNextCustomerUsername = false;

    private CucumberDataGenerator(){
        // 0 is not a valid flight number
        flightList.add((long)0);
    }

    private static class SingletonHolder{
        private static final CucumberDataGenerator INSTANCE = new CucumberDataGenerator();
    }

    public static final CucumberDataGenerator getInstance(){
        return CucumberDataGenerator.SingletonHolder.INSTANCE;
    }

    public synchronized long getNextAvailableFlightNumber(){
        long flightNumber = flightList.size();
        if (flightNumber == FLIGHT_NUMBER_9999 ||
                flightNumber == FLIGHT_NUMBER_999 ||
                flightNumber == FLIGHT_NUMBER_99 ||
                flightNumber == FLIGHT_NUMBER_1){
            flightList.add(flightNumber);
            flightNumber++;
        }
        flightList.add(flightNumber);
        return flightNumber;
    }

    public synchronized String getNextAdminUsername() {
        isGetNextAdminUsername = true;
        int adminNumber = adminUserList.size();
        String newAdminUsername = "auto_admin_" + adminNumber;
        adminUserList.put(newAdminUsername, (long)-1);
        return newAdminUsername;
    }

    public synchronized String getNextCustomerUsername() {
        int customerNumber = customerUserList.size();
        String newCustomerUsername = "auto_customer_" + customerNumber + "@carleton.ca";
        customerUserList.put(newCustomerUsername, (long)-1);
        return newCustomerUsername;
    }

    public Date tomorrow() {
        return datePlusSomeDays(today(),1);
    }

    public Date today(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return new Date(calendar.getTime().getTime());
    }

    public Date datePlusSomeDays(Date date, int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return new Date(calendar.getTime().getTime());
    }

    public BigDecimal getOverbookingByNumber(double overbooking){
        return  BigDecimal.valueOf(overbooking).setScale(2);
    }

    public void setUserId(Role role, String userName, long id){
        if (role == Role.USER){
            customerUserList.put(userName, id);
        }
        else {
            adminUserList.put(userName, id);
        }
    }

    public void testSetUp() throws Exception {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Create default admin user 1

            String testUsername =getNextAdminUsername();
            AccountInfo newUserInfo = new AccountInfo(testUsername, ADMIN_USER_PASSWORD_0, Role.ADMIN);
            CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
            assertEquals(testUsername, res.getUsername());
            assertNotNull(res.getAccountId());
            User user = userRepository.findById(res.getAccountId()).get();
            assertEquals(Role.ADMIN, user.getRole());

            testUsername = getNextAdminUsername();
            newUserInfo = new AccountInfo(testUsername, ADMIN_USER_PASSWORD_1, Role.ADMIN);
            res = jwtUserDetailsService.createAccount(newUserInfo);
            assertEquals(testUsername, res.getUsername());
            assertNotNull(res.getAccountId());
            user = userRepository.findById(res.getAccountId()).get();
            assertEquals(Role.ADMIN, user.getRole());

            // Create default customer user
            testUsername = getNextCustomerUsername();
            newUserInfo = new AccountInfo(testUsername, CUSTOMER_USER_PASSWORD_0, Role.USER, "test", Gender.male, "2000-01-01");
            res = jwtUserDetailsService.createAccount(newUserInfo);
            assertEquals(testUsername, res.getUsername());
            assertNotNull(res.getAccountId());
            user = userRepository.findById(res.getAccountId()).get();
            assertEquals(Role.USER, user.getRole());
            CustomerInfo customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
            assertEquals("test", customerInfo.getName());
            assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
            assertEquals(Gender.male, customerInfo.getGender());

            testUsername = getNextCustomerUsername();
            newUserInfo = new AccountInfo(testUsername, CUSTOMER_USER_PASSWORD_1, Role.USER, "test", Gender.male, "2000-01-01");
            res = jwtUserDetailsService.createAccount(newUserInfo);
            assertEquals(testUsername, res.getUsername());
            assertNotNull(res.getAccountId());
            setUserId(Role.USER, res.getUsername(), res.getAccountId());
            user = userRepository.findById(res.getAccountId()).get();
            assertEquals(Role.USER, user.getRole());
            customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
            assertEquals("test", customerInfo.getName());
            assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
            assertEquals(Gender.male, customerInfo.getGender());

            // Create default flight
            for (int i = 0; i < defaultFlightAmount; i++) {
                long flightNumber = getNextAvailableFlightNumber();
                String departureCity = "YYZ";
                String destinationCity = "YVR";
                Time departureTime = Time.valueOf("10:05:00");
                Time arrivalTime = Time.valueOf("12:00:00");
                int aircraftId = 900;
                BigDecimal overbooking = BigDecimal.valueOf(6).setScale(2);
                Date startDate = datePlusSomeDays(today(), 80);
                Date endDate = datePlusSomeDays(today(), 100);
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
            for (int i = 0; i < getCustomerUserList().size(); i++) {
                selectFlight = new Flight(getFlightList().get(cancelFlightRouteIndex), bookedFlightDate);
                Ticket returnedTicket = ticketService.bookFlight(new FlightRequest(selectFlight), getCustomerUserList().get(i));
                assertNotNull(returnedTicket);
                BookSeatRequest bookSeatRequest1 = new BookSeatRequest(selectFlight.getFlightNumber(),
                        selectFlight.getFlightDate(), i+2);
                returnedTicket = ticketService.bookSeat(bookSeatRequest1, getCustomerUserList().get(i));
                assertNotNull(returnedTicket);
            }
            // This index should be same as the cancelFlightTest_Controller_Success
            for (int i = 0; i < getCustomerUserList().size(); i++) {
                selectFlight = new Flight(getFlightList().get(cancelFlightIndex), bookedFlightDate);
                Ticket returnedTicket = ticketService.bookFlight(new FlightRequest(selectFlight), getCustomerUserList().get(i));
                assertNotNull(returnedTicket);
                BookSeatRequest bookSeatRequest1 = new BookSeatRequest(selectFlight.getFlightNumber(),
                        selectFlight.getFlightDate(), i+2);
                returnedTicket = ticketService.bookSeat(bookSeatRequest1, getCustomerUserList().get(i));
                assertNotNull(returnedTicket);
            }
            System.out.println("Booked seats for ticket");

            // This index should be same as the cancelFlightTest_Controller_Success
            for (int i = 0; i < getCustomerUserList().size(); i++) {
                selectFlight = new Flight(getFlightList().get(cancelTicketIndex), bookedFlightDate);
                Ticket returnedTicket = ticketService.bookFlight(new FlightRequest(selectFlight), getCustomerUserList().get(i));
                assertNotNull(returnedTicket);
                BookSeatRequest bookSeatRequest1 = new BookSeatRequest(selectFlight.getFlightNumber(),
                        selectFlight.getFlightDate(), i+2);
                returnedTicket = ticketService.bookSeat(bookSeatRequest1, getCustomerUserList().get(i));
                assertNotNull(returnedTicket);
            }
            System.out.println("Booked seats for ticket");
            System.out.println("Before Suite Function. Thread ID: " + Thread.currentThread().getId());

    }
}