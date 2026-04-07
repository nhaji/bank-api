package com.bank.account;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.bank.shared.BankingSharedConfig;

@SpringBootApplication
@Import({BankingSharedConfig.class, BankingAccountConfig.class})
public class AccountModuleTestApplication {
}
