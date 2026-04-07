package com.bank.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.account.api.AccountDto;
import com.bank.shared.dtos.ResponseDTO;
import com.bank.user.auth.api.RegisterResponse;
import com.bank.user.management.api.UpdateUserRequest;
import com.bank.user.management.api.UserResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("Application Integration Tests")
class AppIntegrationTest extends BaseIntegrationTest {

  @Nested
  @DisplayName("Auth + Account Flow")
  class AuthAndAccountFlow {

    @Test
    void shouldRegisterLoginAndLoadProvisionedAccount() throws Exception {
      registerUser(uniqueEmail);
      String token = loginAndGetToken(uniqueEmail);

      MvcResult result =
          mockMvc
              .perform(get("/accounts").header(HttpHeaders.AUTHORIZATION, bearer(token)))
              .andExpect(status().isOk())
              .andReturn();

      ResponseDTO<List<AccountDto>> response =
          objectMapper.readValue(
              result.getResponse().getContentAsString(), new TypeReference<>() {});

      assertThat(response.getRequestId()).isNotBlank();
      assertThat(response.getError()).isNull();
      assertThat(response.getData()).hasSize(1);
      assertThat(response.getData().getFirst().getAccountNumber()).isNotBlank();
    }
  }

  @Nested
  @DisplayName("Management Flow")
  class ManagementFlow {

    @Test
    void shouldListUpdateAndDeleteUsersThroughRealApplication() throws Exception {
      registerUser(testEmail);
      String token = loginAndGetToken(testEmail);

      MvcResult listBefore =
          mockMvc
              .perform(get("/users").header(HttpHeaders.AUTHORIZATION, bearer(token)))
              .andExpect(status().isOk())
              .andReturn();

      ResponseDTO<List<UserResponse>> beforeResponse =
          objectMapper.readValue(
              listBefore.getResponse().getContentAsString(), new TypeReference<>() {});
      assertThat(beforeResponse.getData()).hasSize(1);
      Long userId = beforeResponse.getData().getFirst().getId();

      UpdateUserRequest updateRequest =
          UpdateUserRequest.builder()
              .firstName("Jane")
              .lastName("Smith")
              .email(uniqueEmail)
              .build();

      MvcResult updateResult =
          mockMvc
              .perform(
                  put("/users/{userId}", userId)
                      .header(HttpHeaders.AUTHORIZATION, bearer(token))
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(updateRequest)))
              .andExpect(status().isOk())
              .andReturn();

      ResponseDTO<UserResponse> updateResponse =
          objectMapper.readValue(
              updateResult.getResponse().getContentAsString(), new TypeReference<>() {});
      assertThat(updateResponse.getData().getFirstName()).isEqualTo("Jane");
      assertThat(updateResponse.getData().getEmail()).isEqualTo(uniqueEmail);

      String updatedToken = loginAndGetToken(uniqueEmail);

      MvcResult deleteResult =
          mockMvc
              .perform(
                  delete("/users/{userId}", userId)
                      .header(HttpHeaders.AUTHORIZATION, bearer(updatedToken)))
              .andExpect(status().isOk())
              .andReturn();

      ResponseDTO<UserResponse> deleteResponse =
          objectMapper.readValue(
              deleteResult.getResponse().getContentAsString(), new TypeReference<>() {});
      assertThat(deleteResponse.getData().getId()).isEqualTo(userId);

      RegisterResponse recreated = registerUser(uniqueEmail);
      assertThat(recreated.getEmail()).isEqualTo(uniqueEmail);
    }
  }
}
