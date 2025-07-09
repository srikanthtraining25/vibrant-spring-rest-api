
# Book API - Spring Boot REST Application

A comprehensive Spring Boot REST API application for managing a book library system.

## Features

- 📚 Complete CRUD operations for books
- 🔍 Search books by author or genre
- ✅ Input validation with proper error handling
- 📊 API statistics endpoint
- 🛡️ Global exception handling
- 📋 Clean layered architecture
- 🚀 RESTful API design with proper HTTP status codes

## API Endpoints

### Books Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | Get all books (supports filtering by author/genre) |
| GET | `/api/books/{id}` | Get book by ID |
| POST | `/api/books` | Create a new book |
| PUT | `/api/books/{id}` | Update an existing book |
| DELETE | `/api/books/{id}` | Delete a book |
| GET | `/api/books/stats` | Get book statistics |

### Query Parameters

- `?author=authorName` - Filter books by author
- `?genre=genreName` - Filter books by genre

## Sample API Calls

### Get All Books
```bash
curl -X GET http://localhost:8080/api/books
```

### Get Book by ID
```bash
curl -X GET http://localhost:8080/api/books/1
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

### Update a Book
```bash
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Great Gatsby - Updated",
    "author": "F. Scott Fitzgerald",
    "isbn": "978-0-7432-7356-5",
    "publicationYear": 1925,
    "genre": "Classic Fiction",
    "description": "An updated classic American novel"
  }'
```

### Delete a Book
```bash
curl -X DELETE http://localhost:8080/api/books/1
```

### Search Books by Author
```bash
curl -X GET "http://localhost:8080/api/books?author=Fitzgerald"
```

### Search Books by Genre
```bash
curl -X GET "http://localhost:8080/api/books?genre=Fiction"
```

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
│   │   │   └── BookController.java          # REST controller
│   │   ├── service/
│   │   │   └── BookService.java             # Business logic
│   │   ├── model/
│   │   │   └── Book.java                    # Book entity
│   │   ├── dto/
│   │   │   └── ApiResponse.java             # Response wrapper
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java  # Exception handling
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

The application comes pre-loaded with sample books:
- The Great Gatsby by F. Scott Fitzgerald
- To Kill a Mockingbird by Harper Lee
- 1984 by George Orwell

## Error Handling

The application includes comprehensive error handling:
- Validation errors (400 Bad Request)
- Resource not found (404 Not Found)
- Duplicate ISBN (409 Conflict)
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
