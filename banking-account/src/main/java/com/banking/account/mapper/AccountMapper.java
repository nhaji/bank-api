package com.banking.account.mapper;

import com.banking.account.dtos.StatementDto;
import com.banking.account.dtos.TransactionDto;
import com.banking.account.entities.Account;
import com.banking.account.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    TransactionDto toTransactionDto(Transaction transaction);

    @Mapping(target = "transactions", source = "transactions")
    StatementDto toStatementDto(Account account);

    List<TransactionDto> toTransactionDtoList(List<Transaction> transactions);
}
