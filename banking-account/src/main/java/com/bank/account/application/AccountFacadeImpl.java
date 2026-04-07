package com.bank.account.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountFacadeImpl implements AccountFacade {

    private final AccountServiceImpl accountService;

    @Override
    public void createInitialAccount(String ownerEmail) {
        accountService.createAccountForOwner(ownerEmail);
    }
}
