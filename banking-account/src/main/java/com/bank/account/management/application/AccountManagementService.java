package com.bank.account.management.application;

import com.bank.account.management.api.AccountDto;
import java.util.List;

public interface AccountManagementService {

  AccountDto createAccountForCurrentUser();

  AccountDto createAccountForOwner(String ownerEmail);

  List<AccountDto> getCurrentUserAccounts();
}
