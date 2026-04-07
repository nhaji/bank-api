package com.bank.account.services.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.account.dtos.LoginRequest;
import com.bank.account.dtos.LoginResponse;
import com.bank.account.dtos.RegisterRequest;
import com.bank.account.dtos.RegisterResponse;
import com.bank.account.entities.User;
import com.bank.account.exceptions.UserBusinessException;
import com.bank.account.repositories.UserRepository;
import com.bank.account.services.AccountService;
import com.bank.account.services.UserService;
import com.bank.shared.exceptions.BusinessException;
import com.bank.shared.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final AccountService accountService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(UserBusinessException.EMAIL_ALREADY_EXIST_CODE, UserBusinessException.EMAIL_ALREADY_EXIST_MSG);
        }

        User user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        User savedUser = userRepository.save(user);

        // Create initial account with zero balance transaction
        accountService.createAccountForUser(savedUser.getId());

        // Generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = tokenProvider.generateToken(userDetails);

        return RegisterResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = tokenProvider.generateToken(userDetails);

        return LoginResponse.builder()
                .token(token)
                .email(userDetails.getUsername())
                .build();
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(UserBusinessException.USER_NOT_FOUND_CODE, UserBusinessException.USER_NOT_FOUND_MSG));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserBusinessException.USER_NOT_FOUND_CODE, UserBusinessException.USER_NOT_FOUND_MSG));
    }
}