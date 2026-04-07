package com.bank.account.controllers;

import com.bank.account.dtos.LoginRequest;
import com.bank.account.dtos.LoginResponse;
import com.bank.account.dtos.RegisterRequest;
import com.bank.account.dtos.RegisterResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
public interface UserController {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    RegisterResponse register(@RequestBody RegisterRequest request);

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    LoginResponse login(@RequestBody LoginRequest request);
}
