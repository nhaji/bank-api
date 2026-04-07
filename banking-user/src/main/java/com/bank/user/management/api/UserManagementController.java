package com.bank.user.management.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bank.user.management.application.UserManagementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> listUsers() {
        return userManagementService.listUsers();
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
        return userManagementService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse deleteUser(@PathVariable Long userId) {
        return userManagementService.deleteUser(userId);
    }
}
