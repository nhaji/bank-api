package com.banking.account.controllers;

import com.banking.account.dtos.AccountDto;
import com.banking.account.dtos.DepositWithdrawDto;
import com.banking.account.dtos.StatementDto;
import com.banking.shared.dtos.ResponseDTO;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/accounts")
public interface AccountController {

    @PostMapping
    ResponseEntity<ResponseDTO<AccountDto>> createAccount();

    @GetMapping
    ResponseEntity<ResponseDTO<List<AccountDto>>> getUserAccounts();

    @PostMapping("/{accountId}/deposit")
    ResponseEntity<ResponseDTO<Void>> deposit(@PathVariable Long accountId, @RequestBody DepositWithdrawDto request);

    @PostMapping("/{accountId}/withdraw")
    ResponseEntity<ResponseDTO<Void>> withdraw(@PathVariable Long accountId, @RequestBody DepositWithdrawDto request);

    @GetMapping("/{accountId}/statement")
    ResponseEntity<ResponseDTO<StatementDto>> getStatement(@PathVariable Long accountId);
}