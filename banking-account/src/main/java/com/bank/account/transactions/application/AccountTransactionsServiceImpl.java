package com.bank.account.transactions.application;

import com.bank.account.management.domain.Account;
import com.bank.account.management.domain.AccountBusinessException;
import com.bank.account.management.infrastructure.AccountMapper;
import com.bank.account.management.infrastructure.AccountRepository;
import com.bank.account.transactions.api.StatementDto;
import com.bank.account.transactions.domain.Transaction;
import com.bank.account.transactions.domain.Transaction.TransactionType;
import com.bank.account.transactions.infrastructure.TransactionRepository;
import com.bank.shared.exceptions.BusinessException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountTransactionsServiceImpl implements AccountTransactionsService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final AccountMapper accountMapper;

  @Override
  @Transactional
  public void deposit(Long accountId, Long amount) {
    if (amount == null || amount <= 0) {
      throw new BusinessException(
          AccountBusinessException.NEGATIVE_AMOUNT_DEPOSIT_CODE,
          AccountBusinessException.NEGATIVE_AMOUNT_DEPOSIT_MSG);
    }
    Account account = getAccountForCurrentUser(accountId);
    Long newBalance = currentBalance(account) + amount;
    createTransaction(account, amount, TransactionType.DEPOSIT, newBalance);
  }

  @Override
  @Transactional
  public void withdraw(Long accountId, Long amount) {
    if (amount == null || amount <= 0) {
      throw new BusinessException(
          AccountBusinessException.NEGATIVE_AMOUNT_WITHDRAW_CODE,
          AccountBusinessException.NEGATIVE_AMOUNT_WITHDRAW_MSG);
    }
    Account account = getAccountForCurrentUser(accountId);
    Long balance = currentBalance(account);
    if (balance < amount) {
      throw new BusinessException(
          AccountBusinessException.INSUFFICIENT_BALANCE_CODE,
          AccountBusinessException.INSUFFICIENT_BALANCE_MSG);
    }
    createTransaction(account, -amount, TransactionType.WITHDRAW, balance - amount);
  }

  @Override
  @Transactional(readOnly = true)
  public StatementDto getStatement(Long accountId) {
    Account account = getAccountForCurrentUser(accountId);
    return accountMapper.toStatementDto(account);
  }

  private Account getAccountForCurrentUser(Long accountId) {
    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        AccountBusinessException.ACCOUNT_NOT_FOUND_CODE,
                        AccountBusinessException.ACCOUNT_NOT_FOUND_MSG));
    String ownerEmail = getCurrentUserEmail();
    if (!account.getOwnerEmail().equals(ownerEmail)) {
      throw new BusinessException(
          AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_CODE,
          AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_MSG);
    }
    return account;
  }

  private Long currentBalance(Account account) {
    return account.getTransactions().stream()
        .map(Transaction::getBalanceAfter)
        .reduce((first, second) -> second)
        .orElse(0L);
  }

  private void createTransaction(
      Account account, Long amount, TransactionType type, Long newBalance) {
    Transaction tx =
        Transaction.builder()
            .amount(amount)
            .type(type)
            .balanceAfter(newBalance)
            .transactionDate(LocalDateTime.now())
            .account(account)
            .build();
    transactionRepository.save(tx);
    account.getTransactions().add(tx);
    accountRepository.save(account);
  }

  private String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new BusinessException(
          AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_CODE,
          AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_MSG);
    }
    return authentication.getName();
  }
}
