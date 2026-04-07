package com.bank.account.controllers;

import com.bank.account.dtos.AccountDto;
import com.bank.account.dtos.DepositWithdrawDto;
import com.bank.account.dtos.StatementDto;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/accounts")
public interface AccountController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AccountDto createAccount();

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<AccountDto> getUserAccounts();

    @PostMapping("/{accountId}/deposit")
    @ResponseStatus(HttpStatus.OK)
    void deposit(@PathVariable Long accountId, @RequestBody DepositWithdrawDto request);

    @PostMapping("/{accountId}/withdraw")
    @ResponseStatus(HttpStatus.OK)
    void withdraw(@PathVariable Long accountId, @RequestBody DepositWithdrawDto request);

    @GetMapping("/{accountId}/statement")
    @ResponseStatus(HttpStatus.OK)
    StatementDto getStatement(@PathVariable Long accountId);
}