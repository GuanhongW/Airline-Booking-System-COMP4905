# Airline-Booking-System-COMP4905

Java Spring boot project 

## Test environment
IDE: IntelliJ IDEA
Database Manager: DataGrip
OS: Mac OS
JDK: Java 8

## How to run this project
1. Install MySQL database in your computer. (The download link: https://dev.mysql.com/downloads/mysql/)
2. Start the MySQL database by Legacy Password Encryption
3. Import the maven project by IntelliJ IDEA (Select JDK as Java 8, and enable lombok in IntelliJ IDEA)
4. Run `user.sql` to setup the MySQL database
5. Before run the application, change the log4j2 output directory in `/src/main/resources/log4j2.xml` to your log  path (Change this line: `<property name="FILE_PATH" value= UPDATE_THE_LOG_PATH />"`)
6. Update the database username and password in `/src/main/resources/application.properties`
7. Run the AirlineBookingSystemApplication 
8. Visit http://localhost:8080/swagger-ui.html

## How to run the unit test
1. Find `/test/java/airlinebookingsystem`
2. Right click the directory and select ``Run Test in `com.guanhong.airlinebookingsystem` ``
3. This will run both controller and service test cases

## How to run the Acceptance test (Cucumber)
1. Before run cucumber test, please use the Swagger to create two default account (Username: `admintest`, password: `adminadmin`; Username: `test@carleton.ca`, password: `useruser`)
2. Find `/test/java/airlinebooksystem/cucumber/runner`
3. There are two Cucumber runner: ConcurrencyCucumberTest only includes the concurrent scenarios; CucumberTests includes all Cucumber test cases.
4. NOTE: If you need the permanent Cucumber test report, please uncomment the `CUCUMBER_PUBLISH_TOKEN=TODO` and add your own token. instruction: https://reports.cucumber.io/profile 