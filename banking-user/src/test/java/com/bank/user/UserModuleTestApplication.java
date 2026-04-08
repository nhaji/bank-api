package com.bank.user;

import com.bank.shared.BankingSharedConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({BankingSharedConfig.class, BankingUserConfig.class})
public class UserModuleTestApplication {}
