package com.bank.account;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"com.bank.account"})
@EntityScan(basePackages = {"com.bank.account.entities"})
@EnableJpaRepositories(basePackages = {"com.bank.account.repositories"})
public class BankingAccountConfig {
    
}
