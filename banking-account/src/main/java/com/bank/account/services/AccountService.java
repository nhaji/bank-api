package com.bank.account.services;

import java.util.List;

import com.bank.account.dtos.AccountDto;
import com.bank.account.dtos.StatementDto;

public interface AccountService {

    public static final String ACCOUNT_PREFIX = "ACC-";
    
    AccountDto createAccountForUser(Long userId);
    void deposit(Long accountId, Long amount);
    void withdraw(Long accountId, Long amount);
    StatementDto getStatement(Long accountId);
    List<AccountDto> getUserAccounts() ;
}