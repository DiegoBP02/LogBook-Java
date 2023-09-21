# LogBook 

## Main purpose
This project was created with the aim of helping users to track and manage their gym progress effectively.

## About the project
This project combines Java and Spring Boot for the backend, React for the frontend, PostgreSQL for data storage and includes robust unit tests to ensure reliability. User authentication is handled through the utilization of JWT. 

## Project Technology Stack
- **Java and Spring Boot**: Backend development with RESTful APIs.
- **React**: Frontend development to provide user interface and interactivity.
- **PostgreSQL**: Database management for storing user data and application records.
- **JUnit 5**: Rigorous unit testing to ensure code reliability and functionality.


## Main features
- **Responsive design**: Ensures the application adapts to various screen sizes. 
- **JWT Authentication**: User authentication using JWT tokens.
- **Add new workout**: Define workout details, including exercise name and reps range.
- **Remove workout**: Delete workouts from the workout page.
- **Add set**:  Specify exercise name, weight, and reps for each set.
- **Dynamic Set Display**: Sets outside the specified rep range are highlighted in red.
- **Edit set**:  Modify data for specific sets as needed.
- **Remove set**: Delete specific sets.
- **Get exercises from the previous workout**: Allows the user to retrieve exercises from the previous workout and apply them to the current workout to make changes. This can only occur if the current and previous workouts share the same lower and upper rep range.
- **Get unique workout exercises**: Allows the user to compare unique exercises between the current and previous workouts to track changes in the workout program.

## Access the Swagger UI Documentation
[Swagger UI](https://diegobp02.github.io/LogBook-Java/)

## Run Application Locally

### Pre requisites
- JDK 17 or higher
- Docker to run PostgreSQL as a container

To run the project locally, please follow these steps:

1. Clone this repository and build the project.
2. Create a copy of the `application.properties.template` file located in `src/main/resources` and rename it to `application.properties`.
3. Open the `application.properties` file and provide the following information:

```properties
jwt.secret=your-jwt-secret
token.expiration=your-token-expiration
timezone.offset=your-timezone-offset
```

3. Replace the placeholders with your actual database and email configuration details. Here's a description of each placeholder:
    1. your-jwt-secret: A secret key to sign the JWT.
    2. your-token-expiration: Expiration time for JWT tokens. (in seconds).
    3. your-timezone-offset: Specify the timezone offset (e.g.: -03:00).
4. Run the following command in the root of the project to start a running instance of PostgreSQL:

```bash
   docker compose up
```
   
5.  In the project root directory, start the backend and frontend simultaneously with the following command:

```bash
cd client && npm start
```

This command will initiate the React frontend and the Java Spring backend, allowing you to interact with the application by accessing it in your web browser.

6. After successfully running the application, you should see log messages indicating the startup of the application. The logs will display the port on which the application is running. Additionally, ensure that the React frontend is also running by accessing it in your web browser at the specified address.

## ⚠️ Security Warning

Please exercise caution when modifying the `application.properties` file and ensure that you do not inadvertently expose your sensitive information, such as jwt secret, to unauthorized individuals. 
