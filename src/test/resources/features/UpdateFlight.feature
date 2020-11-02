Feature: Verify the update flight function of the Airline Booking System

  Scenario Outline: Admin user update an existent flight route in the airline booking system - Successful
    Given Printing the thread info for feature "Cancel Flight Route" and scenario "Admin user cancel flight route successfully"
    Given "Admin" user 1 logs in to the system
    When User clicks "Update Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    When Admin user enters following information of a existent flight route in update flight page
      # Flight Number will be ignored and use the auto generated one from dataGenerator
      | flightNumber    | <flightNumber>    |
      | departureCity   | <departureCity>   |
      | destinationCity | <destinationCity> |
      | aircraftId      | <aircraftId>      |
      # startDate and endDate is how many day start from today. It also can be negative
      | startDate       | <startDate>       |
      | endDate         | <endDate>         |
      | departureTime   | <departureTime>   |
      | arrivalTime     | <arrivalTime>     |
      | overbooking     | <overbooking>     |
    And User clicks the "Update Flight" button
    Then The server return status code of 200
    Then The server return the following response for update flight request
      # Flight Number will be ignored and use the auto generated one from dataGenerator
      | flightNumber    | <flightNumber>    |
      | departureCity   | <departureCity>   |
      | destinationCity | <destinationCity> |
      | aircraftId      | <aircraftId>      |
      # startDate and endDate is how many day start from today. It also can be negative
      | startDate       | <startDate>       |
      | endDate         | <endDate>         |
      | departureTime   | <departureTime>   |
      | arrivalTime     | <arrivalTime>     |
      | overbooking     | <overbooking>     |
    Then The selected flight in the database have the following information
      # Flight Number will be ignored and use the auto generated one from dataGenerator
      | flightNumber    | <flightNumber>    |
      | departureCity   | <departureCity>   |
      | destinationCity | <destinationCity> |
      | aircraftId      | <aircraftId>      |
      # startDate and endDate is how many day start from today. It also can be negative
      | startDate       | <startDate>       |
      | endDate         | <endDate>         |
      | departureTime   | <departureTime>   |
      | arrivalTime     | <arrivalTime>     |
      | overbooking     | <overbooking>     |
    Examples:
      | flightNumber | departureCity | destinationCity | aircraftId | startDate | endDate | departureTime | arrivalTime | overbooking |
      | DEFAULT:1    | YOW           | YUL             | 737        | 30        | 35      | 18:00:00      | 18:50:00    | 8           |
      | DEFAULT:2    | YOW           | YVR             | 900        | 80        | 100     | 10:05:00      | 12:00:00    | 6           |
      | DEFAULT:3    | YYZ           | YUL             | 900        | 80        | 100     | 10:05:00      | 12:00:00    | 6           |
      | DEFAULT:4    | YYZ           | YVR             | 900        | 80        | 100     | 11:01:00      | 12:00:00    | 6           |
      | DEFAULT:5    | YYZ           | YVR             | 900        | 80        | 100     | 10:05:00      | 11:57:00    | 6           |
      | DEFAULT:4    | YYZ           | YVR             | 777        | 80        | 100     | 10:05:00      | 12:00:00    | 6           |
      | DEFAULT:3    | YYZ           | YVR             | 900        | 80        | 100     | 10:05:00      | 12:00:00    | 10          |
      | DEFAULT:2    | YYZ           | YVR             | 900        | 90        | 100     | 10:05:00      | 12:00:00    | 6           |
      | DEFAULT:1    | YYZ           | YVR             | 900        | 80        | 82      | 10:05:00      | 12:00:00    | 6           |

  Scenario Outline: Admin user update an existent flight route in the airline booking system - Failed
    Given Printing the thread info for feature "Cancel Flight Route" and scenario "Admin user cancel flight route successfully"
    Given "Admin" user 1 logs in to the system
    When User clicks "Update Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    And User records the original flight information for flight "<flightNumber>"
    When Admin user enters following information of a existent flight route in update flight page
      # Flight Number will be ignored and use the auto generated one from dataGenerator
      | flightNumber    | <flightNumber>    |
      | departureCity   | <departureCity>   |
      | destinationCity | <destinationCity> |
      | aircraftId      | <aircraftId>      |
      # startDate and endDate is how many day start from today. It also can be negative
      | startDate       | <startDate>       |
      | endDate         | <endDate>         |
      | departureTime   | <departureTime>   |
      | arrivalTime     | <arrivalTime>     |
      | overbooking     | <overbooking>     |
    And User clicks the "Update Flight" button
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |
    Then The selected flight does not change

    Examples:
      | flightNumber | departureCity | destinationCity | aircraftId | startDate | endDate | departureTime | arrivalTime | overbooking | expectedMessage                                                      |
      | DEFAULT:1    | YOW           | YYZ             | 797        | 30        | 35      | 18:00:00      | 18:50:00    | 8           | Flight's aircraft is invalid.                                        |
      | DEFAULT:2    | YYZ           | YVR             | 900        | -1        | 5       | 10:05:00      | 12:00:00    | 6           | The start of travel range should not before today.                   |
      | DEFAULT:3    | YYZ           | YVR             | 900        | 80        | 79      | 10:05:00      | 12:00:00    | 6           | The end of travel range should not before the start of travel range. |
      | DEFAULT:4    | YYZ           | YVR             | 900        | 80        | 82      | 10:05:00      | 12:00:00    | -0.01       | Flight's overbooking allowance should between 0% to 10%              |
      | DEFAULT:5    | YYZ           | YVR             | 900        | 80        | 82      | 10:05:00      | 12:00:00    | 10.01       | Flight's overbooking allowance should between 0% to 10%              |

  Scenario Outline: Admin user update an existent flight route in the airline booking system - Failed (NON-EXISTENT)
    Given Printing the thread info for feature "Cancel Flight Route" and scenario "Admin user cancel flight route successfully"
    Given "Admin" user 1 logs in to the system
    When User clicks "Update Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    When Admin user enters following information of a existent flight route in update flight page
      # Flight Number will be ignored and use the auto generated one from dataGenerator
      | flightNumber    | <flightNumber>    |
      | departureCity   | <departureCity>   |
      | destinationCity | <destinationCity> |
      | aircraftId      | <aircraftId>      |
      # startDate and endDate is how many day start from today. It also can be negative
      | startDate       | <startDate>       |
      | endDate         | <endDate>         |
      | departureTime   | <departureTime>   |
      | arrivalTime     | <arrivalTime>     |
      | overbooking     | <overbooking>     |
    And User clicks the "Update Flight" button
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |
    Then The select flight does not exist in the system

    Examples:
      | flightNumber | departureCity | destinationCity | aircraftId | startDate | endDate | departureTime | arrivalTime | overbooking | expectedMessage                                 |
      | 9993         | YYZ           | YVR             | 900        | 80        | 82      | 10:05:00      | 12:00:00    | 6           | The flight 9993 does not existed in the system. |
      | 0            | YYZ           | YVR             | 900        | 80        | 82      | 10:05:00      | 12:00:00    | 6           | Flight number is empty or invalid.              |
      | 10000        | YYZ           | YVR             | 900        | 80        | 82      | 10:05:00      | 12:00:00    | 6           | Flight number is empty or invalid.              |



