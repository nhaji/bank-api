package com.bank.account.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.account.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
