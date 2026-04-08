package com.bank.account.transactions.application;

import com.bank.account.transactions.api.StatementDto;

public interface AccountTransactionsService {

  void deposit(Long accountId, Long amount);

  void withdraw(Long accountId, Long amount);

  StatementDto getStatement(Long accountId);
}
