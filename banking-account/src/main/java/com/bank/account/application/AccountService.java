package com.bank.account.application;

import com.bank.account.api.AccountDto;
import com.bank.account.api.StatementDto;
import java.util.List;

public interface AccountService {

  String ACCOUNT_PREFIX = "ACC-";

  AccountDto createAccountForCurrentUser();

  void deposit(Long accountId, Long amount);

  void withdraw(Long accountId, Long amount);

  StatementDto getStatement(Long accountId);

  List<AccountDto> getCurrentUserAccounts();
}
