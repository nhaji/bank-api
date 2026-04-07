package com.bank.account.infrastructure;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bank.account.api.AccountDto;
import com.bank.account.api.StatementDto;
import com.bank.account.api.TransactionDto;
import com.bank.account.domain.Account;
import com.bank.account.domain.Transaction;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "date", source = "transactionDate")
    @Mapping(target = "balance", source = "balanceAfter")
    TransactionDto toTransactionDto(Transaction transaction);

    AccountDto toAccountDto(Account account);

    List<AccountDto> toAccountDtoList(List<Account> accounts);

    @Mapping(target = "transactions", source = "transactions")
    StatementDto toStatementDto(Account account);

    List<TransactionDto> toTransactionDtoList(List<Transaction> transactions);
}
