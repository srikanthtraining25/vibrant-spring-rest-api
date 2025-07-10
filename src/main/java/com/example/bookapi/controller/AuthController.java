
package com.example.bookapi.controller;

import com.example.bookapi.dto.ApiResponse;
import com.example.bookapi.dto.AuthRequest;
import com.example.bookapi.model.User;
import com.example.bookapi.service.MfaService;
import com.example.bookapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MfaService mfaService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody User user) {
        // Check if username or email already exists
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error("Username already exists")
            );
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error("Email already exists")
            );
        }
        
        User createdUser = userService.createUser(user);
        
        // Don't return password in response
        createdUser.setPassword(null);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success("User registered successfully", createdUser)
        );
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody AuthRequest authRequest) {
        boolean isAuthenticated = mfaService.authenticateUser(
            authRequest.getUsernameOrEmail(), 
            authRequest.getPassword(), 
            authRequest.getMfaCode()
        );
        
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error("Invalid credentials or MFA code")
            );
        }
        
        Optional<User> userOpt = userService.getUserByUsernameOrEmail(authRequest.getUsernameOrEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error("User not found")
            );
        }
        
        User user = userOpt.get();
        user.setPassword(null); // Don't return password
        
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", "jwt-token-placeholder"); // In real implementation, generate JWT
        response.put("mfaRequired", user.isMfaEnabled() && authRequest.getMfaCode() == null);
        
        return ResponseEntity.ok(
            ApiResponse.success("Login successful", response)
        );
    }
    
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam Long userId, @RequestParam String token) {
        Optional<User> userOpt = userService.getUserById(userId);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("User not found")
            );
        }
        
        // Simple verification (in real implementation, validate actual token)
        if ("verify123".equals(token)) {
            User user = userOpt.get();
            user.setEmailVerified(true);
            userService.updateUser(userId, user);
            
            return ResponseEntity.ok(
                ApiResponse.success("Email verified successfully", "Email verification completed")
            );
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponse.error("Invalid verification token")
        );
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String email) {
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("User with this email not found")
            );
        }
        
        // In real implementation, send password reset email
        return ResponseEntity.ok(
            ApiResponse.success("Password reset email sent", "Check your email for reset instructions")
        );
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@RequestParam Long userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("User not found")
            );
        }
        
        User user = userOpt.get();
        user.setPassword(null); // Don't return password
        
        return ResponseEntity.ok(
            ApiResponse.success("User profile retrieved", user)
        );
    }
}
