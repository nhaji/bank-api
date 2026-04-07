package com.bank.user.management.application;

import com.bank.user.management.api.UpdateUserRequest;
import com.bank.user.management.api.UserResponse;
import java.util.List;

public interface UserManagementService {
  List<UserResponse> listUsers();

  UserResponse updateUser(Long userId, UpdateUserRequest request);

  UserResponse deleteUser(Long userId);
}
