package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.Exception.ClientException;
import com.guanhong.airlinebookingsystem.Exception.ServerException;
import com.guanhong.airlinebookingsystem.config.JwtTokenUtil;
import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.*;
import com.guanhong.airlinebookingsystem.repository.CustomerInfoRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRouteRepository;
import com.guanhong.airlinebookingsystem.repository.FlightSeatInfoRepository;
import com.guanhong.airlinebookingsystem.repository.UserRepository;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;


import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootTest
class JwtUserDetailsServiceTest {



    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerInfoRepository customerInfoRepository;


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
        java.sql.Date startDate = constants.datePlusSomeDays(constants.today(), 80);
        java.sql.Date endDate = constants.datePlusSomeDays(constants.today(), 180);
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
    void createAccount_Admin_Success() throws Exception {
        // Test 1: Create a new Admin account
        String testUsername = constants.getNextAdminUsername();
        AccountInfo newUserInfo = new AccountInfo(testUsername,constants.ADMIN_USER_PASSWORD_0, Role.ADMIN);
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    @Transactional
    void createAccount_Gender_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Test 1: Create a new User account (Male)
        String testUsername = constants.getNextCustomerUsername();
        AccountInfo newUserInfo = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER,"test", Gender.male,"2000-01-01");
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        CustomerInfo customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

