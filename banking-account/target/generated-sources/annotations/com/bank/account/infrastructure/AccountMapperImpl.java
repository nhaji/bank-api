package com.bank.account.infrastructure;

import com.bank.account.api.AccountDto;
import com.bank.account.api.StatementDto;
import com.bank.account.api.TransactionDto;
import com.bank.account.domain.Account;
import com.bank.account.domain.Transaction;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-07T23:27:54+0000",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public TransactionDto toTransactionDto(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        TransactionDto.TransactionDtoBuilder transactionDto = TransactionDto.builder();

        transactionDto.date( transaction.getTransactionDate() );
        transactionDto.balance( transaction.getBalanceAfter() );
        transactionDto.amount( transaction.getAmount() );

        return transactionDto.build();
    }

    @Override
    public AccountDto toAccountDto(Account account) {
        if ( account == null ) {
            return null;
        }

        AccountDto.AccountDtoBuilder accountDto = AccountDto.builder();

        accountDto.id( account.getId() );
        accountDto.accountNumber( account.getAccountNumber() );

        return accountDto.build();
    }

    @Override
    public List<AccountDto> toAccountDtoList(List<Account> accounts) {
        if ( accounts == null ) {
            return null;
        }

        List<AccountDto> list = new ArrayList<AccountDto>( accounts.size() );
        for ( Account account : accounts ) {
            list.add( toAccountDto( account ) );
        }

        return list;
    }

    @Override
    public StatementDto toStatementDto(Account account) {
        if ( account == null ) {
            return null;
        }

        StatementDto.StatementDtoBuilder statementDto = StatementDto.builder();

        statementDto.transactions( toTransactionDtoList( account.getTransactions() ) );
        statementDto.accountNumber( account.getAccountNumber() );

        return statementDto.build();
    }

    @Override
    public List<TransactionDto> toTransactionDtoList(List<Transaction> transactions) {
        if ( transactions == null ) {
            return null;
        }

        List<TransactionDto> list = new ArrayList<TransactionDto>( transactions.size() );
        for ( Transaction transaction : transactions ) {
            list.add( toTransactionDto( transaction ) );
        }

        return list;
    }
}
