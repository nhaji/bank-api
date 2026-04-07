package com.bank.account.controllers.impl;

import com.bank.account.controllers.AccountController;
import com.bank.account.dtos.*;
import com.bank.account.entities.User;
import com.bank.account.services.AccountService;
import com.bank.account.services.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @Override
    public AccountDto createAccount() {
        User currentUser = userService.getCurrentUser();
        return accountService.createAccountForUser(currentUser.getId());
    }

    @Override
    public List<AccountDto> getUserAccounts() {
        return accountService.getUserAccounts();
    }

    @Override
    public void deposit(Long accountId, DepositWithdrawDto request) {
        accountService.deposit(accountId, request.getAmount());
    }

    @Override
    public void withdraw(Long accountId, DepositWithdrawDto request) {
        accountService.withdraw(accountId, request.getAmount());
    }

    @Override
    public StatementDto getStatement(Long accountId) {
        return  accountService.getStatement(accountId);
    }

}