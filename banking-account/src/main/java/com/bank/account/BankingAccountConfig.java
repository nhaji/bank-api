package com.bank.account;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"com.banking.account"})
@EntityScan(basePackages = {"com.banking.account.entities"})
@EnableJpaRepositories(basePackages = {"com.banking.account.repositories"})
public class BankingAccountConfig {
    
}
