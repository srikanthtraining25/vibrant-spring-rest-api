
package com.example.bookapi.controller;

import com.example.bookapi.dto.ApiResponse;
import com.example.bookapi.model.Book;
import com.example.bookapi.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre) {
        
        List<Book> books;
        
        if (author != null && !author.trim().isEmpty()) {
            books = bookService.getBooksByAuthor(author);
        } else if (genre != null && !genre.trim().isEmpty()) {
            books = bookService.getBooksByGenre(genre);
        } else {
            books = bookService.getAllBooks();
        }
        
        return ResponseEntity.ok(
            ApiResponse.success("Books retrieved successfully", books)
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        
        if (book.isPresent()) {
            return ResponseEntity.ok(
                ApiResponse.success("Book found", book.get())
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("Book not found with id: " + id)
            );
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Book>> createBook(@Valid @RequestBody Book book) {
        // Check if ISBN already exists
        if (bookService.existsByIsbn(book.getIsbn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error("Book with this ISBN already exists")
            );
        }
        
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success("Book created successfully", createdBook)
        );
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> updateBook(
            @PathVariable Long id, 
            @Valid @RequestBody Book book) {
        
        Optional<Book> updatedBook = bookService.updateBook(id, book);
        
        if (updatedBook.isPresent()) {
            return ResponseEntity.ok(
                ApiResponse.success("Book updated successfully", updatedBook.get())
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("Book not found with id: " + id)
            );
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        
        if (deleted) {
            return ResponseEntity.ok(
                ApiResponse.success("Book deleted successfully", null)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("Book not found with id: " + id)
            );
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Long>> getBookStats() {
        long totalBooks = bookService.getTotalBooks();
        return ResponseEntity.ok(
            ApiResponse.success("Total books count", totalBooks)
        );
    }
}
