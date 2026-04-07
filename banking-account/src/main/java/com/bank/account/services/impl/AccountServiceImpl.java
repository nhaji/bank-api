package com.bank.account.services.impl;

import com.bank.account.dtos.AccountDto;
import com.bank.account.dtos.StatementDto;
import com.bank.account.entities.Account;
import com.bank.account.entities.Transaction;
import com.bank.account.entities.User;
import com.bank.account.entities.Transaction.TransactionType;
import com.bank.account.exceptions.AccountBusinessException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repositories.AccountRepository;
import com.bank.account.repositories.TransactionRepository;
import com.bank.account.services.AccountService;
import com.bank.account.services.UserService;
import com.bank.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountDto createAccountForUser(Long userId) {
        User user = userService.getUserById(userId);
        Account account = createNewAccount(user);
        createInitialTransaction(account);
        return accountMapper.toAccountDto(account);
    }

    @Override
    @Transactional
    public void deposit(Long accountId, Long amount) {
        if (amount <= 0) {
            throw new BusinessException(AccountBusinessException.NEGATIVE_AMOUNT_DEPOSIT_CODE, AccountBusinessException.NEGATIVE_AMOUNT_DEPOSIT_MSG);
        }
        User currentUser = userService.getCurrentUser();
        Account account = getAccountAndVerifyOwnership(accountId, currentUser.getId());
        Long currentBalance = getCurrentBalance(account);
        Long newBalance = currentBalance + amount;
        createTransaction(account, amount, Transaction.TransactionType.DEPOSIT, newBalance);
    }

    @Override
    @Transactional
    public void withdraw(Long accountId, Long amount) {
        if (amount <= 0) {
            throw new BusinessException(AccountBusinessException.NEGATIVE_AMOUNT_WITHDRAW_CODE, AccountBusinessException.NEGATIVE_AMOUNT_WITHDRAW_MSG);
        }
        User currentUser = userService.getCurrentUser();
        Account account = getAccountAndVerifyOwnership(accountId, currentUser.getId());
        Long currentBalance = getCurrentBalance(account);
        if (currentBalance < amount) {
            throw new BusinessException(AccountBusinessException.INSUFFICIENT_BALANCE_CODE, AccountBusinessException.INSUFFICIENT_BALANCE_MSG);
        }
        Long newBalance = currentBalance - amount;
        createTransaction(account, -amount, Transaction.TransactionType.WITHDRAW, newBalance);
    }

    @Override
    @Transactional(readOnly = true)
    public StatementDto getStatement(Long accountId) {
        User currentUser = userService.getCurrentUser();
        Account account = getAccountAndVerifyOwnership(accountId, currentUser.getId());
        return accountMapper.toStatementDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getUserAccounts() {
        User currentUser = userService.getCurrentUser();
        return accountMapper.toAccountDtoList(currentUser.getAccounts());
    }

    private Account getAccountAndVerifyOwnership(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(AccountBusinessException.ACCOUNT_NOT_FOUND_CODE, AccountBusinessException.ACCOUNT_NOT_FOUND_MSG));
        if (!account.getUser().getId().equals(userId)) {
            throw new BusinessException(AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_CODE, AccountBusinessException.ACCOUNT_ACCES_UNAUTHORIZED_MSG);
        }
        return account;
    }

    private Account createNewAccount(User user) {
        String accountNumber = generateAccountNumber();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .user(user)
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

    private void createTransaction(Account account, Long amount, Transaction.TransactionType type, Long newBalance) {
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
}