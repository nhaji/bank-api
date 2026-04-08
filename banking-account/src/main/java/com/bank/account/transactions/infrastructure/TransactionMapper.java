package com.bank.account.transactions.infrastructure;

import com.bank.account.management.domain.Account;
import com.bank.account.transactions.api.StatementDto;
import com.bank.account.transactions.api.TransactionDto;
import com.bank.account.transactions.domain.Transaction;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(target = "date", source = "transactionDate")
  @Mapping(target = "balance", source = "balanceAfter")
  TransactionDto toTransactionDto(Transaction transaction);

  List<TransactionDto> toTransactionDtoList(List<Transaction> transactions);

  @Mapping(target = "transactions", source = "transactions")
  StatementDto toStatementDto(Account account);
}
