package com.bank.user.support;

import com.bank.shared.BankingSharedConfig;
import com.bank.user.BankingUserConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({BankingSharedConfig.class, BankingUserConfig.class})
public class UserModuleTestApplication {}
