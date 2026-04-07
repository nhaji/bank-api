package com.bank.user;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"com.bank.user"})
@EntityScan(basePackages = {"com.bank.user"})
@EnableJpaRepositories(basePackages = {"com.bank.user"})
public class BankingUserConfig {}
