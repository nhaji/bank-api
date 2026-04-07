package com.bank.account.controllers.impl;

import com.bank.account.controllers.UserController;
import com.bank.account.dtos.LoginRequest;
import com.bank.account.dtos.LoginResponse;
import com.bank.account.dtos.RegisterRequest;
import com.bank.account.dtos.RegisterResponse;
import com.bank.account.services.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        return userService.register(request);

    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return userService.login(request);
    }

}