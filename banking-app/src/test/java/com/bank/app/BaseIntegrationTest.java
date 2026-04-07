package com.bank.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.shared.dtos.ResponseDTO;
import com.bank.user.auth.api.LoginRequest;
import com.bank.user.auth.api.LoginResponse;
import com.bank.user.auth.api.RegisterRequest;
import com.bank.user.auth.api.RegisterResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = BankingApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

  @Autowired protected MockMvc mockMvc;

  @Autowired protected ObjectMapper objectMapper;

  @Value("${test.user.email}")
  protected String testEmail;

  @Value("${test.user.unique-email}")
  protected String uniqueEmail;

  @Value("${test.user.password}")
  protected String testPassword;

  @Value("${test.user.first-name}")
  protected String testFirstName;

  @Value("${test.user.last-name}")
  protected String testLastName;

  protected RegisterResponse registerUser(String email) throws Exception {
    RegisterRequest request =
        RegisterRequest.builder()
            .firstname(testFirstName)
            .lastname(testLastName)
            .email(email)
            .password(testPassword)
            .build();

    MvcResult result =
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn();

    ResponseDTO<RegisterResponse> response =
        objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    return response.getData();
  }

  protected String loginAndGetToken(String email) throws Exception {
    LoginRequest request = LoginRequest.builder().email(email).password(testPassword).build();

    MvcResult result =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

    ResponseDTO<LoginResponse> response =
        objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    return response.getData().getToken();
  }

  protected String bearer(String token) {
    return "Bearer " + token;
  }
}
