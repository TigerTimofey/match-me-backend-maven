# MatchMe Backend

## About

Backend part of the MatchMe application, built with Spring Boot using modern technologies to provide a reliable and scalable dating platform.

## Technologies

- **Spring Boot** - core framework
- **Spring Security** - for security and JWT authentication
- **Spring Data JPA** - for database operations
- **WebSocket** - for chat implementation
- **PostgreSQL** - database
- **Maven** - dependency management

## Main Features

- 🔐 JWT authentication and authorization
- 📊 REST API for profile management
- 💾 User data storage and management
- 💬 WebSocket for real-time chat
- 📸 Image upload and storage
- 🔍 API for user search and filtering

## Installation and Launch

1. Clone the repository:

```bash
git clone https://github.com/TigerTimofey/match-me-backend-maven
```

2. Configure PostgreSQL database and update `application.properties`

3. Build the project:

```bash
mvn clean install
```

4. Run the application:

Option 1: Through Maven

```bash
mvn spring-boot:run
```

Option 2: Through IDE (Recommended)

- Open the project in your IDE (IntelliJ IDEA recommended)
- Navigate to `src/main/java/com/example/jwt_demo/JwtDemoApplication.java`
- Right-click on the file and select "Run JwtDemoApplication"
- Or click on the green play button next to the main class

## API Endpoints

### Authentication

- POST `/api/auth/register` - registration
- POST `/api/auth/login` - login

### Users

- GET `/api/users/me` - current user
- GET `/api/users/{id}` - user information
- PATCH `/api/users/{id}` - profile update
- GET `/api/users/{id}/connections` - connections list

### Chat

- GET `/api/messages/{userId}/{connectionId}` - message history
- WebSocket `/ws` - chat connection

## Requirements

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
