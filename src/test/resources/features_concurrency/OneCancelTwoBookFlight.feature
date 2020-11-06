Feature: The flight is full, and one customer cancels the ticket of this flight,
  and other two customers try to book the same flight at the same time

  Scenario Outline: Setup last ticket flight for three concurrent canceling and booking
    Given Printing the thread info for feature "Book Flight Concurrency" and scenario "Setup concurrency flight"
    Given Concurrency scenario set up the checkpoint "<setupCheckpoint>"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Then System create a flight for concurrency test "<flightNumber>"
    And Set up the available tickets for following flight
      | flightNumber     | <flightNumber>     |
      | flightDate       | <flightDate>       |
      | availableTickets | <availableTickets> |
    And Commit current transaction to database
    And Scenario updates the checkpoint "<setupCheckpoint>"
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished by each 20 ms
    Examples:
      | flightNumber           | flightDate | availableTickets | setupCheckpoint          | waitAllScenario    |
      | OneCancelTwoBookFlight | 99         | 1                | OneCancelTwoBookFlight:1 | OneCancelTwoBook:4 |


  Scenario Outline: Customer 1 book the last ticket of the select flight and try to cancel this ticket
    Given Printing the thread info for feature "Last Ticket of Flight" and scenario "Customer 1 book the last ticket"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookingCheckpoint>"
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
    Then Verify the available tickets of following flight
      | flightNumber     | <flightNumber>     |
      # Flight date is how many days start from today
      | flightDate       | <flightDate>       |
      | availableTickets | <availableTickets> |
    When User clicks "Cancel Ticket" in side menu
    Then The server return status code of 200
    And User enters the following flight in cancel ticket page
      | flightNumber | <flightNumber> |
      | flightDate   | <flightDate>   |
    And Scenario updates the checkpoint "<waitBookCheckpoint>"
    Then Waiting the checkpoint "<waitBookCheckpoint>" is finished by each 15 ms
    And User clicks the "Cancel Ticket" button
    And Save the current response into concurrent list "<responseName>"
    And Scenario updates the checkpoint "<bookingCheckpoint>"
    And Waiting the checkpoint "<bookingCheckpoint>" is finished by each 15 ms
    And Verify concurrent response by following information
      | responseName          | <responseName>          |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished by each 20 ms

    Examples:
      | flightNumber           | flightDate | customer  | tickets | availableTickets | seatNumber | responseName     | successfulNum | failedNum | failedStatus | expectedFailedMessage                                                                                       | bookingCheckpoint      | waitAllScenario    | setupCheckpoint          | waitBookCheckpoint           |
      | OneCancelTwoBookFlight | 99         | DEFAULT:1 | 1       | 0                | NULL       | OneCancelTwoBook | 1/2           | 2/1       | 503/503      | Failed to book the ticket because the flight is full./Failed to book the ticket because the flight is full. | CanceledOneBookedTwo:3 | OneCancelTwoBook:4 | OneCancelTwoBookFlight:1 | BeforeCanceledOneBookedTwo:3 |

  Scenario Outline: Customer 2 book the the select flight
    Given Printing the thread info for feature "Last Ticket of Flight" and scenario "Customer 2 book the last ticket"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookingCheckpoint>"
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
    And Scenario updates the checkpoint "<waitBookCheckpoint>"
    Then Waiting the checkpoint "<waitBookCheckpoint>" is finished by each 15 ms
    And User clicks the "Book Flight" button
    And Save the current response into concurrent list "<responseName>"
    And Scenario updates the checkpoint "<bookingCheckpoint>"
    And Waiting the checkpoint "<bookingCheckpoint>" is finished by each 15 ms
    And Verify concurrent response by following information
      | responseName          | <responseName>          |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished by each 20 ms

    Examples:
      | flightNumber           | flightDate | tickets | responseName     | successfulNum | failedNum | failedStatus | expectedFailedMessage                                                                                       | bookingCheckpoint      | waitAllScenario    | setupCheckpoint          | waitBookCheckpoint           |
      | OneCancelTwoBookFlight | 99         | 1       | OneCancelTwoBook | 1/2           | 2/1       | 503/503      | Failed to book the ticket because the flight is full./Failed to book the ticket because the flight is full. | CanceledOneBookedTwo:3 | OneCancelTwoBook:4 | OneCancelTwoBookFlight:1 | BeforeCanceledOneBookedTwo:3 |

  Scenario Outline: Customer 3 book the the select flight
    Given Printing the thread info for feature "Last Ticket of Flight" and scenario "Customer 2 book the last ticket"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookingCheckpoint>"
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
    And Scenario updates the checkpoint "<waitBookCheckpoint>"
    Then Waiting the checkpoint "<waitBookCheckpoint>" is finished by each 15 ms
    And User clicks the "Book Flight" button
    And Save the current response into concurrent list "<responseName>"
    And Scenario updates the checkpoint "<bookingCheckpoint>"
    And Waiting the checkpoint "<bookingCheckpoint>" is finished by each 15 ms
    And Verify concurrent response by following information
      | responseName          | <responseName>          |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished by each 20 ms

    Examples:
      | flightNumber           | flightDate | tickets | responseName     | successfulNum | failedNum | failedStatus | expectedFailedMessage                                                                                       | bookingCheckpoint      | waitAllScenario    | setupCheckpoint          | waitBookCheckpoint           |
      | OneCancelTwoBookFlight | 99         | 1       | OneCancelTwoBook | 1/2           | 2/1       | 503/503      | Failed to book the ticket because the flight is full./Failed to book the ticket because the flight is full. | CanceledOneBookedTwo:3 | OneCancelTwoBook:4 | OneCancelTwoBookFlight:1 | BeforeCanceledOneBookedTwo:3 |