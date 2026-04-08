package com.bank.account.management.infrastructure;

import com.bank.account.management.api.AccountDto;
import com.bank.account.management.domain.Account;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  AccountDto toAccountDto(Account account);

  List<AccountDto> toAccountDtoList(List<Account> accounts);
}
