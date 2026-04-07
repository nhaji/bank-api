package com.bank.account;

import com.bank.shared.dtos.ResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.account.dtos.LoginRequest;
import com.bank.account.dtos.RegisterRequest;
import com.bank.account.exceptions.UserBusinessException;

@DisplayName("Authentication API Tests")
class UserControllerTest extends BaseApiTest {

    @Value("${test.user.unique-email}")
    protected String uniqueEmail;

    @Nested
    @DisplayName("POST /user/register")
    class RegisterTests {

        @Test
        void shouldRegisterNewUserAndCreateInitialAccount() throws Exception {
            RegisterRequest request = new RegisterRequest(uniqueEmail, password);

            MvcResult result = mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            ResponseDTO<AuthResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});

            assertThat(response.getData().getToken()).isNotBlank();
            assertThat(response.getData().getEmail()).isEqualTo(uniqueEmail);
            assertThat(response.getRequestId()).isNotBlank();
        }

        @Test
        void shouldFailWhenEmailAlreadyExists() throws Exception {
            // First registration
            mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RegisterRequest(testEmail, testPassword))))
                    .andExpect(status().isCreated());

            // Duplicate registration
            RegisterRequest duplicate = new RegisterRequest(testEmail, "different");
            MvcResult result = mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicate)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ResponseDTO<Void> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(response.getError().getCode()).isEqualTo(UserBusinessException.EMAIL_ALREADY_EXIST_CODE);
            assertThat(response.getError().getMessage()).contains(UserBusinessException.EMAIL_ALREADY_EXIST_MSG);
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        void shouldLoginWithValidCredentials() throws Exception {
            // Register first
            mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RegisterRequest(testEmail, testPassword))))
                    .andExpect(status().isCreated());

            // Login
            LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
            MvcResult result = mockMvc.perform(post("/user/login")
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

        @Test
        void shouldRejectInvalidPassword() throws Exception {
            // Register
            mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RegisterRequest(testEmail, testPassword))))
                    .andExpect(status().isCreated());

            // Wrong password
            LoginRequest wrong = new LoginRequest(testEmail, "wrong");
            mockMvc.perform(post("/user/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrong)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldRejectNonexistentEmail() throws Exception {
            LoginRequest request = new LoginRequest("nonexistent@example.com", "pass");
            mockMvc.perform(post("/user/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }
}