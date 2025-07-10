
# Book API with MFA Management - Spring Boot REST Application

A comprehensive Spring Boot REST API application for managing a book library system with Multi-Factor Authentication (MFA) support.

## Features

- 📚 Complete CRUD operations for books
- 👤 User management with registration and authentication
- 🔐 Multi-Factor Authentication (MFA) with TOTP support
- 🔑 Backup codes for MFA recovery
- 📱 Device management for MFA
- 🔍 Search books by author or genre
- ✅ Input validation with proper error handling
- 📊 API statistics endpoints
- 🛡️ Global exception handling
- 📋 Clean layered architecture
- 🚀 RESTful API design with proper HTTP status codes

## API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login with username/email and password (+ MFA code if enabled) |
| POST | `/api/auth/verify-email` | Verify user email address |
| POST | `/api/auth/reset-password` | Request password reset |
| GET | `/api/auth/me` | Get current user profile |

### User Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user profile |
| DELETE | `/api/users/{id}` | Delete user account |
| GET | `/api/users/stats` | Get user statistics |

### MFA Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/mfa/setup/totp` | Setup TOTP authenticator device |
| POST | `/api/mfa/verify` | Verify MFA setup with code |
| GET | `/api/mfa/devices` | Get user's MFA devices |
| DELETE | `/api/mfa/devices/{deviceId}` | Delete MFA device |
| PUT | `/api/mfa/devices/{deviceId}/activate` | Activate MFA device |
| PUT | `/api/mfa/devices/{deviceId}/deactivate` | Deactivate MFA device |
| POST | `/api/mfa/backup-codes/regenerate` | Regenerate backup codes |
| GET | `/api/mfa/status` | Get MFA status for user |

### Books Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | Get all books (supports filtering by author/genre) |
| GET | `/api/books/{id}` | Get book by ID |
| POST | `/api/books` | Create a new book |
| PUT | `/api/books/{id}` | Update an existing book |
| DELETE | `/api/books/{id}` | Delete a book |
| GET | `/api/books/stats` | Get book statistics |

## Sample API Calls

### User Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### User Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "johndoe",
    "password": "password123"
  }'
```

### Login with MFA
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "johndoe",
    "password": "password123",
    "mfaCode": "123456"
  }'
```

### Setup TOTP MFA
```bash
curl -X POST "http://localhost:8080/api/mfa/setup/totp?userId=1&deviceName=My%20Phone" \
  -H "Content-Type: application/json"
```

### Verify MFA Setup
```bash
curl -X POST "http://localhost:8080/api/mfa/verify?deviceId=1&code=123456" \
  -H "Content-Type: application/json"
```

### Get User's MFA Devices
```bash
curl -X GET "http://localhost:8080/api/mfa/devices?userId=1"
```

### Get MFA Status
```bash
curl -X GET "http://localhost:8080/api/mfa/status?userId=1"
```

### Create a New Book
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Catcher in the Rye",
    "author": "J.D. Salinger",
    "isbn": "978-0-316-76948-0",
    "publicationYear": 1951,
    "genre": "Fiction",
    "description": "A controversial novel about teenage rebellion"
  }'
```

## MFA Workflow

1. **User Registration**: Create account with basic information
2. **MFA Setup**: User initiates TOTP setup, receives QR code and backup codes
3. **MFA Verification**: User scans QR code in authenticator app and verifies with generated code
4. **Login with MFA**: User provides username/password + 6-digit TOTP code
5. **Backup Code Usage**: If authenticator unavailable, user can use 8-digit backup codes
6. **Device Management**: Users can activate/deactivate/delete MFA devices

## Security Features

- Password-based authentication
- TOTP-based Multi-Factor Authentication
- Backup codes for MFA recovery
- Device management for MFA
- Email verification workflow
- Password reset functionality
- User session management
- Input validation and sanitization

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Steps
1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. The API will be available at `http://localhost:8080`

### Alternative: Using Maven Wrapper
```bash
./mvnw spring-boot:run
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/bookapi/
│   │   ├── BookApiApplication.java          # Main application class
│   │   ├── controller/
│   │   │   ├── AuthController.java          # Authentication endpoints
│   │   │   ├── MfaController.java           # MFA management endpoints
│   │   │   ├── UserController.java          # User management endpoints
│   │   │   └── BookController.java          # Book management endpoints
│   │   ├── service/
│   │   │   ├── UserService.java             # User business logic
│   │   │   ├── MfaService.java              # MFA business logic
│   │   │   └── BookService.java             # Book business logic
│   │   ├── model/
│   │   │   ├── User.java                    # User entity
│   │   │   ├── MfaDevice.java               # MFA device entity
│   │   │   └── Book.java                    # Book entity
│   │   ├── dto/
│   │   │   ├── AuthRequest.java             # Authentication request DTO
│   │   │   ├── MfaSetupResponse.java        # MFA setup response DTO
│   │   │   └── ApiResponse.java             # Standard API response wrapper
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java  # Global exception handling
│   └── resources/
│       └── application.properties           # Configuration
└── pom.xml                                  # Maven dependencies
```

## API Response Format

All API responses follow a consistent format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2023-12-07T10:30:00"
}
```

## Sample Data

The application comes pre-loaded with:
- **Books**: The Great Gatsby, To Kill a Mockingbird, 1984
- **Users**: Admin user (username: admin, email: admin@example.com, password: password123)

## Error Handling

The application includes comprehensive error handling:
- Validation errors (400 Bad Request)
- Authentication failures (401 Unauthorized)
- Resource not found (404 Not Found)
- Duplicate username/email (409 Conflict)
- Internal server errors (500 Internal Server Error)

## Technologies Used

- **Spring Boot 3.2.0** - Framework
- **Java 17** - Programming language
- **Maven** - Build tool
- **Spring Web** - REST API
- **Spring Validation** - Input validation
- **Jackson** - JSON processing

## Development Features

- Hot reload with Spring Boot DevTools
- Detailed logging configuration
- CORS enabled for cross-origin requests
- Clean separation of concerns
- In-memory data storage (suitable for development/testing)

## MFA Implementation Notes

- Uses TOTP (Time-based One-Time Password) algorithm
- Generates QR codes for easy authenticator app setup
- Provides backup codes for account recovery
- Supports multiple MFA devices per user
- Device activation/deactivation functionality
- Secure secret generation and storage

This implementation provides a solid foundation for a production-ready authentication system with MFA support.
