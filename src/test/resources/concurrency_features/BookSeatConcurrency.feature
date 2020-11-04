Feature: Four customer try to book the same flight at the same time (However, server only can process three request at the same time)

  Scenario Outline: Setup concurrency test's flight in the system
    Given Printing the thread info for feature "Book Flight Concurrency" and scenario "Setup concurrency flight"
    Given Concurrency scenario set up the checkpoint "<setupCheckpoint>"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Then System create a flight for concurrency test "BookFlightConcurrency"
    And Commit current transaction to database
    And Scenario updates the checkpoint "<setupCheckpoint>"
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished
    Examples:
      | setupCheckpoint   | waitAllScenario   |
      | SetupBookFlight:1 | WaitAllScenario:5 |


  Scenario Outline: Test 1
    Given Printing the thread info for feature "Book Flight Concurrency" and scenario "Test1"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookingCheckpoint>"
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
    And User clicks the "Book Flight" button
    And Save the current response into concurrent list "BookFlightRequest"
    And Scenario updates the checkpoint "<bookingCheckpoint>"
    And Waiting the checkpoint "<bookingCheckpoint>" is finished
    And Verify concurrent response by following information
      | requestName           | <requestName>           |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
#    Then User get the following ticket
#      | customer     | <customer>     |
#      | flightNumber | <flightNumber> |
#      # Flight date is how many days start from today
#      | flightDate   | <flightDate>   |
#      | seatNumber   | <seatNumber>   |
    Then Verify the change of available ticket amount of the following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | tickets      | <tickets>      |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished

    Examples:
      | flightNumber          | flightDate | customer  | tickets | seatNumber | requestName       | successfulNum | failedNum | failedStatus | expectedFailedMessage                      | bookingCheckpoint | waitAllScenario   | setupCheckpoint   |
      | BookFlightConcurrency | 85         | DEFAULT:2 | 3       | NULL       | BookFlightRequest | 3             | 1         | 503          | Server is busy. Try to book flight failed. | BOOKED:4          | WaitAllScenario:5 | SetupBookFlight:1 |

  Scenario Outline: Test 2
    Given Printing the thread info for feature "Book Flight Concurrency" and scenario "Test1"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookingCheckpoint>"
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
    And User clicks the "Book Flight" button
    And Save the current response into concurrent list "BookFlightRequest"
    And Scenario updates the checkpoint "<bookingCheckpoint>"
    And Waiting the checkpoint "<bookingCheckpoint>" is finished
    And Verify concurrent response by following information
      | requestName           | <requestName>           |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
#    Then User get the following ticket
#      | customer     | <customer>     |
#      | flightNumber | <flightNumber> |
#      # Flight date is how many days start from today
#      | flightDate   | <flightDate>   |
#      | seatNumber   | <seatNumber>   |
    Then Verify the change of available ticket amount of the following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | tickets      | <tickets>      |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished

    Examples:
      | flightNumber          | flightDate | customer  | tickets | seatNumber | requestName       | successfulNum | failedNum | failedStatus | expectedFailedMessage                      | bookingCheckpoint | waitAllScenario   | setupCheckpoint   |
      | BookFlightConcurrency | 85         | DEFAULT:1 | 3       | NULL       | BookFlightRequest | 3             | 1         | 503          | Server is busy. Try to book flight failed. | BOOKED:4          | WaitAllScenario:5 | SetupBookFlight:1 |

  Scenario Outline: Test 3
    Given Printing the thread info for feature "Book Flight Concurrency" and scenario "Test1"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookingCheckpoint>"
    And Commit current transaction to database
    Then Waiting the checkpoint "<setupCheckpoint>" is finished
    Given "Customer" user 3 logs in to the system
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
    And Save the current response into concurrent list "BookFlightRequest"
    And Scenario updates the checkpoint "<bookingCheckpoint>"
    And Waiting the checkpoint "<bookingCheckpoint>" is finished
    And Verify concurrent response by following information
      | requestName           | <requestName>           |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
#    Then User get the following ticket
#      | customer     | <customer>     |
#      | flightNumber | <flightNumber> |
#      # Flight date is how many days start from today
#      | flightDate   | <flightDate>   |
#      | seatNumber   | <seatNumber>   |
    Then Verify the change of available ticket amount of the following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | tickets      | <tickets>      |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished

    Examples:
      | flightNumber          | flightDate | customer  | tickets | seatNumber | requestName       | successfulNum | failedNum | failedStatus | expectedFailedMessage                      | bookingCheckpoint | waitAllScenario   | setupCheckpoint   |
      | BookFlightConcurrency | 85         | DEFAULT:3 | 3       | NULL       | BookFlightRequest | 3             | 1         | 503          | Server is busy. Try to book flight failed. | BOOKED:4          | WaitAllScenario:5 | SetupBookFlight:1 |

  Scenario Outline: Test 4
    Given Printing the thread info for feature "Book Flight Concurrency" and scenario "Test1"
    Given Concurrency scenario set up the checkpoint "<waitAllScenario>"
    Given Concurrency scenario set up the checkpoint "<bookingCheckpoint>"
    And Commit current transaction to database
    Then Waiting the checkpoint "<setupCheckpoint>" is finished
    Given "Customer" user 4 logs in to the system
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
    And Save the current response into concurrent list "BookFlightRequest"
    And Scenario updates the checkpoint "<bookingCheckpoint>"
    And Waiting the checkpoint "<bookingCheckpoint>" is finished
    And Verify concurrent response by following information
      | requestName           | <requestName>           |
      | successfulNum         | <successfulNum>         |
      | failedNum             | <failedNum>             |
      | failedStatus          | <failedStatus>          |
      | expectedFailedMessage | <expectedFailedMessage> |
#    Then User get the following ticket
#      | customer     | <customer>     |
#      | flightNumber | <flightNumber> |
#      # Flight date is how many days start from today
#      | flightDate   | <flightDate>   |
#      | seatNumber   | <seatNumber>   |
    Then Verify the change of available ticket amount of the following flight
      | flightNumber | <flightNumber> |
      # Flight date is how many days start from today
      | flightDate   | <flightDate>   |
      | tickets      | <tickets>      |
    And Scenario updates the checkpoint "<waitAllScenario>"
    Then Waiting the checkpoint "<waitAllScenario>" is finished

    Examples:
      | flightNumber          | flightDate | customer  | tickets | seatNumber | requestName       | successfulNum | failedNum | failedStatus | expectedFailedMessage                      | bookingCheckpoint | waitAllScenario   | setupCheckpoint   |
      | BookFlightConcurrency | 85         | DEFAULT:4 | 3       | NULL       | BookFlightRequest | 3             | 1         | 503          | Server is busy. Try to book flight failed. | BOOKED:4          | WaitAllScenario:5 | SetupBookFlight:1 |