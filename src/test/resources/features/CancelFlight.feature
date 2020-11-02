Feature: Verify the cancel flight function of the Airline Booking System

  Scenario Outline: Admin user cancel an existent flight route in the airline booking system - Successful
    Given Printing the thread info for feature "Cancel Flight Route" and scenario "Admin user cancel flight route successfully"
    Given "Admin" user 1 logs in to the system
    When User clicks "Cancel Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    When Admin user enters following information to cancel new flight route in cancel flight page
      | flightNumber | <flightNumber> |
    And User clicks the "Cancel Flight" button
    Then The server return status code of 200
    Then The select flight does not exist in the system
    Examples:
      | flightNumber |
      | DEFAULT:1    |

  Scenario Outline: Admin user cancel an existent flight route in the airline booking system - Failed
    Given Printing the thread info for feature "Cancel Flight Route" and scenario "Admin user cancel flight route successfully"
    Given "Admin" user 1 logs in to the system
    When User clicks "Cancel Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    When Admin user enters following information to cancel new flight route in cancel flight page
      | flightNumber | <flightNumber> |
    And User clicks the "Cancel Flight" button
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |
    Then The select flight does not exist in the system
    Examples:
      | flightNumber | expectedMessage                                |
      | NON_EXISTENT | The flight route is unavailable in the system. |
      | 0            | The flight route is unavailable in the system. |
      | 10000        | The flight route is unavailable in the system. |
      | -1        | The flight route is unavailable in the system. |







