package com.bank.user.management.application;

import java.util.List;

import com.bank.user.management.api.UpdateUserRequest;
import com.bank.user.management.api.UserResponse;

public interface UserManagementService {
    List<UserResponse> listUsers();
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    UserResponse deleteUser(Long userId);
}