        // Test 2: Create a new User account (Female)
        testUsername = constants.getNextCustomerUsername();
        newUserInfo = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER,"test", Gender.female,"2000-01-01");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.female, customerInfo.getGender());
    }

    @Test
    @Transactional
    void createAccount_Email_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Test 1: valid email address
        String testUsername = "testuser1@cmail.carleton.ca";
        AccountInfo newUserInfo = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER,"test", Gender.male,"2000-01-01");
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        CustomerInfo customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

        // Test 2: Create a new User account (Female)
        testUsername = "test.user2@carleton.ca";
        newUserInfo = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER,"test", Gender.female,"2000-01-01");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.female, customerInfo.getGender());

        // Test 3: Create a new User account (Female)
        testUsername = "test@test.ca";
        newUserInfo = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER,"test", Gender.female,"2000-01-01");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.female, customerInfo.getGender());
    }

    @Test
    @Transactional
    void createAccount_Password_Success() throws Exception {
        // Test 1: password only have 6 digits
        String testUsername = constants.getNextAdminUsername();
        String password = "123456";
        AccountInfo newUserInfo = new AccountInfo(testUsername, password, Role.ADMIN);
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        // Test 2: password only have 255 digits
        testUsername = constants.getNextAdminUsername();
        password = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newUserInfo = new AccountInfo(testUsername,password, Role.ADMIN);
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        // Test 3: password only have 15 digits
        testUsername = constants.getNextAdminUsername();
        password = "password+PASSWORD";
        newUserInfo = new AccountInfo(testUsername,password, Role.ADMIN);
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    @Transactional
    void createAccount_Birthdate_Success() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Test 1: Use valid birth date 2000-01-31
        String testUsername = constants.getNextCustomerUsername();
        String password = "123456";
        AccountInfo newUserInfo = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-01-31");
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        CustomerInfo customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("testuser", customerInfo.getName());
        assertEquals("2000-01-31", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

        // Test 2: Use valid birth date 2000-02-29
        testUsername = constants.getNextCustomerUsername();
        newUserInfo = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-02-29");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("testuser", customerInfo.getName());
        assertEquals("2000-02-29", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

        // Test 3: Use valid birth date today
        testUsername = constants.getNextCustomerUsername();
        Date today = new Date();
        String todayStr = dateFormat.format(today);
        newUserInfo = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, todayStr);
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("testuser", customerInfo.getName());
        assertEquals(todayStr, dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.male, customerInfo.getGender());

    }

    @Test
    @Transactional
    void createAccount_Password_Failed() throws Exception {
        // Test 1: password only have 5 digits
        String testUsername = constants.getNextAdminUsername();
        String password = "12345";
        AccountInfo newUserInfo1 = new AccountInfo(testUsername, password, Role.ADMIN);
        ClientException exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo1));
        assertEquals("The password should be at least six digits and less than 255 digits.", exception.getMessage());


        // Test 2: password is more than 255 digits
        password = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        AccountInfo newUserInfo2 = new AccountInfo(testUsername,password, Role.ADMIN);
        exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo2));
        assertEquals("The password should be at least six digits and less than 255 digits.", exception.getMessage());
    }

    @Test
    @Transactional
    void createAccount_Duplicated_Username_Failed() throws Exception {
        // Test 1: Admin user already exist in the system
        String testUsername = defaultAdminUsernames.get(0);
        String password = "123456";
        AccountInfo newUserInfo1 = new AccountInfo(testUsername, password, Role.ADMIN);
        ClientException exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo1));
        assertEquals("The user already exits in system.", exception.getMessage());

        // Test 2: Customer user already exist in the system
        testUsername = defaultCustomerUsernames.get(0);
        AccountInfo newUserInfo2 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-01-01");
        exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo2));
        assertEquals("The user already exits in system.", exception.getMessage());
    }

    @Test
    @Transactional
    void createAccount_Invalid_Username_Failed() throws Exception {
        // Test 1: Invalid email format 1
        String testUsername = "test1.carleton.ca";
        AccountInfo newUserInfo1 = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "testuser", Gender.male, "2000-01-01");
        ClientException exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo1));
        assertEquals("The email format is invalid.",exception.getMessage());

        // Test 2: Invalid email format
        testUsername = "test1@ca..ca";
        AccountInfo newUserInfo2 = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "testuser", Gender.male, "2000-01-01");
        exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo2));
        assertEquals("The email format is invalid.",exception.getMessage());

        // Test 3: Invalid email format
        testUsername = "testuser";
        AccountInfo newUserInfo3 = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "testuser", Gender.male, "2000-01-01");
        exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo3));
        assertEquals("The email format is invalid.",exception.getMessage());

        // Test 4: Invalid email format
        testUsername = "test..user@carleton.ca";
        AccountInfo newUserInfo4 = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "testuser", Gender.male, "2000-01-01");
        exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo4));
        assertEquals("The email format is invalid.",exception.getMessage());

        // Test 5: Invalid email format
        testUsername = "test.user@.carleton.ca";
        AccountInfo newUserInfo5 = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "testuser", Gender.male, "2000-01-01");
        exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo4));
        assertEquals("The email format is invalid.",exception.getMessage());
    }

    @Test
    @Transactional
    void createAccount_Birthdate_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Test 1: Invalid birth date (2000-01-32)
        String testUsername = constants.getNextCustomerUsername();
        AccountInfo newUserInfo1 = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "testuser", Gender.male, "2000-01-32");
        ClientException exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo1));
        assertEquals("The birth date's format is invalid.", exception.getMessage());

        // Test 2: Invalid birth date (2001-02-29)
        AccountInfo newUserInfo2 = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "testuser", Gender.male, "2001-02-29");
        exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo2));
        assertEquals("The birth date's format is invalid.", exception.getMessage());

        // Test 3: Invalid birth date (2000-02-30)
        AccountInfo newUserInfo3 = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "testuser", Gender.male, "2000-02-30");
        exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo3));
        assertEquals("The birth date's format is invalid.", exception.getMessage());

        // Test 3: Invalid birth date (tomorrow)
        Date tomorrow = tomorrow(new Date());
        AccountInfo newUserInfo4 = new AccountInfo(testUsername,constants.CUSTOMER_USER_PASSWORD_0, Role.USER, "testuser", Gender.male, dateFormat.format(tomorrow));
        exception = assertThrows(ClientException.class, ()->jwtUserDetailsService.createAccount(newUserInfo3));
        assertEquals("The birth date's format is invalid.", exception.getMessage());
    }

    @Test
    @Transactional
    void authUser_Success() throws Exception {
        // Test 1: Auth a Admin account
        UserCredential userCredential = new UserCredential(defaultAdminUsernames.get(0), constants.ADMIN_USER_PASSWORD_0);
        UserLoginResponse userLoginResponse = jwtUserDetailsService.authUser(userCredential);
        assertEquals(defaultAdminUsernames.get(0), userLoginResponse.getUsername());
        assertNotNull(userLoginResponse.getJwttoken());
        assertEquals(defaultAdminUsernames.get(0), jwtTokenUtil.getUsernameFromToken(userLoginResponse.getJwttoken()));

        // Test 2: Auth a Customer account
        userCredential = new UserCredential(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_0);
        userLoginResponse = jwtUserDetailsService.authUser(userCredential);
        assertEquals(defaultCustomerUsernames.get(0), userLoginResponse.getUsername());
        assertNotNull(userLoginResponse.getJwttoken());
        assertEquals(defaultCustomerUsernames.get(0), jwtTokenUtil.getUsernameFromToken(userLoginResponse.getJwttoken()));
    }

    @Test
    @Transactional
    void authUser_Failed() throws Exception {
        // Test 1: Auth a Admin account with random password
        UserCredential userCredential1 = new UserCredential(defaultAdminUsernames.get(0), "adminadminwrong");
        Exception exception = assertThrows(Exception.class, ()->jwtUserDetailsService.authUser(userCredential1));
        assertEquals("INVALID_CREDENTIALS", exception.getMessage());

        // Test 2: Auth a Admin account with other password
        UserCredential userCredential2 = new UserCredential(defaultAdminUsernames.get(1), constants.ADMIN_USER_PASSWORD_0);
        exception = assertThrows(Exception.class, ()->jwtUserDetailsService.authUser(userCredential2));
        assertEquals("INVALID_CREDENTIALS", exception.getMessage());

        // Test 3: Auth a admin account with unknown user name
        UserCredential userCredential3 = new UserCredential("unknownautoadmin", constants.ADMIN_USER_PASSWORD_0);
        exception = assertThrows(Exception.class, ()->jwtUserDetailsService.authUser(userCredential3));
        assertEquals("INVALID_CREDENTIALS", exception.getMessage());

        // Test 4: Auth a Customer account with random password
        UserCredential userCredential4 = new UserCredential(defaultCustomerUsernames.get(0), "useruserwrong");
        exception = assertThrows(Exception.class, ()->jwtUserDetailsService.authUser(userCredential4));
        assertEquals("INVALID_CREDENTIALS", exception.getMessage());

        // Test 5: Auth a Customer account with other password
        UserCredential userCredential5 = new UserCredential(defaultCustomerUsernames.get(0), constants.CUSTOMER_USER_PASSWORD_1);
        exception = assertThrows(Exception.class, ()->jwtUserDetailsService.authUser(userCredential5));
        assertEquals("INVALID_CREDENTIALS", exception.getMessage());

        // Test 6: Auth a Customer account with unknown user name
        UserCredential userCredential6 = new UserCredential("unknownauto@test.com", constants.CUSTOMER_USER_PASSWORD_1);
        assertThrows(Exception.class, ()->jwtUserDetailsService.authUser(userCredential6));
        assertEquals("INVALID_CREDENTIALS", exception.getMessage());
    }

    @Test
    @Transactional
    void getUserRoleTest_Success() throws Exception{
        assertEquals(Role.ADMIN, jwtUserDetailsService.getUserRole(defaultAdminUsernames.get(0)));
        assertEquals(Role.ADMIN, jwtUserDetailsService.getUserRole(defaultAdminUsernames.get(1)));
        assertEquals(Role.USER, jwtUserDetailsService.getUserRole(defaultCustomerUsernames.get(0)));
        assertEquals(Role.USER, jwtUserDetailsService.getUserRole(defaultCustomerUsernames.get(1)));
    }

    @Test
    @Transactional
    void getUserRoleTest_Failed(){
        ServerException exception = assertThrows(ServerException.class, ()->jwtUserDetailsService.getUserRole("WRONGUSER"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
        String expectedMessage = "User cannot be found in database.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    private Date tomorrow(Date today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        return calendar.getTime();
    }
}