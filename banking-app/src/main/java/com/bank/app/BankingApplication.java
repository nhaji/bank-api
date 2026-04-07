package com.bank.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.bank.account.BankingAccountConfig;
import com.bank.shared.BankingSharedConfig;
import com.bank.user.BankingUserConfig;

@SpringBootApplication
@Import({ BankingSharedConfig.class, BankingAccountConfig.class, BankingUserConfig.class })
public class BankingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}
