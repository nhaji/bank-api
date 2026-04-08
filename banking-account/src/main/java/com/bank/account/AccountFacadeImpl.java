package com.bank.account;

import com.bank.account.management.application.AccountManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountFacadeImpl implements AccountFacade {

  private final AccountManagementService accountService;

  @Override
  public void createInitialAccount(String ownerEmail) {
    accountService.createAccountForOwner(ownerEmail);
  }
}
