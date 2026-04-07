package com.bank.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bank.account.dtos.AccountDto;
import com.bank.account.dtos.StatementDto;
import com.bank.account.dtos.TransactionDto;
import com.bank.account.entities.Account;
import com.bank.account.entities.Transaction;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    TransactionDto toTransactionDto(Transaction transaction);

    AccountDto toAccountDto(Account account);

    List<AccountDto> toAccountDtoList(List<Account> accounts);

    @Mapping(target = "transactions", source = "transactions")
    StatementDto toStatementDto(Account account);

    List<TransactionDto> toTransactionDtoList(List<Transaction> transactions);
}
