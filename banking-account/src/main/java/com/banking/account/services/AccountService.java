package com.banking.account.services;

import com.banking.account.dtos.StatementDto;
import com.banking.account.entities.Account;

public interface AccountService {
    Account createAccountForUser(Long userId);
    void deposit(Long accountId, Long amount);
    void withdraw(Long accountId, Long amount);
    StatementDto getStatement(Long accountId);
}