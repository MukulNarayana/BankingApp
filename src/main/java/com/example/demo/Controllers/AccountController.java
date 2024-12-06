package com.example.demo.Controllers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.demo.Entity.Account;
import com.example.demo.Exception.AccountNotFoundException;
import com.example.demo.Repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

import org.springdoc.api.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "*")
@Slf4j
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    // Show all accounts
    @GetMapping("/fetch")
    public ResponseEntity<List<Account>> showAllAccounts() {
        log.info("Fetching all accounts");
        List<Account> accounts = accountRepository.findAll();
        log.info("Found {} accounts", accounts.size());
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Show account by ID
    @GetMapping("/fetch/{id}")
    public ResponseEntity<Account> showAccountById(@PathVariable Long id) {
        log.info("Fetching account with ID {}", id);
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            log.info("Account with ID {} found", id);
            return new ResponseEntity<>(account.get(), HttpStatus.OK);
        } else {
            log.warn("Account with ID {} not found", id);
            throw new AccountNotFoundException("Account not found with ID: " + id); // Throwing custom exception
        }
    }

    // Add a new account
    @PostMapping("/add")
    public ResponseEntity<String> addAccount(@RequestBody Account newAccount) {
        log.info("Adding a new account with account number {}", newAccount.getAccountNumber());
        accountRepository.save(newAccount);
        log.info("Account added successfully with account number {}", newAccount.getAccountNumber());
        return new ResponseEntity<>("Account added successfully", HttpStatus.CREATED);
    }

    // Update an existing account
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAccount(@RequestBody Account newAccount, @PathVariable Long id) {
        log.info("Updating account with ID {}", id);
        Optional<Account> accountOld = accountRepository.findById(id);
        if (accountOld.isPresent()) {
            Account account = accountOld.get();
            account.setAccountNumber(newAccount.getAccountNumber());
            account.setBalance(newAccount.getBalance());
            account.setUser(newAccount.getUser());
            account.setTransactions(newAccount.getTransactions());
            accountRepository.save(account);
            log.info("Account with ID {} updated successfully", id);
            return new ResponseEntity<>("Account updated successfully", HttpStatus.OK);
        } else {
            log.warn("Account with ID {} not found", id);
            throw new AccountNotFoundException("Account not found with ID: " + id); // Throwing custom exception
        }
    }

    // Delete an account
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        log.info("Deleting account with ID {}", id);
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            accountRepository.deleteById(id);
            log.info("Account with ID {} deleted successfully", id);
            return new ResponseEntity<>("Account deleted successfully", HttpStatus.OK);
        } else {
            log.warn("Account with ID {} not found", id);
            throw new AccountNotFoundException("Account not found with ID: " + id); // Throwing custom exception
        }
    }

    @GetMapping("/fetch/user/{userId}")
    public ResponseEntity<?> getAccountsByUserId(@PathVariable Long userId) {
        log.info("Fetching accounts for user with ID {}", userId);
        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            log.warn("No accounts found for user with ID {}", userId);
            // Return 404 with a custom message
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "No accounts found for user with ID " + userId));
        } else {
            log.info("Found {} accounts for user with ID {}", accounts.size(), userId);
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        }
    }



}

