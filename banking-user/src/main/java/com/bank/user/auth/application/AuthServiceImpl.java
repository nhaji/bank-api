package com.bank.user.auth.application;

import com.bank.account.application.AccountFacade;
import com.bank.shared.exceptions.BusinessException;
import com.bank.shared.security.JwtTokenProvider;
import com.bank.user.auth.api.LoginRequest;
import com.bank.user.auth.api.LoginResponse;
import com.bank.user.auth.api.RegisterRequest;
import com.bank.user.auth.api.RegisterResponse;
import com.bank.user.shared.domain.User;
import com.bank.user.shared.domain.UserBusinessException;
import com.bank.user.shared.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;
  private final AccountFacade accountFacade;

  @Override
  @Transactional
  public RegisterResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException(
          UserBusinessException.EMAIL_ALREADY_EXIST_CODE,
          UserBusinessException.EMAIL_ALREADY_EXIST_MSG);
    }

    User user =
        User.builder()
            .firstName(request.getFirstname())
            .lastName(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();
    User savedUser = userRepository.save(user);

    accountFacade.createInitialAccount(savedUser.getEmail());

    UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
    String token = tokenProvider.generateToken(userDetails);

    return RegisterResponse.builder().token(token).email(savedUser.getEmail()).build();
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String token = tokenProvider.generateToken(userDetails);

    return LoginResponse.builder().token(token).email(userDetails.getUsername()).build();
  }
}
