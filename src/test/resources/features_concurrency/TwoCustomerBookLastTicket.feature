Feature: Three customer try to book the same flight at the same time

  Scenario Outline: Setup last ticket flight for two concurrent booking
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
    Then Waiting the checkpoint "<waitAllScenario>" is finished
    Examples:
      | flightNumber     | flightDate | availableTickets | setupCheckpoint    | waitAllScenario   |
      | LastTicketFlight | 92         | 1                | LastTicketFlight:1 | LastTicket:3 |


  Scenario Outline: Customer 1 book the last ticket of the select flight
    Given Printing the thread info for feature "Last Ticket of Flight" and scenario "Customer 1 book the last ticket"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookingCheckpoint>"
    Given Concurrency scenario set up the checkpoint "<waitBookCheckpoint>"
    And Commit current transaction to database
    Then Waiting the checkpoint "<setupCheckpoint>" is finished
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
    And Scenario updates the checkpoint "<waitBookCheckpoint>"
    Then Waiting the checkpoint "<waitBookCheckpoint>" is finished
    And User clicks the "Book Flight" button
    And Save the current response into concurrent list "<responseName>"
    And Scenario updates the checkpoint "<bookingCheckpoint>"
    And Waiting the checkpoint "<bookingCheckpoint>" is finished
    And Verify concurrent response by following information
      | responseName          | <responseName>          |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
    Then Verify the change of available ticket amount of the following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | tickets      | <tickets>      |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished

    Examples:
      | flightNumber     | flightDate | tickets | responseName              | successfulNum | failedNum | failedStatus | expectedFailedMessage                                 | bookingCheckpoint | waitAllScenario   | setupCheckpoint    | waitBookCheckpoint |
      | LastTicketFlight | 92         | 1       | TwoCustomerBookLastTicket | 1             | 1         | 503          | Failed to book the ticket because the flight is full. | BookLastTicket:2          | LastTicket:3 | LastTicketFlight:1 | BeforeBookLastTicket:2       |

  Scenario Outline: Customer 2 book the last ticket of the select flight
    Given Printing the thread info for feature "Last Ticket of Flight" and scenario "Customer 2 book the last ticket"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookingCheckpoint>"
    Given Concurrency scenario set up the checkpoint "<waitBookCheckpoint>"
    And Commit current transaction to database
    Then Waiting the checkpoint "<setupCheckpoint>" is finished
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
    And Scenario updates the checkpoint "<waitBookCheckpoint>"
    Then Waiting the checkpoint "<waitBookCheckpoint>" is finished
    And User clicks the "Book Flight" button
    And Save the current response into concurrent list "<responseName>"
    And Scenario updates the checkpoint "<bookingCheckpoint>"
    And Waiting the checkpoint "<bookingCheckpoint>" is finished
    And Verify concurrent response by following information
      | responseName          | <responseName>          |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
    Then Verify the change of available ticket amount of the following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | tickets      | <tickets>      |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished

    Examples:
      | flightNumber     | flightDate | tickets | responseName              | successfulNum | failedNum | failedStatus | expectedFailedMessage                                 | bookingCheckpoint | waitAllScenario   | setupCheckpoint    | waitBookCheckpoint |
      | LastTicketFlight | 92         | 1       | TwoCustomerBookLastTicket | 1             | 1         | 503          | Failed to book the ticket because the flight is full. | BookLastTicket:2          | LastTicket:3 | LastTicketFlight:1 | BeforeBookLastTicket:2       |
