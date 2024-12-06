package com.example.demo.Controllers;

import java.util.List;
import java.util.Optional;

import com.example.demo.Exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    // Show all users
    @GetMapping("/fetch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> showAllUsers() {
        log.info("Request received to show all users");
        List<User> users = userRepository.findAll();
        log.info("Returning {} users", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Show user by ID
    @GetMapping("/fetch/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<User> showUserById(@PathVariable Long id) {
        log.info("Request received to show user with ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            log.info("User found with ID: {}", id);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            log.warn("User not found with ID: {}", id);
            throw new UserNotFoundException("User not found with ID: " + id); // Throwing custom exception
        }
    }

    // Add a new user
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User newUser) {
        log.info("Request received to add a new user: {}", newUser);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        log.info("User added successfully with ID: {}", newUser.getId());
        return new ResponseEntity<>("User added successfully", HttpStatus.CREATED);
    }

    // Update an existing user
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@RequestBody User newUser, @PathVariable Long id) {
        log.info("Request received to update user with ID: {}", id);
        Optional<User> userOld = userRepository.findById(id);
        if (userOld.isPresent()) {
            User user = userOld.get();
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            if (newUser.getEmail() != null && !newUser.getEmail().isEmpty()) {
                user.setEmail(newUser.getEmail());
            }
            if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
//                String hashedPassword = passwordEncoder().encode(newUser.getPassword());
                String hashedPassword = passwordEncoder.encode(newUser.getPassword());
                user.setPassword(hashedPassword);
            }
            if (newUser.getRole() != null && !newUser.getRole().isEmpty()) {
                user.setRole(newUser.getRole());
            }
            user.setAccounts(newUser.getAccounts());
            userRepository.save(user);
            log.info("User updated successfully with ID: {}", id);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        } else {
            log.warn("User not found with ID: {}", id);
            throw new UserNotFoundException("User not found with ID: " + id); // Throwing custom exception
        }
    }

    // Delete a user
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        log.info("Request received to delete user with ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            log.info("User deleted successfully with ID: {}", id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } else {
            log.warn("User not found with ID: {}", id);
            throw new UserNotFoundException("User not found with ID: " + id); // Throwing custom exception
        }
    }
}
