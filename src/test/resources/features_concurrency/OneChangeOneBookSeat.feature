Feature: One customer user tries to change the seat  and release the target seat,
  and another customer user tries to book the seat at the same time

  Scenario Outline: Setup the flight for one change and one book request at the same time
    Given Printing the thread info for feature "Book Flight Concurrency" and scenario "Setup concurrency flight"
    Given Concurrency scenario set up the checkpoint "<setupCheckpoint>"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Then System create a flight for concurrency test "OneChangeOneBookSeatFlight"
    And Commit current transaction to database
    And Scenario updates the checkpoint "<setupCheckpoint>"
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished by each 20 ms
    Examples:
      | setupCheckpoint              | waitAllScenario        |
      | OneChangeOneBookSeatFlight:1 | OneChangeOneBookSeat:3 |


  Scenario Outline: Customer 1 try to book the seat 15
    Given Printing the thread info for feature "Book Flight Concurrency" and scenario "Test1"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookedCheckpoint>"
    Given Concurrency scenario set up the checkpoint "<waitBookCheckpoint>"
    And Commit current transaction to database
    Then Waiting the checkpoint "<setupCheckpoint>" is finished by each 20 ms
    Given "Customer" user 1 logs in to the system
    When User clicks "Book Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    Then The flight "<flightNumber>" includes in get flight routes request
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
      | customer     | <customer>       |
      | flightNumber | <flightNumber>   |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>     |
      | seatNumber   | <initSeatNumber> |
    Then User get the following ticket
      | customer     | <customer>       |
      | flightNumber | <flightNumber>   |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>     |
      | seatNumber   | <initSeatNumber> |
    When User clicks "Book Seat" in side menu
    Then The server return status code of 200
    Then Verify the following ticket status in the server response
      | flightNumber    | <flightNumber> |
      | flightDate      | <flightDate>   |
      | existInResponse | TRUE           |
    And User enters the seat in book seat page
      | flightNumber | <flightNumber>     |
      | flightDate   | <flightDate>       |
      | seatNumber   | <selectSeatNumber> |
    And Scenario updates the checkpoint "<waitBookCheckpoint>"
    Then Waiting the checkpoint "<waitBookCheckpoint>" is finished by each 40 ms
#    And Waiting 15 ms
    And User clicks the "Book Seat" button
    And Save the current response into concurrent list "<responseName>"
    And Scenario updates the checkpoint "<bookedCheckpoint>"
    And Waiting the checkpoint "<bookedCheckpoint>" is finished by each 40 ms
    And Verify concurrent response by following information
      | responseName          | <responseName>          |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished by each 20 ms

    Examples:
      | flightNumber               | flightDate | customer  | selectSeatNumber | initSeatNumber | responseName         | successfulNum | failedNum | failedStatus | expectedFailedMessage  | bookedCheckpoint    | waitAllScenario        | setupCheckpoint              | waitBookCheckpoint     |
      | OneChangeOneBookSeatFlight | 85         | DEFAULT:1 | 15               | NULL           | OneChangeOneBookSeat | 2/1           | 0/1       | 200/400      | NULL/is not available. | ChangedBookedSeat:2 | OneChangeOneBookSeat:3 | OneChangeOneBookSeatFlight:1 | BeforeChangeBookSeat:2 |

  Scenario Outline: Customer 2 try to change the seat from 15 to 22
    Given Printing the thread info for feature "Book Flight Concurrency" and scenario "Test1"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookedCheckpoint>"
    Given Concurrency scenario set up the checkpoint "<waitBookCheckpoint>"
    And Commit current transaction to database
    Then Waiting the checkpoint "<setupCheckpoint>" is finished by each 20 ms
    Given "Customer" user 2 logs in to the system
    When User clicks "Book Flight" in side menu
    Then The server return status code of 200
    Then The flight "DEFAULT" includes in get flight routes request
    Then The flight "<flightNumber>" includes in get flight routes request
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
      | customer     | <customer>       |
      | flightNumber | <flightNumber>   |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>     |
      | seatNumber   | <initSeatNumber> |
    Then User get the following ticket
      | customer     | <customer>       |
      | flightNumber | <flightNumber>   |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>     |
      | seatNumber   | <initSeatNumber> |
    When User clicks "Book Seat" in side menu
    Then The server return status code of 200
    Then Verify the following ticket status in the server response
      | flightNumber    | <flightNumber> |
      | flightDate      | <flightDate>   |
      | existInResponse | TRUE           |
    And User enters the seat in book seat page
      | flightNumber | <flightNumber>     |
      | flightDate   | <flightDate>       |
      | seatNumber   | <selectSeatNumber> |
    And User clicks the "Book Seat" button
    Then The server return status code of 200
    Then The server return the following response for book flight request
      | customer     | <customer>         |
      | flightNumber | <flightNumber>     |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>       |
      | seatNumber   | <selectSeatNumber> |
    Then User get the following ticket
      | customer     | <customer>         |
      | flightNumber | <flightNumber>     |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>       |
      | seatNumber   | <selectSeatNumber> |
    Then Verify the seat status in following flight
      | flightNumber | <flightNumber>     |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>       |
      | seatNumber   | <selectSeatNumber> |
      | seatStatus   | BOOKED             |

    When User clicks "Book Seat" in side menu
    Then The server return status code of 200
    Then Verify the following ticket status in the server response
      | flightNumber    | <flightNumber> |
      | flightDate      | <flightDate>   |
      | existInResponse | TRUE           |
    And User enters the seat in book seat page
      | flightNumber | <flightNumber>  |
      | flightDate   | <flightDate>    |
      | seatNumber   | <newSeatNumber> |
    And Scenario updates the checkpoint "<waitBookCheckpoint>"
    Then Waiting the checkpoint "<waitBookCheckpoint>" is finished by each 40 ms
    And User clicks the "Book Seat" button
    And Save the current response into concurrent list "<responseName>"
    And Scenario updates the checkpoint "<bookedCheckpoint>"
    And Waiting the checkpoint "<bookedCheckpoint>" is finished by each 40 ms
    And Verify concurrent response by following information
      | responseName          | <responseName>          |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
    Then The server return status code of 200
    Then The server return the following response for book flight request
      | customer     | <customer>         |
      | flightNumber | <flightNumber>     |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>       |
      | seatNumber   | <newSeatNumber> |
    Then User get the following ticket
      | customer     | <customer>      |
      | flightNumber | <flightNumber>  |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>    |
      | seatNumber   | <newSeatNumber> |
    Then Verify the seat status in following flight
      | flightNumber | <flightNumber>  |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>    |
      | seatNumber   | <newSeatNumber> |
      | seatStatus   | BOOKED          |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished by each 20 ms

    Examples:
      | flightNumber               | flightDate | customer  | selectSeatNumber | newSeatNumber | initSeatNumber | responseName         | successfulNum | failedNum | failedStatus | expectedFailedMessage  | bookedCheckpoint    | waitAllScenario        | setupCheckpoint              | waitBookCheckpoint     |
      | OneChangeOneBookSeatFlight | 85         | DEFAULT:2 | 15               | 22            | NULL           | OneChangeOneBookSeat | 2/1           | 0/1       | 200/400      | NULL/is not available. | ChangedBookedSeat:2 | OneChangeOneBookSeat:3 | OneChangeOneBookSeatFlight:1 | BeforeChangeBookSeat:2 |
