package com.bank.account.controllers;

import com.bank.account.dtos.LoginRequest;
import com.bank.account.dtos.LoginResponse;
import com.bank.account.dtos.RegisterRequest;
import com.bank.account.dtos.RegisterResponse;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
public interface UserController {

    @PostMapping("/register")
    RegisterResponse register(@RequestBody RegisterRequest request);

    @PostMapping("/login")
    LoginResponse login(@RequestBody LoginRequest request);
}