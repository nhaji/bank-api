package com.bank.user.management.application;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.shared.exceptions.BusinessException;
import com.bank.user.management.api.UpdateUserRequest;
import com.bank.user.management.api.UserResponse;
import com.bank.user.shared.domain.User;
import com.bank.user.shared.domain.UserBusinessException;
import com.bank.user.shared.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        UserBusinessException.USER_NOT_FOUND_CODE,
                        UserBusinessException.USER_NOT_FOUND_MSG));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new BusinessException(
                    UserBusinessException.EMAIL_ALREADY_EXIST_CODE,
                    UserBusinessException.EMAIL_ALREADY_EXIST_MSG);
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        UserBusinessException.USER_NOT_FOUND_CODE,
                        UserBusinessException.USER_NOT_FOUND_MSG));
        UserResponse response = toResponse(user);
        userRepository.delete(user);
        return response;
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
