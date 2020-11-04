package com.guanhong.airlinebooksystem.cucumber.stepdefs;

import com.guanhong.airlinebookingsystem.entity.FlightRoute;
import com.guanhong.airlinebookingsystem.repository.FlightRepository;
import com.guanhong.airlinebookingsystem.repository.FlightRouteRepository;
import com.guanhong.airlinebookingsystem.service.FlightService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.bs.A;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.sl.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrencyStepDef {

    @Autowired
    private FlightService flightService;
    @Autowired
    private FlightRouteRepository flightRouteRepository;



    private static CucumberDataGenerator dataGenerator = CucumberDataGenerator.getInstance();

    @Given("Concurrency scenario set up the checkpoint {string}")
    public void set_checkpoint(String checkpointStr) {
        if (dataGenerator.getCheckpointValue(checkpointStr) == null){
            dataGenerator.addCheckpoint(checkpointStr, 0);
        }
    }



    @And("Scenario updates the checkpoint {string}")
    public void update_checkpoint(String checkpointStr) {
        if (dataGenerator.getCheckpointValue(checkpointStr) == null) {
            System.out.println("The checkpoint does not exist.");
            assertFalse(true);
        } else {
            dataGenerator.addCheckpoint(checkpointStr, dataGenerator.getCheckpointValue(checkpointStr) + 1);
            System.out.println("Current checkpoint number: " + dataGenerator.getCheckpointValue(checkpointStr));
        }
    }

    @Then("Waiting the checkpoint {string} is finished")
    public void wait_checkpoint(String checkpointStr) throws InterruptedException {
        String[] checkpoint = getCheckpointInfo(checkpointStr);
//        boolean isFinished = false;
        int timeout = 0;
        while ( timeout < 500){
            Integer target = Integer.parseInt(checkpoint[1]);
            Integer current = dataGenerator.getCheckpointValue(checkpointStr);
            if (current == null){
                timeout++;
                Thread.sleep(10);
            }
            if (target.equals(current)){
                System.out.println("All scenario reachs the checkpoint, start next step.");
                return;
            }
            else {
                timeout++;
                Thread.sleep(10);
            }
        }
        System.out.println("The waiting function is timeout. (5 sec)");
        assertFalse(true);
    }

    @Then("^Verify concurrent response by following information$")
    public void verify_concurrent_response(DataTable dt) throws UnsupportedEncodingException {
        Map<String, String> responseInfo = dt.asMap(String.class, String.class);
        String key = responseInfo.get("requestName");
        int expectedSuccessfulAmount = Integer.parseInt(responseInfo.get("successfulNum"));
        int expectedFailedAmount = Integer.parseInt(responseInfo.get("failedNum"));
        int totalAmount = expectedSuccessfulAmount + expectedFailedAmount;
        int failedStatus = Integer.parseInt(responseInfo.get("failedStatus"));
        String expectedFailedMessage = responseInfo.get("expectedFailedMessage");

        List<MvcResult> allResponse = dataGenerator.getConcurrentResult(key);
        assertEquals(totalAmount, allResponse.size());
        int actualSuccessful = 0;
        int actualFailed = 0;
        for (MvcResult response: allResponse) {
            if (response.getResponse().getStatus() == 200){
                actualSuccessful++;
            }
            else {
                actualFailed++;
                assertEquals(expectedFailedMessage, response.getResponse().getContentAsString());
            }
        }
    }



    private String[] getCheckpointInfo(String checkpoint) {
        String[] checkpointInfo = null;
        try {
            if (!checkpoint.contains(":")) {
                throw new Exception("checkpoint string is invalid.");
            }
            checkpointInfo = checkpoint.split(":");
            if (checkpointInfo.length != 2) {
                throw new Exception("checkpoint string is invalid.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertFalse(true);
        }
        return checkpointInfo;
    }


}

