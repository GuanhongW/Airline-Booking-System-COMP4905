Feature: Verify the create new flight route function of the Airline Booking System

  Scenario Outline: Admin user create a new flight route in the airline booking system - Successful
    Given Printing the thread info for feature "Create Flight Route" and scenario "Admin user creates flight route successfully"
    Given "Admin" user 1 logs in to the system
    When Admin user enters following information of a new flight route in create flight page
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
    And User clicks the "Create Flight" button
    Then The server return status code of 200
    And The server return the following response for create flight request
      # Flight Number will be ignored and use the auto generated one from dataGenerator
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
      | NEXT         | YOW           | YYZ             | 200        | 20        | 30      | 18:00:00      | 19:10:00    | 6           |

  Scenario Outline: Admin user create a new flight route in the airline booking system - Failed (duplicated)
    Given Printing the thread info for feature "Create Flight Route" and scenario "Admin user creates flight route failed (duplicated)"
    Given "Admin" user 1 logs in to the system
    When Admin user enters following information of a new flight route in create flight page
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
    And User clicks the "Create Flight" button
    Then The server return status code of 400
    And The server return the following message
      | expectedMessage | <expectedMessage> |
    And The select flight exist in the system

    Examples:
      | flightNumber | departureCity | destinationCity | aircraftId | startDate | endDate | departureTime | arrivalTime | overbooking | expectedMessage                    |
      | DUPLICATED   | YOW           | YYZ             | 200        | 20        | 30      | 18:00:00      | 19:10:00    | 6           | The flight number already be used. |

  Scenario Outline: Admin user create a new flight route in the airline booking system - Failed
    Given Printing the thread info for feature "Create Flight Route" and scenario "Admin user creates flight route failed"
    Given "Admin" user 1 logs in to the system
    When Admin user enters following information of a new flight route in create flight page
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
    And User clicks the "Create Flight" button
    Then The server return status code of 400
    And The server return the following message
      | expectedMessage | <expectedMessage> |
    And The select flight does not exist in the system

    Examples:
      | flightNumber | departureCity | destinationCity | aircraftId | startDate | endDate | departureTime | arrivalTime | overbooking | expectedMessage                                                      |
      | -1           | YOW           | YYZ             | 200        | 20        | 30      | 18:00:00      | 19:10:00    | 6           | The flight number should not excess 4 digits.                        |
      | 0            | YOW           | YYZ             | 200        | 20        | 30      | 18:00:00      | 19:10:00    | 6           | The flight number should not excess 4 digits.                        |
      | 10000        | YOW           | YYZ             | 200        | 20        | 30      | 18:00:00      | 19:10:00    | 6           | The flight number should not excess 4 digits.                        |
      | NEXT         | YOW           | YYZ             | 325        | 20        | 30      | 18:00:00      | 19:10:00    | 6           | Flight's aircraft is invalid.                                        |
      | NEXT         | YOW           | YYZ             | 200        | -1        | 30      | 18:00:00      | 19:10:00    | 6           | The start of travel range should not before today.                   |
      | NEXT         | YOW           | YYZ             | 200        | 31        | 30      | 18:00:00      | 19:10:00    | 6           | The end of travel range should not before the start of travel range. |
      | NEXT         | YOW           | YYZ             | 200        | 20        | 30      | 18:00:00      | 19:10:00    | -0.1        | Flight's overbooking allowance should between 0% to 10%              |
      | NEXT         | YOW           | YYZ             | 200        | 20        | 30      | 18:00:00      | 19:10:00    | 10.01       | Flight's overbooking allowance should between 0% to 10%              |

