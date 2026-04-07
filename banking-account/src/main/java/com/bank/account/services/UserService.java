package com.bank.account.services;

import com.bank.account.dtos.LoginRequest;
import com.bank.account.dtos.LoginResponse;
import com.bank.account.dtos.RegisterRequest;
import com.bank.account.dtos.RegisterResponse;
import com.bank.account.entities.User;

public interface UserService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    User getCurrentUser();
    User getUserById(Long userId);
}