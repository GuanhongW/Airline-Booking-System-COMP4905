package com.guanhong;




import com.guanhong.airlinebookingsystem.AirlineBookingSystemApplication;

import com.guanhong.airlinebookingsystem.entity.*;
import com.guanhong.airlinebookingsystem.model.AccountInfo;
import com.guanhong.airlinebookingsystem.model.BookSeatRequest;
import com.guanhong.airlinebookingsystem.model.CreateUserResponse;
import com.guanhong.airlinebookingsystem.model.FlightRequest;
import com.guanhong.airlinebookingsystem.repository.CustomerInfoRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRouteRepository;
import com.guanhong.airlinebookingsystem.repository.UserRepository;
import com.guanhong.airlinebookingsystem.service.FlightService;
import com.guanhong.airlinebookingsystem.service.JwtUserDetailsService;
import com.guanhong.airlinebookingsystem.service.TicketService;
import com.guanhong.airlinebooksystem.cucumber.stepdefs.CucumberDataGenerator;
import io.cucumber.java.Before;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "timeline:target/cucumber-report/timeline/",
        "html:target/cucumber-report/html/report.html", "json:target/cucumber-report/json/cucumber.json"},
        features = "src/test/resources/features",
        glue = "com.guanhong.airlinebooksystem.cucumber.stepdefs")
//@SpringBootTest
//@ContextConfiguration(classes = AirlineBookingSystemApplication.class)
public class CucumberTests extends  AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }


}
