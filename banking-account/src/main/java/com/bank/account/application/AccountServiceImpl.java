package com.bank.account.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.account.api.AccountDto;
import com.bank.account.api.StatementDto;
import com.bank.account.domain.Account;
import com.bank.account.domain.AccountBusinessException;
import com.bank.account.domain.Transaction;
import com.bank.account.domain.Transaction.TransactionType;
import com.bank.account.infrastructure.AccountMapper;
import com.bank.account.infrastructure.AccountRepository;
import com.bank.account.infrastructure.TransactionRepository;
import com.bank.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountDto createAccountForCurrentUser() {
        Account account = createNewAccount(getCurrentUserEmail());
        createInitialTransaction(account);
        return accountMapper.toAccountDto(account);
    }

    @Transactional
    public AccountDto createAccountForOwner(String ownerEmail) {
        Account account = createNewAccount(ownerEmail);
        createInitialTransaction(account);
        return accountMapper.toAccountDto(account);
    }

    @Override
    @Transactional
    public void deposit(Long accountId, Long amount) {
        if (amount <= 0) {
            throw new BusinessException(AccountBusinessException.NEGATIVE_AMOUNT_DEPOSIT_CODE, AccountBusinessException.NEGATIVE_AMOUNT_DEPOSIT_MSG);
        }
        Account account = getAccountAndVerifyOwnership(accountId, getCurrentUserEmail());
        Long newBalance = getCurrentBalance(account) + amount;
        createTransaction(account, amount, TransactionType.DEPOSIT, newBalance);
    }

    @Override
    @Transactional
    public void withdraw(Long accountId, Long amount) {
        if (amount <= 0) {
            throw new BusinessException(AccountBusinessException.NEGATIVE_AMOUNT_WITHDRAW_CODE, AccountBusinessException.NEGATIVE_AMOUNT_WITHDRAW_MSG);
        }
        Account account = getAccountAndVerifyOwnership(accountId, getCurrentUserEmail());
        Long currentBalance = getCurrentBalance(account);
        if (currentBalance < amount) {
            throw new BusinessException(AccountBusinessException.INSUFFICIENT_BALANCE_CODE, AccountBusinessException.INSUFFICIENT_BALANCE_MSG);
        }
        createTransaction(account, -amount, TransactionType.WITHDRAW, currentBalance - amount);
    }

    @Override
    @Transactional(readOnly = true)
    public StatementDto getStatement(Long accountId) {
        Account account = getAccountAndVerifyOwnership(accountId, getCurrentUserEmail());
        return accountMapper.toStatementDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getCurrentUserAccounts() {
        return accountMapper.toAccountDtoList(accountRepository.findByOwnerEmailOrderByIdAsc(getCurrentUserEmail()));
    }

    private Account getAccountAndVerifyOwnership(Long accountId, String ownerEmail) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(AccountBusinessException.ACCOUNT_NOT_FOUND_CODE, AccountBusinessException.ACCOUNT_NOT_FOUND_MSG));
        if (!account.getOwnerEmail().equals(ownerEmail)) {
            throw new BusinessException(AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_CODE, AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_MSG);
        }
        return account;
    }

    private Account createNewAccount(String ownerEmail) {
        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .ownerEmail(ownerEmail)
                .build();
        return accountRepository.save(account);
    }

    private void createInitialTransaction(Account account) {
        createTransaction(account, 0L, TransactionType.INITIAL, 0L);
    }

    private Long getCurrentBalance(Account account) {
        return account.getTransactions().stream()
                .map(Transaction::getBalanceAfter)
                .reduce((first, second) -> second)
                .orElse(0L);
    }

    private void createTransaction(Account account, Long amount, TransactionType type, Long newBalance) {
        Transaction tx = Transaction.builder()
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

    private String generateAccountNumber() {
        return ACCOUNT_PREFIX + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
