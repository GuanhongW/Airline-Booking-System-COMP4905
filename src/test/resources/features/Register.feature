Feature: Verify the register function of the Airline Booking System

  Scenario Outline: Admin user register in the system successfully
    Given Printing the thread info for feature "Register" and scenario "correct info of admin"
    When User register in the airline booking system by following credentials
      | username | <username> |
      | password | <password> |
      | role     | <role>     |
    Then The server return status code of 200
    Then The server return the following response for register request
      | username | <username> |

    Examples:
      | username      | password   | role  |
      | admincucumber | adminadmin | ADMIN |

  Scenario Outline: Customer user register in the system successfully
    Given Printing the thread info for feature "Register" and scenario "correct info of customer"
    When User register in the airline booking system by following credentials
      | username  | <username>  |
      | password  | <password>  |
      | role      | <role>      |
      | gender    | <gender>    |
      | name      | <name>      |
      | birthDate | <birthDate> |
    Then The server return status code of 200
    Then The server return the following response for register request
      | username | <username> |

    Examples:
      | username                   | password | role | gender | name  | birthDate  |
      | testcucumber@carleton.com  | useruser | USER | male   | Eric  | 1995-12-28 |
      | testcucumber2@carleton.com | usertest | USER | female | Alice | 2000-02-29 |

  Scenario Outline: Admin user register in the system with invalid infomation
    Given Printing the thread info for feature "Register" and scenario "incorrect info of admin"
    When User register in the airline booking system by following credentials
      | username | <username> |
      | password | <password> |
      | role     | <role>     |
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |

    Examples:
      | username      | password                                                                                                                                                                                                                                                         | role  | expectedMessage                                                      |
      # Password is more than 255 digits
      | admincucumber | 1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111 | ADMIN | The password should be at least six digits and less than 255 digits. |
      # Password is less than 6 digits
      | admincucumber | 11111                                                                                                                                                                                                                                                            | ADMIN | The password should be at least six digits and less than 255 digits. |
      # Duplicate username (This user should be created in DB manually)
      | admintest     | adminadmin                                                                                                                                                                                                                                                       | ADMIN | The user already exits in system.                                    |

  Scenario Outline: Customer user register in the system with invalid infomation
    Given Printing the thread info for feature "Register" and scenario "incorrect info of customer"
    When User register in the airline booking system by following credentials
      | username  | <username>  |
      | password  | <password>  |
      | role      | <role>      |
      | gender    | <gender>    |
      | name      | <name>      |
      | birthDate | <birthDate> |
    Then The server return status code of 400
    Then The server return the following message
      | expectedMessage | <expectedMessage> |

    Examples:
      | username                   | password                                                                                                                                                                                                                                                         | role | gender | name  | birthDate  | expectedMessage                                                      |
      # Password is more than 255 digits
      | testcucumber@carleton.com  | 1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111 | USER | male   | Eric  | 1995-12-28 | The password should be at least six digits and less than 255 digits. |
      # Password is less than 6 digits
      | testcucumber2@carleton.com | 11111                                                                                                                                                                                                                                                            | USER | female | Alice | 2000-02-29 | The password should be at least six digits and less than 255 digits. |
      # Duplicate email address (This user should be created in DB manually)
      | test@carleton.ca           | usersuer                                                                                                                                                                                                                                                         | USER | male   | Bob   | 2006-08-03 | The user already exits in system.                                    |
      # Invalid email address
      | testcustomer               | usersuer                                                                                                                                                                                                                                                         | USER | male   | Bob   | 2005-12-31 | The email format is invalid.                                         |
      # Birthdate is invalid
      | testcucumber3@carleton.com | usersuer                                                                                                                                                                                                                                                         | USER | male   | Bob   | 2001-02-29 | The birth date's format is invalid.                                       |



