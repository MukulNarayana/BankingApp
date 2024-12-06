package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private double amount;          // Field for transaction amount
    private String transactionType; // Field for transaction type (e.g., Deposit or Withdrawal)
    private Long accountId;
}
