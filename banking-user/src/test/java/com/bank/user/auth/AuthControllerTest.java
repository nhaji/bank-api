package com.bank.user.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.bank.shared.dtos.ResponseDTO;
import com.bank.user.auth.api.LoginRequest;
import com.bank.user.auth.api.LoginResponse;
import com.bank.user.auth.api.RegisterRequest;
import com.bank.user.auth.api.RegisterResponse;
import com.bank.user.shared.domain.UserBusinessException;
import com.bank.user.support.BaseApiTest;
import com.fasterxml.jackson.core.type.TypeReference;

@DisplayName("Authentication API Tests")
class AuthControllerTest extends BaseApiTest {

    @Value("${test.user.unique-email}")
    private String uniqueEmail;

    @Nested
    @DisplayName("POST /auth/register")
    class RegisterTests {

        @Test
        void shouldRegisterNewUserAndProvisionInitialAccount() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .firstname(testFirstName)
                    .lastname(testLastName)
                    .email(uniqueEmail)
                    .password(testPassword)
                    .build();

            MvcResult result = mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            ResponseDTO<RegisterResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});

            assertThat(response.getData().getToken()).isNotBlank();
            assertThat(response.getData().getEmail()).isEqualTo(uniqueEmail);
            assertThat(response.getRequestId()).isNotBlank();
            verify(accountFacade).createInitialAccount(uniqueEmail);
        }

        @Test
        void shouldFailWhenEmailAlreadyExists() throws Exception {
            RegisterRequest initialRequest = RegisterRequest.builder()
                    .firstname(testFirstName)
                    .lastname(testLastName)
                    .email(testEmail)
                    .password(testPassword)
                    .build();

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(initialRequest)))
                    .andExpect(status().isCreated());

            RegisterRequest duplicate = RegisterRequest.builder()
                    .firstname(testFirstName)
                    .lastname(testLastName)
                    .email(testEmail)
                    .password("different")
                    .build();

            MvcResult result = mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicate)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
            assertThat(readErrorCode(result)).isEqualTo(UserBusinessException.EMAIL_ALREADY_EXIST_CODE);
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        void shouldLoginWithValidCredentials() throws Exception {
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .firstname(testFirstName)
                    .lastname(testLastName)
                    .email(testEmail)
                    .password(testPassword)
                    .build();

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            LoginRequest loginRequest = LoginRequest.builder()
                    .email(testEmail)
                    .password(testPassword)
                    .build();

            MvcResult result = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            ResponseDTO<LoginResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});

            assertThat(response.getData().getToken()).isNotBlank();
            assertThat(response.getData().getEmail()).isEqualTo(testEmail);
        }
    }
}
