package com.banking.account.controllers;

import com.banking.account.dtos.DepositWithdrawDto;
import com.banking.account.dtos.StatementDto;
import com.banking.shared.dtos.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
public interface AccountController {

    @PostMapping("/accounts/{accountId}/deposit")
    ResponseEntity<ResponseDTO<Void>> deposit(@PathVariable Long accountId, @RequestBody DepositWithdrawDto request);

    @PostMapping("/accounts/{accountId}/withdraw")
    ResponseEntity<ResponseDTO<Void>> withdraw(@PathVariable Long accountId, @RequestBody DepositWithdrawDto request);

    @GetMapping("/accounts/{accountId}/statement")
    ResponseEntity<ResponseDTO<StatementDto>> getStatement(@PathVariable Long accountId);
}