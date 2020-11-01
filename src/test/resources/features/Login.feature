Feature: Verify the login function of the Airline Booking System

  Scenario: Admin user log in the system successfully with correct username and password
    Given Printing the thread info for feature "Login" and scenario "correct info of admin"
    When User "admintest" login to airline booking system by password "adminadmin"
    Then The server return status code of 200
    Then The JWT token is "not empty"

  Scenario: Customer user log in the system successfully with correct username and password
    Given Printing the thread info for feature "Login" and scenario "correct info of customer"
    When User "test@carleton.ca" login to airline booking system by password "useruser"
    Then The server return status code of 200
    Then The JWT token is "not empty"

  Scenario Outline: Admin user log in the system with incorrect username and password
    Given Printing the thread info for feature "Login" and scenario "incorrect info of admin"
    When User "<userName>" login to airline booking system by password "<password>"
    Then The server return status code of 401
    Then The JWT token is "empty"
    Examples:
      | userName  | password       |
      | admintest | admin2admin2   |
      | admintest | randompassword |

  Scenario Outline: Customer user log in the system with incorrect username and password
    Given Printing the thread info for feature "Login" and scenario "incorrect info of customer"
    When User "<userName>" login to airline booking system by password "<password>"
    Then The server return status code of 401
    Then The JWT token is "empty"
    Examples:
      | userName         | password       |
      | test@carleton.ca | user2user2     |
      | test@carleton.ca | randompassword |