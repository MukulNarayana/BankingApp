package com.example.demo.Controllers;

import com.example.demo.Entity.Account;
import com.example.demo.Exception.AccountNotFoundException;
import com.example.demo.Repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for fetching all accounts
    @Test
    public void testShowAllAccounts() {
        Account account1 = new Account(1L, "12345", 1000.0, null, null);
        Account account2 = new Account(2L, "67890", 2000.0, null, null);

        when(accountRepository.findAll()).thenReturn(Arrays.asList(account1, account2));

        ResponseEntity<List<Account>> response = accountController.showAllAccounts();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());

        verify(accountRepository, times(1)).findAll();
    }

    // Test for fetching an account by ID (success)
    @Test
    public void testShowAccountById_Success() {
        Account account = new Account(1L, "12345", 1000.0, null, null);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        ResponseEntity<Account> response = accountController.showAccountById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("12345", response.getBody().getAccountNumber());

        verify(accountRepository, times(1)).findById(1L);
    }

    // Test for fetching an account by ID (failure)
    @Test
    public void testShowAccountById_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            accountController.showAccountById(1L);
        } catch (AccountNotFoundException ex) {
            assertEquals("Account not found with ID: 1", ex.getMessage());
        }

        verify(accountRepository, times(1)).findById(1L);
    }

    // Test for adding a new account
    @Test
    public void testAddAccount() {
        Account newAccount = new Account(1L, "12345", 1000.0, null, null);

        when(accountRepository.save(newAccount)).thenReturn(newAccount);

        ResponseEntity<String> response = accountController.addAccount(newAccount);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Account added successfully", response.getBody());

        verify(accountRepository, times(1)).save(newAccount);
    }

    // Test for updating an existing account (success)
    @Test
    public void testUpdateAccount_Success() {
        Account existingAccount = new Account(1L, "12345", 1000.0, null, null);
        Account updatedAccount = new Account(1L, "67890", 2000.0, null, null);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(updatedAccount);

        ResponseEntity<String> response = accountController.updateAccount(updatedAccount, 1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Account updated successfully", response.getBody());

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(existingAccount);
    }

    // Test for updating an existing account (failure)
    @Test
    public void testUpdateAccount_NotFound() {
        Account updatedAccount = new Account(1L, "67890", 2000.0, null, null);

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            accountController.updateAccount(updatedAccount, 1L);
        } catch (AccountNotFoundException ex) {
            assertEquals("Account not found with ID: 1", ex.getMessage());
        }

        verify(accountRepository, times(1)).findById(1L);
    }

    // Test for deleting an account (success)
    @Test
    public void testDeleteAccount_Success() {
        Account account = new Account(1L, "12345", 1000.0, null, null);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        ResponseEntity<String> response = accountController.deleteAccount(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Account deleted successfully", response.getBody());

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).deleteById(1L);
    }

    // Test for deleting an account (failure)
    @Test
    public void testDeleteAccount_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            accountController.deleteAccount(1L);
        } catch (AccountNotFoundException ex) {
            assertEquals("Account not found with ID: 1", ex.getMessage());
        }

        verify(accountRepository, times(1)).findById(1L);
    }

    // Test for fetching accounts by user ID
    @Test
    public void testGetAccountsByUserId() {
        Account account1 = new Account(1L, "12345", 1000.0, null, null);
        Account account2 = new Account(2L, "67890", 2000.0, null, null);

        when(accountRepository.findByUserId(1L)).thenReturn(Arrays.asList(account1, account2));

        ResponseEntity<?> response = accountController.getAccountsByUserId(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, ((List<Account>) response.getBody()).size());

        verify(accountRepository, times(1)).findByUserId(1L);
    }
}
