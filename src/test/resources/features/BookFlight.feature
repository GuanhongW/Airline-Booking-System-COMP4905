Feature: Verify the book flight function of the Airline Booking System

  Scenario Outline: Customer user books a flight in the airline booking system - Successful
    Given Printing the thread info for feature "Book Flight" and scenario "Customer user books flight successfully"
    Given "Customer" user 2 logs in to the system
    When User clicks "Book Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    When User select flight "<flightNumber>"
    And User clicks the "Check Flight" button
    Then The server return status code of 200
    Then The server returns all flights by the flight number "<flightNumber>"
    And User enters the flight date in book flight page
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
    And System record the current available ticket amount for following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
    And User clicks the "Book Flight" button
    Then The server return status code of 200
    Then The server return the following response for book flight request
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
    Then User get the following ticket
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
    Then Verify the change of available ticket amount of the following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | tickets      | <tickets>      |

    Examples:
      | flightNumber | flightDate | customer  | seatNumber | tickets |
      | DEFAULT:1    | 85         | DEFAULT:2 | NULL       | 1       |


  Scenario Outline: Customer user books a flight in the airline booking system - Failed
    Given Printing the thread info for feature "Book Flight" and scenario "Customer user books flight failed"
    Given "Customer" user 1 logs in to the system
    When User clicks "Book Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    When User select flight "<flightNumber>"
    And User clicks the "Check Flight" button
    Then The server return status code of 200
    Then The server returns all flights by the flight number "<validFlightNumber>"
    And User enters the flight date in book flight page
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
    And User clicks the "Book Flight" button
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |
    Then User does not get the following ticket
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |

    Examples:
      | validFlightNumber | flightNumber | flightDate | customer  | expectedMessage                                                                                   |
      | DEFAULT:1         | NON-EXISTENT | 85         | DEFAULT:1 | Selected Flight is not exist in the system. Please check the flight number and flight date again. |
      | DEFAULT:1         | 0            | 85         | DEFAULT:1 | Selected Flight is not exist in the system. Please check the flight number and flight date again. |
      | DEFAULT:1         | DEFAULT:1    | 79         | DEFAULT:1 | Selected Flight is not exist in the system. Please check the flight number and flight date again. |

  Scenario Outline: Customer user books a flight in the airline booking system - Failed (The available ticket is not enough)
    Given Printing the thread info for feature "Book Flight" and scenario "Customer user books flight failed (The available ticket is not enough)"
    Given Set up the available ticket amount for the following flight
      | flightNumber     | <flightNumber>     |
      # Flight date is how many days start from today
      | flightDate       | <flightDate>       |
      | availableTickets | <availableTickets> |
    Given "Customer" user 1 logs in to the system
    When User clicks "Book Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    When User select flight "<flightNumber>"
    And User clicks the "Check Flight" button
    Then The server return status code of 200
    Then The server returns all flights by the flight number "<flightNumber>"
    And User enters the flight date in book flight page
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
    And User clicks the "Book Flight" button
    Then The server return status code of 503
    Then The server return the following message
      | expectedMessage | <expectedMessage> |
    Then User does not get the following ticket
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |

    Examples:
      | flightNumber | availableTickets | flightDate | customer  | expectedMessage                                       |
      | DEFAULT:1    | 0                | 85         | DEFAULT:1 | Failed to book the ticket because the flight is full. |
