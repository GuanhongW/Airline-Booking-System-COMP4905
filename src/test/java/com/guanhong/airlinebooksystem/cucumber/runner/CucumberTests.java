package com.guanhong.airlinebooksystem.cucumber.runner;


import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

//@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "timeline:target/cucumber-report/timeline/",
        "html:target/cucumber-report/html/report.html", "json:target/cucumber-report/json/cucumber.json"},
        features = "src/test/resources/",
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
