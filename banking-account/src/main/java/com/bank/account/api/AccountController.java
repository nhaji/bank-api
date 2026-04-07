package com.bank.account.api;

import com.bank.account.application.AccountService;
import java.util.List;
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
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AccountDto createAccount() {
    return accountService.createAccountForCurrentUser();
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<AccountDto> getUserAccounts() {
    return accountService.getCurrentUserAccounts();
  }

  @PostMapping("/{accountId}/deposit")
  @ResponseStatus(HttpStatus.OK)
  public void deposit(@PathVariable Long accountId, @RequestBody DepositWithdrawDto request) {
    accountService.deposit(accountId, request.getAmount());
  }

  @PostMapping("/{accountId}/withdraw")
  @ResponseStatus(HttpStatus.OK)
  public void withdraw(@PathVariable Long accountId, @RequestBody DepositWithdrawDto request) {
    accountService.withdraw(accountId, request.getAmount());
  }

  @GetMapping("/{accountId}/statement")
  @ResponseStatus(HttpStatus.OK)
  public StatementDto getStatement(@PathVariable Long accountId) {
    return accountService.getStatement(accountId);
  }
}
