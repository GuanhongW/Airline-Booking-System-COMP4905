Feature: Verify the book seat function of the Airline Booking System

  Scenario Outline: Customer user books a seat in the airline booking system - Successful
    Given Printing the thread info for feature "Book Seat" and scenario "Customer user books seat successfully"
    Given "Customer" user 2 logs in to the system
    Given User books the following flight
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
    When User clicks "Book Seat" in side menu
    Then The server return status code of 200
    Then Verify the following ticket status in the server response
      | flightNumber    | <flightNumber> |
      | flightDate      | <flightDate>   |
      | existInResponse | TRUE           |
    And User enters the seat in book seat page
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
    And User clicks the "Book Seat" button
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
    Then Verify the seat status in following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
      | seatStatus   | BOOKED         |

    Examples:
      | customer  | flightNumber | flightDate | seatNumber |
      | DEFAULT:2 | DEFAULT:3    | 88         | 10         |

  Scenario Outline: Customer user books a seat in the airline booking system - Failed (Unreserved flight)
    Given Printing the thread info for feature "Book Seat" and scenario "Customer user books seat failed (Unreserved flight)"
    Given "Customer" user 2 logs in to the system
    When User clicks "Book Seat" in side menu
    Then The server return status code of 200
    Then Verify the following ticket status in the server response
      | flightNumber    | <flightNumber> |
      | flightDate      | <flightDate>   |
      | existInResponse | FALSE          |
    And User enters the seat in book seat page
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
    And User clicks the "Book Seat" button
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |
    Then User does not get the following ticket
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
    Then Verify the seat status in following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
      | seatStatus   | AVAILABLE      |

    Examples:
      | customer  | flightNumber | flightDate | seatNumber | expectedMessage                                    |
      | DEFAULT:2 | DEFAULT:3    | 88         | 10         | You have to book the ticket before booking a seat. |

  Scenario Outline: Customer user books a seat in the airline booking system - Failed (Invalid flight number)
    Given Printing the thread info for feature "Book Seat" and scenario "Customer user books seat failed (Invalid flight number)"
    Given "Customer" user 2 logs in to the system
    When User clicks "Book Seat" in side menu
    Then The server return status code of 200
    And User enters the seat in book seat page
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
    And User clicks the "Book Seat" button
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |
    Then User does not get the following ticket
      | customer     | <customer>     |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |

    Examples:
      | customer  | flightNumber | flightDate | seatNumber | expectedMessage                            |
      | DEFAULT:2 | 0            | 88         | 10         | The flight does not existed in the system. |
      | DEFAULT:2 | NON-EXISTENT | 89         | 10         | The flight does not existed in the system. |
      | DEFAULT:2 | DEFAULT:1    | 101        | 10         | The flight does not existed in the system. |

  Scenario Outline: Customer user books a seat in the airline booking system - Failed (reserved seat number)
    Given Printing the thread info for feature "Book Seat" and scenario "Customer user books seat failed (reserved seat number)"
    Given User book the following ticket and seat
      | customer     | <customer1>    |
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
    Given "Customer" user 2 logs in to the system
    Given User books the following flight
      | customer     | <customer2>     |
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
    When User clicks "Book Seat" in side menu
    Then The server return status code of 200
    Then Verify the following ticket status in the server response
      | flightNumber    | <flightNumber> |
      | flightDate      | <flightDate>   |
      | existInResponse | TRUE           |
    And User enters the seat in book seat page
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
      | seatNumber   | <seatNumber>   |
    And User clicks the "Book Seat" button
    Then The server return status code of 400
    Then The server return the following failed message for book seat request because of reserved seat
      | flightNumber | <flightNumber> |
      | seatNumber   | <seatNumber>   |
    Then User get the following ticket
      | customer     | <customer2>    |
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | seatNumber   | NULL           |

    Examples:
      | customer1 | customer2 | flightNumber | flightDate | seatNumber |
      | DEFAULT:1 | DEFAULT:2 | DEFAULT:1    | 88         | 10         |


