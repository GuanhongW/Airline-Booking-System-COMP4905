Feature: Verify the book seat function of the Airline Booking System

  Scenario Outline: Customer user books a seat in the airline booking system - Successful
    Given Printing the thread info for feature "Book Seat" and scenario "Customer user books seat failed (Successful)"
    Given User book the following ticket and seat
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
    And System record the current available ticket amount for following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
    Given "Customer" user 1 logs in to the system
    When User clicks "Cancel Ticket" in side menu
    Then The server return status code of 200
    And User enters the following flight in cancel ticket page
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
    And User clicks the "Cancel Ticket" button
    Then The server return status code of 200
    Then The server return the following message
      | expectedMessage | true |
    Then User does not get the following ticket
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | NULL           |
    Then Verify the seat status in following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
      | seatStatus   | AVAILABLE      |
    Then Verify the change of available ticket amount of the following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | tickets      | <tickets>      |

    Examples:
      | customer  | flightNumber | flightDate | seatNumber | tickets |
      | DEFAULT:1 | DEFAULT:1    | 88         | 10         | -1      |

  Scenario Outline: Customer user books a seat in the airline booking system - Failed (unreserved flight)
    Given Printing the thread info for feature "Book Seat" and scenario "Customer user books seat failed (unreserved flight)"
    And System record the current available ticket amount for following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
    Given "Customer" user 1 logs in to the system
    When User clicks "Cancel Ticket" in side menu
    Then The server return status code of 200
    And User enters the following flight in cancel ticket page
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
    And User clicks the "Cancel Ticket" button
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |
    Then User does not get the following ticket
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | NULL           |
    Then Verify the seat status in following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
      | seatStatus   | AVAILABLE      |
    Then Verify the change of available ticket amount of the following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | tickets      | <tickets>      |

    Examples:
      | customer  | flightNumber | flightDate | seatNumber | tickets | expectedMessage              |
      | DEFAULT:1 | DEFAULT:1    | 88         | 10         | 0       | You do not book this flight. |
      | DEFAULT:1 | DEFAULT:2    | 99         | 1          | 0       | You do not book this flight. |

  Scenario Outline: Customer user books a seat in the airline booking system - Failed (non-existent and invalid flight)
    Given Printing the thread info for feature "Book Seat" and scenario "Customer user books seat failed (non-existent and invalid flight)"
    Given "Customer" user 1 logs in to the system
    When User clicks "Cancel Ticket" in side menu
    Then The server return status code of 200
    And User enters the following flight in cancel ticket page
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
    And User clicks the "Cancel Ticket" button
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |
    Then User does not get the following ticket
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | NULL           |

    Examples:
      | customer  | flightNumber | flightDate | expectedMessage                          |
      | DEFAULT:1 | NON-EXISTENT | 88         | The flight does not exist in the system. |
      | DEFAULT:1 | DEFAULT:1    | 50         | The flight does not exist in the system. |
      | DEFAULT:1 | 0            | 82         | The flight does not exist in the system. |
      | DEFAULT:1 | -1           | 82         | The flight does not exist in the system. |
      | DEFAULT:1 | 10000        | 82         | The flight does not exist in the system. |



