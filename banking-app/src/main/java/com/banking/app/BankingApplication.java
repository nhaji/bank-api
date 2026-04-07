package com.banking.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.bank.account.BankingAccountConfig;
import com.bank.shared.BankingSharedConfig;

@SpringBootApplication
@Import({ BankingSharedConfig.class, BankingAccountConfig.class })
public class BankingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}