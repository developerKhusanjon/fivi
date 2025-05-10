# Fabler Backend

A micronaut based backend service for the Fivi application, a platform for messaging and sharing notes and important events (fables).

## Project Structure

```
fabler-firecloud/
├── build.gradle                  # gradle build configuration
├── src/
│   └── main/
│       ├── resources/
│       │   ├── application.yaml # Application configuration
│       │   └── logback.xml      # Logging configuration
│       └── groovy/
│           └── me/
│               └── fivi/
│                       ├── Fivi.groovy           # Main application entry point
│                       ├── controller/
│                       ├── domain/
│                       ├── dto/
│                       ├── exception/
│                       ├── repository/
│                       ├── security/
│                       └── service/
```

## Tech Stack

- Groovy 4.0
- Micronaut 4.8
- Jwt based security
- Email verification
- Firebase push notifications

## Project Structure

The project follows a clean architecture approach with the following components:

- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic
- **Repositories**: Data access layer
- **Domains**: Domain objects
- **Config**: Application configuration

## Getting Started

### Prerequisites
- JDK 17 or higher
- Groovy 4.0 +
- Firebase project with service account credentials


## Setup

1. Create a Firebase project and download the service account credentials file.
2. Place the credentials file at the project root as `credentials-file.json`
3. Configure the `application.yaml` with your Firebase database URL.
```
gradle clean
```

### Running locally
1. Make sure PostgreSQL is running and create a database named `fivitestdb`
2. Place Firebase credentials JSON file in the appropriate location
3. Update `application.yaml` with correct database connection details
4. Run the application:

## API Documentation

### Auth
- `POST /api/auth/signup`: Register a new user
- `POST /api/auth/signin`: Login with email and password
- `POST /api/auth/refresh`: Refresh token
- `POST /api/auth/confirm-email`: Confirm sign-up
- `POST /api/auth/change-password`: Change password
- `GET /api/auth/check-status`: Verify authentication token

### Profile
- `GET /api/profiles/me`: Get user info

## Deployment
The application (will be) deployed to any environment that supports Java applications. A Dockerfile is included for container-based deployments.

## License
This project (may be) licensed under the MIT License - see the LICENSE file for details.
