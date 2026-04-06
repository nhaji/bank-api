package com.banking.account;

import com.bank.account.dto.AuthRequest;
import com.bank.account.dto.AuthResponse;
import com.bank.shared.dto.ResponseDTO;
import com.banking.account.exceptions.AuthBusinessException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Authentication API Tests")
class AuthControllerTest extends BaseApiTest {

    @Value("${test.user.unique-email}")
    protected String uniqueEmail;

    @Nested
    @DisplayName("POST /auth/register")
    class RegisterTests {

        @Test
        void shouldRegisterNewUserAndCreateInitialAccount() throws Exception {
            AuthRequest request = new AuthRequest(uniqueEmail, password);

            MvcResult result = mockMvc.perform(post("/auth/register")
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
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new AuthRequest(testEmail, testPassword))))
                    .andExpect(status().isCreated());

            // Duplicate registration
            AuthRequest duplicate = new AuthRequest(testEmail, "different");
            MvcResult result = mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicate)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ResponseDTO<Void> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(response.getError().getCode()).isEqualTo(AuthBusinessException.EMAIL_ALREADY_EXIST_CODE);
            assertThat(response.getError().getMessage()).contains(AuthBusinessException.EMAIL_ALREADY_EXIST_MSG);
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        void shouldLoginWithValidCredentials() throws Exception {
            // Register first
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new AuthRequest(testEmail, testPassword))))
                    .andExpect(status().isCreated());

            // Login
            AuthRequest loginRequest = new AuthRequest(testEmail, testPassword);
            MvcResult result = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            ResponseDTO<AuthResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {});
            assertThat(response.getData().getToken()).isNotBlank();
            assertThat(response.getData().getEmail()).isEqualTo(testEmail);
        }

        @Test
        void shouldRejectInvalidPassword() throws Exception {
            // Register
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new AuthRequest(testEmail, testPassword))))
                    .andExpect(status().isCreated());

            // Wrong password
            AuthRequest wrong = new AuthRequest(testEmail, "wrong");
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrong)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldRejectNonexistentEmail() throws Exception {
            AuthRequest request = new AuthRequest("nonexistent@example.com", "pass");
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }
}