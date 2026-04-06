package com.banking.account.controllers;

import com.banking.account.dtos.AuthRequest;
import com.banking.account.dtos.AuthResponse;
import com.banking.shared.dtos.ResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
public interface AuthController {

    @PostMapping("/register")
    ResponseEntity<ResponseDTO<AuthResponse>> register(@RequestBody AuthRequest request);

    @PostMapping("/login")
    ResponseEntity<ResponseDTO<AuthResponse>> login(@RequestBody AuthRequest request);
}