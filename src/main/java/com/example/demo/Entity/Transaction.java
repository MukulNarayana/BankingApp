package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 'id' column in Transaction table

    private double amount; // 'amount' column
    private String transactionType; // 'transaction_type' column

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account; // 'account_id' column in Transaction table
}




