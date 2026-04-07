package com.bank.user.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.bank.shared.dtos.ResponseDTO;
import com.bank.user.auth.api.RegisterRequest;
import com.bank.user.management.api.UpdateUserRequest;
import com.bank.user.management.api.UserResponse;
import com.bank.user.shared.domain.UserBusinessException;
import com.bank.user.support.BaseApiTest;
import com.fasterxml.jackson.core.type.TypeReference;

@DisplayName("User Management API Tests")
class UserManagementControllerTest extends BaseApiTest {

    private Long firstUserId;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname(testFirstName)
                .lastname(testLastName)
                .email(testEmail)
                .password(testPassword)
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        MvcResult listResult = mockMvc.perform(get("/users").with(auth(testEmail)))
                .andExpect(status().isOk())
                .andReturn();

        ResponseDTO<List<UserResponse>> response = objectMapper.readValue(
                listResult.getResponse().getContentAsString(),
                new TypeReference<>() {});
        firstUserId = response.getData().getFirst().getId();
    }

    @Nested
    @DisplayName("GET /users")
    class ListUsersTests {

        @Test
        void shouldListRegisteredUsers() throws Exception {
            MvcResult result = mockMvc.perform(get("/users").with(auth(testEmail)))
                    .andExpect(status().isOk())
                    .andReturn();

            ResponseDTO<List<UserResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});

            assertThat(response.getRequestId()).isNotBlank();
            assertThat(response.getData()).hasSize(1);
            assertThat(response.getData().getFirst().getEmail()).isEqualTo(testEmail);
        }
    }

    @Nested
    @DisplayName("PUT /users/{userId}")
    class UpdateUserTests {

        @Test
        void shouldUpdateUserDetails() throws Exception {
            UpdateUserRequest request = UpdateUserRequest.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email(uniqueEmail)
                    .build();

            MvcResult result = mockMvc.perform(put("/users/{userId}", firstUserId)
                            .with(auth(testEmail))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            ResponseDTO<UserResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});

            assertThat(response.getData().getFirstName()).isEqualTo("Jane");
            assertThat(response.getData().getLastName()).isEqualTo("Smith");
            assertThat(response.getData().getEmail()).isEqualTo(uniqueEmail);
        }

        @Test
        void shouldRejectDuplicateEmail() throws Exception {
            RegisterRequest secondUser = RegisterRequest.builder()
                    .firstname("Second")
                    .lastname("User")
                    .email(uniqueEmail)
                    .password(testPassword)
                    .build();

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(secondUser)))
                    .andExpect(status().isCreated());

            UpdateUserRequest request = UpdateUserRequest.builder()
                    .firstName(testFirstName)
                    .lastName(testLastName)
                    .email(uniqueEmail)
                    .build();

            MvcResult result = mockMvc.perform(put("/users/{userId}", firstUserId)
                            .with(auth(testEmail))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertThat(readErrorCode(result)).isEqualTo(UserBusinessException.EMAIL_ALREADY_EXIST_CODE);
        }
    }

    @Nested
    @DisplayName("DELETE /users/{userId}")
    class DeleteUserTests {

        @Test
        void shouldDeleteUser() throws Exception {
            MvcResult deleteResult = mockMvc.perform(delete("/users/{userId}", firstUserId)
                            .with(auth(testEmail)))
                    .andExpect(status().isOk())
                    .andReturn();

            ResponseDTO<UserResponse> deleteResponse = objectMapper.readValue(
                    deleteResult.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(deleteResponse.getRequestId()).isNotBlank();
            assertThat(deleteResponse.getData().getId()).isEqualTo(firstUserId);

            MvcResult listResult = mockMvc.perform(get("/users").with(auth(testEmail)))
                    .andExpect(status().isOk())
                    .andReturn();
            ResponseDTO<List<UserResponse>> listResponse = objectMapper.readValue(
                    listResult.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(listResponse.getData()).isEmpty();
        }

        @Test
        void shouldFailWhenUserDoesNotExist() throws Exception {
            MvcResult result = mockMvc.perform(delete("/users/{userId}", 9999L)
                            .with(auth(testEmail)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertThat(readErrorCode(result)).isEqualTo(UserBusinessException.USER_NOT_FOUND_CODE);
        }
    }
}
