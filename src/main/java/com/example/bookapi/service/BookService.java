
package com.example.bookapi.service;

import com.example.bookapi.model.Book;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookService {
    
    private final Map<Long, Book> books = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public BookService() {
        // Initialize with sample data
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0-7432-7356-5", 1925, "Fiction", "A classic American novel");
        book1.setId(idGenerator.getAndIncrement());
        books.put(book1.getId(), book1);
        
        Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", "978-0-06-112008-4", 1960, "Fiction", "A gripping tale of racial injustice");
        book2.setId(idGenerator.getAndIncrement());
        books.put(book2.getId(), book2);
        
        Book book3 = new Book("1984", "George Orwell", "978-0-452-28423-4", 1949, "Dystopian Fiction", "A dystopian social science fiction novel");
        book3.setId(idGenerator.getAndIncrement());
        books.put(book3.getId(), book3);
    }
    
    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }
    
    public Optional<Book> getBookById(Long id) {
        return Optional.ofNullable(books.get(id));
    }
    
    public List<Book> getBooksByAuthor(String author) {
        return books.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }
    
    public List<Book> getBooksByGenre(String genre) {
        return books.values().stream()
                .filter(book -> book.getGenre() != null && 
                               book.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .toList();
    }
    
    public Book createBook(Book book) {
        book.setId(idGenerator.getAndIncrement());
        books.put(book.getId(), book);
        return book;
    }
    
    public Optional<Book> updateBook(Long id, Book updatedBook) {
        Book existingBook = books.get(id);
        if (existingBook != null) {
            updatedBook.setId(id);
            updatedBook.setCreatedAt(existingBook.getCreatedAt());
            books.put(id, updatedBook);
            return Optional.of(updatedBook);
        }
        return Optional.empty();
    }
    
    public boolean deleteBook(Long id) {
        return books.remove(id) != null;
    }
    
    public boolean existsByIsbn(String isbn) {
        return books.values().stream()
                .anyMatch(book -> book.getIsbn().equals(isbn));
    }
    
    public long getTotalBooks() {
        return books.size();
    }
}
