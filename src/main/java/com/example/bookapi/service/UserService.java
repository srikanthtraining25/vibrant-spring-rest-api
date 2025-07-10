
package com.example.bookapi.service;

import com.example.bookapi.model.User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final Map<String, User> usersByUsername = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public UserService() {
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        User admin = new User("admin", "admin@example.com", "password123");
        admin.setId(idGenerator.getAndIncrement());
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmailVerified(true);
        admin.setActive(true);
        
        users.put(admin.getId(), admin);
        usersByEmail.put(admin.getEmail(), admin);
        usersByUsername.put(admin.getUsername(), admin);
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }
    
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }
    
    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        User user = usersByUsername.get(usernameOrEmail);
        if (user == null) {
            user = usersByEmail.get(usernameOrEmail);
        }
        return Optional.ofNullable(user);
    }
    
    public User createUser(User user) {
        user.setId(idGenerator.getAndIncrement());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        users.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        usersByUsername.put(user.getUsername(), user);
        
        return user;
    }
    
    public Optional<User> updateUser(Long id, User updatedUser) {
        User existingUser = users.get(id);
        if (existingUser != null) {
            // Remove old mappings
            usersByEmail.remove(existingUser.getEmail());
            usersByUsername.remove(existingUser.getUsername());
            
            // Update user
            updatedUser.setId(id);
            updatedUser.setCreatedAt(existingUser.getCreatedAt());
            updatedUser.setUpdatedAt(LocalDateTime.now());
            
            // Add new mappings
            users.put(id, updatedUser);
            usersByEmail.put(updatedUser.getEmail(), updatedUser);
            usersByUsername.put(updatedUser.getUsername(), updatedUser);
            
            return Optional.of(updatedUser);
        }
        return Optional.empty();
    }
    
    public boolean deleteUser(Long id) {
        User user = users.remove(id);
        if (user != null) {
            usersByEmail.remove(user.getEmail());
            usersByUsername.remove(user.getUsername());
            return true;
        }
        return false;
    }
    
    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(email);
    }
    
    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username);
    }
    
    public void updateLastLogin(Long userId) {
        User user = users.get(userId);
        if (user != null) {
            user.setLastLoginAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    public void enableMfa(Long userId, String secret) {
        User user = users.get(userId);
        if (user != null) {
            user.setMfaEnabled(true);
            user.setMfaSecret(secret);
            user.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    public void disableMfa(Long userId) {
        User user = users.get(userId);
        if (user != null) {
            user.setMfaEnabled(false);
            user.setMfaSecret(null);
            user.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    public long getTotalUsers() {
        return users.size();
    }
}
