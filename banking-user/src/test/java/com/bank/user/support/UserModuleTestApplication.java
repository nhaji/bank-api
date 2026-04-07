package com.bank.user.support;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.bank.shared.BankingSharedConfig;
import com.bank.user.BankingUserConfig;

@SpringBootApplication
@Import({BankingSharedConfig.class, BankingUserConfig.class})
public class UserModuleTestApplication {
}
