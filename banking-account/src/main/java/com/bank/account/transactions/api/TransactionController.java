package com.bank.account.transactions.api;

import com.bank.account.transactions.application.AccountTransactionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts/{accountId}")
@RequiredArgsConstructor
public class TransactionController {

  private final AccountTransactionsService accountTransactionsService;

  @PostMapping("/deposit")
  @ResponseStatus(HttpStatus.OK)
  public void deposit(
      @PathVariable Long accountId, @Valid @RequestBody DepositWithdrawDto request) {
    accountTransactionsService.deposit(accountId, request.getAmount());
  }

  @PostMapping("/withdraw")
  @ResponseStatus(HttpStatus.OK)
  public void withdraw(
      @PathVariable Long accountId, @Valid @RequestBody DepositWithdrawDto request) {
    accountTransactionsService.withdraw(accountId, request.getAmount());
  }

  @GetMapping("/statement")
  @ResponseStatus(HttpStatus.OK)
  public StatementDto statement(@PathVariable Long accountId) {
    return accountTransactionsService.getStatement(accountId);
  }
}
