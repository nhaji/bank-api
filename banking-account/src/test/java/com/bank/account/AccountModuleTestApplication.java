package com.bank.account;

import com.bank.shared.BankingSharedConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({BankingSharedConfig.class, BankingAccountConfig.class})
public class AccountModuleTestApplication {}
