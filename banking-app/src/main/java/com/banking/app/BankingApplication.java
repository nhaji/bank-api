package com.banking.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.banking.shared.BankingSharedConfig;
import com.banking.account.BankingAccountConfig;

@SpringBootApplication
@Import({ BankingSharedConfig.class, BankingAccountConfig.class })
public class BankingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}