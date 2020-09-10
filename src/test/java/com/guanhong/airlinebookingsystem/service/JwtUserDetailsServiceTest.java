package com.guanhong.airlinebookingsystem.service;

import com.guanhong.airlinebookingsystem.entity.CustomerInfo;
import com.guanhong.airlinebookingsystem.entity.Gender;
import com.guanhong.airlinebookingsystem.entity.Role;
import com.guanhong.airlinebookingsystem.entity.User;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.repository.CustomerInfoRepository;
import com.guanhong.airlinebookingsystem.repository.UserRepository;


import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@SpringBootTest
class JwtUserDetailsServiceTest {


    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerInfoRepository customerInfoRepository;




    @Test
    @Transactional
    void createAccount_Admin_Success() throws Exception {
        // Test 1: Create a new Admin account
        String testUsername = "admin2";
        AccountInfo newUserInfo = new AccountInfo(testUsername,"adminadmin", Role.ADMIN);
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
        String testUsername = "testuser1@carleton.ca";
        AccountInfo newUserInfo = new AccountInfo(testUsername,"useruser", Role.USER,"test", Gender.male,"2000-01-01");
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
        testUsername = "testuser2@carleton.ca";
        newUserInfo = new AccountInfo(testUsername,"useruser", Role.USER,"test", Gender.female,"2000-01-01");
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
        AccountInfo newUserInfo = new AccountInfo(testUsername,"useruser", Role.USER,"test", Gender.male,"2000-01-01");
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
        newUserInfo = new AccountInfo(testUsername,"useruser", Role.USER,"test", Gender.female,"2000-01-01");
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.USER, user.getRole());
        customerInfo = customerInfoRepository.findById(res.getAccountId()).get();
        assertEquals("test", customerInfo.getName());
        assertEquals("2000-01-01", dateFormat.format(customerInfo.getBirthDate()));
        assertEquals(Gender.female, customerInfo.getGender());

        // Test 2: Create a new User account (Female)
        testUsername = "test@test.ca";
        newUserInfo = new AccountInfo(testUsername,"useruser", Role.USER,"test", Gender.female,"2000-01-01");
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
        String testUsername = "admin1";
        String password = "123456";
        AccountInfo newUserInfo = new AccountInfo(testUsername, password, Role.ADMIN);
        CreateUserResponse res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        User user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        // Test 2: password only have 255 digits
        testUsername = "admin2";
        password = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        newUserInfo = new AccountInfo(testUsername,password, Role.ADMIN);
        res = jwtUserDetailsService.createAccount(newUserInfo);
        assertEquals(testUsername, res.getUsername());
        assertNotNull(res.getAccountId());
        user = userRepository.findById(res.getAccountId()).get();
        assertEquals(Role.ADMIN, user.getRole());

        // Test 3: password only have 15 digits
        testUsername = "admin3";
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
        String testUsername = "test1@carleton.ca";
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
        testUsername = "test2@carleton.ca";
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
        testUsername = "test3@carleton.ca";
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
        String testUsername = "admin1";
        String password = "12345";
        AccountInfo newUserInfo1 = new AccountInfo(testUsername, password, Role.ADMIN);
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo1),"The password should be at least six digits and less than 255 digits.");


        // Test 2: password is more than 255 digits
        password = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        AccountInfo newUserInfo2 = new AccountInfo(testUsername,password, Role.ADMIN);
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo2),"The password should be at least six digits and less than 255 digits.");
    }

    @Test
    @Transactional
    void createAccount_Duplicated_Username_Failed() throws Exception {
        // Test 1: Admin user already exist in the system
        String testUsername = "admin";
        String password = "123456";
        AccountInfo newUserInfo1 = new AccountInfo(testUsername, password, Role.ADMIN);
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo1), "The user already exits in system.");

        // Test 2: Customer user already exist in the system
        testUsername = "string@test.com";
        AccountInfo newUserInfo2 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-01-01");
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo2), "The user already exits in system.");
    }

    @Test
    @Transactional
    void createAccount_Invalid_Username_Failed() throws Exception {
        // Test 1: Invalid email format 1
        String testUsername = "test1.carleton.ca";
        String password = "123456";
        AccountInfo newUserInfo1 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-01-01");
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo1), "The email format is invalid.");

        // Test 2: Invalid email format
        testUsername = "test1@ca..ca";
        AccountInfo newUserInfo2 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-01-01");
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo2), "The email format is invalid.");

        // Test 3: Invalid email format
        testUsername = "testuser";
        AccountInfo newUserInfo3 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-01-01");
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo3), "The email format is invalid.");

        // Test 4: Invalid email format
        testUsername = "test..user@carleton.ca";
        AccountInfo newUserInfo4 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-01-01");
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo4), "The email format is invalid.");

        // Test 5: Invalid email format
        testUsername = "test.user@.carleton.ca";
        AccountInfo newUserInfo5 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-01-01");
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo4), "The email format is invalid.");
    }

    @Test
    @Transactional
    void createAccount_Birthdate_Failed() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Test 1: Invalid birth date (2000-01-32)
        String testUsername = "test1@carleton.ca";
        String password = "123456";
        AccountInfo newUserInfo1 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-01-32");
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo1), "The birth date's format is invalid.");

        // Test 2: Invalid birth date (2001-02-29)
        AccountInfo newUserInfo2 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2001-02-29");
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo2), "The birth date's format is invalid.");

        // Test 3: Invalid birth date (2000-02-30)
        AccountInfo newUserInfo3 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, "2000-02-30");
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo3), "The birth date's format is invalid.");

        // Test 3: Invalid birth date (tomorrow)
        Date tomorrow = tomorrow(new Date());
        AccountInfo newUserInfo4 = new AccountInfo(testUsername,password, Role.USER, "testuser", Gender.male, dateFormat.format(tomorrow));
        assertThrows(Exception.class, ()->jwtUserDetailsService.createAccount(newUserInfo3), "The birth date's format is invalid.");
    }



    private Date tomorrow(Date today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        return calendar.getTime();
    }
}