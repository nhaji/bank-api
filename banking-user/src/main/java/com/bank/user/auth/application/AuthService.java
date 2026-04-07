package com.bank.user.auth.application;

import com.bank.user.auth.api.LoginRequest;
import com.bank.user.auth.api.LoginResponse;
import com.bank.user.auth.api.RegisterRequest;
import com.bank.user.auth.api.RegisterResponse;

public interface AuthService {
  RegisterResponse register(RegisterRequest request);

  LoginResponse login(LoginRequest request);
}
