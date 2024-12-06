package com.example.demo.Controllers;

import com.example.demo.DTO.TransactionRequest;
import com.example.demo.Entity.Account;
import com.example.demo.Entity.Transaction;
import com.example.demo.Exception.TransactionNotFoundException;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for adding a new transaction
    @Test
    void testAddTransaction_Success() {
        TransactionRequest request = new TransactionRequest(100.0, "Deposit", 1L);
        Account account = new Account();
        account.setId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        ResponseEntity<String> response = transactionController.addTransaction(request);

        assertEquals(CREATED, response.getStatusCode());
        assertEquals("Transaction added successfully", response.getBody());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testAddTransaction_AccountNotFound() {
        TransactionRequest request = new TransactionRequest(100.0, "Deposit", 1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = transactionController.addTransaction(request);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found", response.getBody());
    }

    @Test
    void testAddTransaction_MissingAccountId() {
        TransactionRequest request = new TransactionRequest(100.0, "Deposit", null);

        ResponseEntity<String> response = transactionController.addTransaction(request);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Account ID must be provided", response.getBody());
    }

    // Test for fetching all transactions
    @Test
    void testShowAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        when(transactionRepository.findAll()).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.showAllTransactions();

        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // Test for fetching transaction by ID (Success)
    @Test
    void testShowTransactionById_Success() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        ResponseEntity<Transaction> response = transactionController.showTransactionById(1L);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    // Test for fetching transaction by ID (Not Found)
    @Test
    void testShowTransactionById_NotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TransactionNotFoundException.class, () ->
                transactionController.showTransactionById(1L)
        );

        assertEquals("Transaction not found with ID: 1", exception.getMessage());
    }

    // Test for updating a transaction (Success)
    @Test
    void testUpdateTransaction_Success() {
        TransactionRequest request = new TransactionRequest(200.0, "Withdrawal", 1L);
        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(1L);

        Account account = new Account();
        account.setId(1L);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existingTransaction));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        ResponseEntity<String> response = transactionController.updateTransaction(1L, request);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Transaction updated successfully", response.getBody());
        verify(transactionRepository, times(1)).save(existingTransaction);
    }

    // Test for updating a transaction (Transaction Not Found)
    @Test
    void testUpdateTransaction_NotFound() {
        TransactionRequest request = new TransactionRequest(200.0, "Withdrawal", 1L);

        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TransactionNotFoundException.class, () ->
                transactionController.updateTransaction(1L, request)
        );

        assertEquals("Transaction not found with ID: 1", exception.getMessage());
    }

    // Test for deleting a transaction (Success)
    @Test
    void testDeleteTransaction_Success() {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<String> response = transactionController.deleteTransaction(1L);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Transaction deleted successfully", response.getBody());
        verify(transactionRepository, times(1)).deleteById(1L);
    }

    // Test for deleting a transaction (Not Found)
    @Test
    void testDeleteTransaction_NotFound() {
        when(transactionRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(TransactionNotFoundException.class, () ->
                transactionController.deleteTransaction(1L)
        );

        assertEquals("Transaction not found with ID: 1", exception.getMessage());
    }

    // Test for fetching transactions by account ID
    @Test
    void testGetTransactionsByAccountId_Success() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());

        when(transactionRepository.findByAccountId(1L)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByAccountId(1L);

        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetTransactionsByAccountId_NotFound() {
        when(transactionRepository.findByAccountId(1L)).thenReturn(new ArrayList<>());

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByAccountId(1L);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }
}



