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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.TransactionStatus;

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
    public static HashMap<String, Long> concurrentFlightList = new HashMap<>();

    public  List<Long> getFlightList() {
        return flightList;
    }

    public  void setFlightList(List<Long> flightList) {
        CucumberDataGenerator.flightList = flightList;
    }

    public  HashMap<String, Long> getAdminUserList() {
        return adminUserList;
    }

    public HashMap<String, List<MvcResult>> concurrentResponses = new HashMap<>();

    private static HashMap<String, Integer> checkpointList = new HashMap<>();

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

    public synchronized void addConcurrentFlight(String testName, long flightNumber){
        concurrentFlightList.put(testName, flightNumber);
    }

    public List<MvcResult> getConcurrentResult(String key){
        return concurrentResponses.get(key);
    }

    public void addConcurrentResult(String key, MvcResult result){
        List<MvcResult> results = concurrentResponses.getOrDefault(key, new ArrayList<>());
        results.add(result);
        concurrentResponses.put(key, results);
    }

    public void addCheckpoint(String key, Integer value){
        checkpointList.put(key, value);
    }

    public Integer getCheckpointValue(String key){
        return checkpointList.get(key);
    }

}
