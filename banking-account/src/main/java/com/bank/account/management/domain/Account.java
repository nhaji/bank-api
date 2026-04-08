package com.bank.account.management.domain;

import com.bank.account.transactions.domain.Transaction;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String accountNumber;

  @Column(nullable = false)
  private String ownerEmail;

  @Builder.Default
  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("transactionDate ASC")
  private List<Transaction> transactions = new ArrayList<>();
}
