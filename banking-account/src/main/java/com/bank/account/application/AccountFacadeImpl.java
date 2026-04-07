package com.bank.account.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountFacadeImpl implements AccountFacade {

  private final AccountServiceImpl accountService;

  @Override
  public void createInitialAccount(String ownerEmail) {
    accountService.createAccountForOwner(ownerEmail);
  }
}
