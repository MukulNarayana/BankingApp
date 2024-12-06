package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 'id' column in Account table

    private String accountNumber; // 'account_number' column
    private double balance; // 'balance' column

    @ManyToOne
    private User user; // 'user_id' column linking to User

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private Set<Transaction> transactions; // 'transactions' column not needed, just the relationship
}

