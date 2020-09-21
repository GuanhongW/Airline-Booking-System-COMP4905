package com.guanhong.airlinebookingsystem.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


public class Constants {


    public static long FLIGHT_NUMBER_1 = 1;
    public static long FLIGHT_NUMBER_9999 = 9999;
    public static long FLIGHT_NUMBER_999 = 999;
    public static long FLIGHT_NUMBER_99 = 99;
    public static long FLIGHT_NUMBER_NO_AVAILABLE_SEAT = 9998;
    public static long FLIGHT_NUMBER_EXPIRED = 9997;
    public static long FLIGHT_NUMBER_AVAILABLE = 9996;
    public static long FLIGHT_NUMBER_9995 = 9995;
    public static long FLIGHT_NUMBER_START_DATE_EXPIRED = 9994;

    // Default admin user password
    public static String ADMIN_USER_PASSWORD_0 = "adminadmin1";
    public static String ADMIN_USER_PASSWORD_1 = "adminadmin2";

    // Default customer user password
    public static String CUSTOMER_USER_PASSWORD_0 = "useruser1";
    public static String CUSTOMER_USER_PASSWORD_1 = "useruser2";

    // Unique List
    public static List<Long> flightList = new ArrayList<>();
    public static List<String> adminUserList = new ArrayList<>();
    public static List<String> customerUserList = new ArrayList<>();

    private static boolean isGetNextAdminUsername = false;
    private static boolean isGetNextCustomerUsername = false;


    private Constants(){
        // 0 is not a valid flight number
        flightList.add((long)0);
    }

    private static class SingletonHolder{
        private static final Constants INSTANCE = new Constants();
    }

    public static final Constants getInstance(){
        return SingletonHolder.INSTANCE;
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

    public synchronized String getNextAdminUsername() throws InterruptedException {
        isGetNextAdminUsername = true;
        int adminNumber = adminUserList.size();
        String newAdminUsername = "auto_admin_" + adminNumber;
        adminUserList.add(newAdminUsername);
        return newAdminUsername;
    }

    public synchronized String getNextCustomerUsername() throws InterruptedException {
        int customerNumber = customerUserList.size();
        String newCustomerUsername = "auto_customer_" + customerNumber + "@carleton.ca";
        customerUserList.add(newCustomerUsername);
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

}
