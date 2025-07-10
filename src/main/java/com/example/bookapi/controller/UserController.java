
package com.example.bookapi.controller;

import com.example.bookapi.dto.ApiResponse;
import com.example.bookapi.model.User;
import com.example.bookapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        
        // Don't return passwords
        users.forEach(user -> user.setPassword(null));
        
        return ResponseEntity.ok(
            ApiResponse.success("Users retrieved successfully", users)
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        
        if (user.isPresent()) {
            User userData = user.get();
            userData.setPassword(null); // Don't return password
            
            return ResponseEntity.ok(
                ApiResponse.success("User found", userData)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("User not found with id: " + id)
            );
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody User user) {
        
        Optional<User> updatedUser = userService.updateUser(id, user);
        
        if (updatedUser.isPresent()) {
            User userData = updatedUser.get();
            userData.setPassword(null); // Don't return password
            
            return ResponseEntity.ok(
                ApiResponse.success("User updated successfully", userData)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("User not found with id: " + id)
            );
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        
        if (deleted) {
            return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully", null)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("User not found with id: " + id)
            );
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Long>> getUserStats() {
        long totalUsers = userService.getTotalUsers();
        return ResponseEntity.ok(
            ApiResponse.success("Total users count", totalUsers)
        );
    }
}
