package com.bank.account.management.api;

import com.bank.account.management.application.AccountManagementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountManagementController {

  private final AccountManagementService accountManagementService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AccountDto createAccount() {
    return accountManagementService.createAccountForCurrentUser();
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<AccountDto> listAccounts() {
    return accountManagementService.getCurrentUserAccounts();
  }
}
