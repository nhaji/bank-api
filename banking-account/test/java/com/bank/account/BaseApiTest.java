package com.bank.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.bank.account.dtos.LoginRequest;
import com.bank.account.dtos.LoginResponse;
import com.bank.account.dtos.RegisterRequest;
import com.bank.shared.dtos.ResponseDTO;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseApiTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${test.user.email}")
    protected String testEmail;

    @Value("${test.user.password}")
    protected String testPassword;

    @Value("${test.user.first-name}")
    protected String testFirstName;

    @Value("${test.user.last-name}")
    protected String testLastName;

    protected String registerAndLogin() throws Exception {
        // Register
        RegisterRequest registerRequest = new RegisterRequest(testFirstName, testLastName, testEmail, testPassword);
        mockMvc.perform(post("/user/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login
        LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
        MvcResult result = mockMvc.perform(post("/user/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        ResponseDTO<LoginResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {});
        return response.getData().getToken();
    }
}