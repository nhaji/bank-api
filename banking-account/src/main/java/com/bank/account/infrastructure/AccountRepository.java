package com.bank.account.infrastructure;

import com.bank.account.domain.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByAccountNumber(String accountNumber);

  List<Account> findByOwnerEmailOrderByIdAsc(String ownerEmail);
}
