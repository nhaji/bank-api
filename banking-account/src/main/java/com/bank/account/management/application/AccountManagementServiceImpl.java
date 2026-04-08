package com.bank.account.management.application;

import com.bank.account.AccountConstants;
import com.bank.account.management.api.AccountDto;
import com.bank.account.management.domain.Account;
import com.bank.account.management.domain.AccountBusinessException;
import com.bank.account.management.infrastructure.AccountMapper;
import com.bank.account.management.infrastructure.AccountRepository;
import com.bank.account.transactions.domain.Transaction;
import com.bank.account.transactions.domain.Transaction.TransactionType;
import com.bank.account.transactions.infrastructure.TransactionRepository;
import com.bank.shared.exceptions.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountManagementServiceImpl implements AccountManagementService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final AccountMapper accountMapper;

  @Override
  @Transactional
  public AccountDto createAccountForCurrentUser() {
    return createAccountForOwner(getCurrentUserEmail());
  }

  @Override
  @Transactional
  public AccountDto createAccountForOwner(String ownerEmail) {
    Account account =
        accountRepository.save(
            Account.builder()
                .accountNumber(generateAccountNumber())
                .ownerEmail(ownerEmail)
                .build());
    createInitialTransaction(account);
    return accountMapper.toAccountDto(account);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AccountDto> getCurrentUserAccounts() {
    return accountMapper.toAccountDtoList(
        accountRepository.findByOwnerEmailOrderByIdAsc(getCurrentUserEmail()));
  }

  private void createInitialTransaction(Account account) {
    Transaction tx =
        Transaction.builder()
            .amount(0L)
            .type(TransactionType.INITIAL)
            .balanceAfter(0L)
            .transactionDate(LocalDateTime.now())
            .account(account)
            .build();
    transactionRepository.save(tx);
    account.getTransactions().add(tx);
    accountRepository.save(account);
  }

  private String generateAccountNumber() {
    return AccountConstants.ACCOUNT_PREFIX
        + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
