package com.example.demo.Controllers;

import com.example.demo.DTO.TransactionRequest;
import com.example.demo.Entity.Transaction;
import com.example.demo.Entity.Account;
import com.example.demo.Exception.TransactionNotFoundException;
import com.example.demo.Repository.TransactionRepository;
import com.example.demo.Repository.AccountRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
@CrossOrigin(origins = "*")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Create a new transaction
    @PostMapping("/add")
    public ResponseEntity<String> addTransaction(@RequestBody TransactionRequest transactionRequest) {
        log.info("Request received to add a new transaction for account ID: {}", transactionRequest.getAccountId());

        if (transactionRequest.getAccountId() == null) {
            log.warn("Account ID not provided in transaction request");
            return new ResponseEntity<>("Account ID must be provided", HttpStatus.BAD_REQUEST);
        }

        Optional<Account> accountOptional = accountRepository.findById(transactionRequest.getAccountId());
        if (accountOptional.isEmpty()) {
            log.warn("Account not found with ID: {}", transactionRequest.getAccountId());
            return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setTransactionType(transactionRequest.getTransactionType());
        transaction.setAccount(accountOptional.get());

        transactionRepository.save(transaction);
        log.info("Transaction added successfully for account ID: {}", transactionRequest.getAccountId());
        return new ResponseEntity<>("Transaction added successfully", HttpStatus.CREATED);
    }

    // Read all transactions
    @GetMapping("/fetch")
    public ResponseEntity<List<Transaction>> showAllTransactions() {
        log.info("Request received to show all transactions");
        List<Transaction> transactions = transactionRepository.findAll();
        log.info("Returning {} transactions", transactions.size());
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // Read a specific transaction by ID
    @GetMapping("/fetch/{id}")
    public ResponseEntity<Transaction> showTransactionById(@PathVariable Long id) {
        log.info("Request received to show transaction with ID: {}", id);
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            log.info("Transaction found with ID: {}", id);
            return new ResponseEntity<>(transaction.get(), HttpStatus.OK);
        } else {
            log.warn("Transaction not found with ID: {}", id);
            throw new TransactionNotFoundException("Transaction not found with ID: " + id);
        }
    }

    // Update an existing transaction
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable Long id, @RequestBody TransactionRequest transactionRequest) {
        log.info("Request received to update transaction with ID: {}", id);
        Optional<Transaction> existingTransaction = transactionRepository.findById(id);
        if (existingTransaction.isPresent()) {
            Optional<Account> accountOptional = accountRepository.findById(transactionRequest.getAccountId());
            if (accountOptional.isEmpty()) {
                log.warn("Account not found with ID: {}", transactionRequest.getAccountId());
                return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
            }

            Transaction transaction = existingTransaction.get();
            transaction.setAmount(transactionRequest.getAmount());
            transaction.setTransactionType(transactionRequest.getTransactionType());
            transaction.setAccount(accountOptional.get());

            transactionRepository.save(transaction);
            log.info("Transaction updated successfully with ID: {}", id);
            return new ResponseEntity<>("Transaction updated successfully", HttpStatus.OK);
        } else {
            log.warn("Transaction not found with ID: {}", id);
            throw new TransactionNotFoundException("Transaction not found with ID: " + id);
        }
    }

    // Delete a transaction
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        log.info("Request received to delete transaction with ID: {}", id);
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            log.info("Transaction deleted successfully with ID: {}", id);
            return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
        } else {
            log.warn("Transaction not found with ID: {}", id);
            throw new TransactionNotFoundException("Transaction not found with ID: " + id);
        }
    }

    // Get transactions by account ID
    @GetMapping("/fetch/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable Long accountId) {
        log.info("Request received to get transactions for account ID: {}", accountId);
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        if (transactions.isEmpty()) {
            log.warn("No transactions found for account ID: {}", accountId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("Returning {} transactions for account ID: {}", transactions.size(), accountId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}
