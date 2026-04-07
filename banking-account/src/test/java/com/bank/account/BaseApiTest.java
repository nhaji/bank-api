package com.bank.account;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = AccountModuleTestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseApiTest {

  protected static final String PRIMARY_USER = "john@example.com";
  protected static final String SECONDARY_USER = "user1@example.com";

  @Autowired protected org.springframework.test.web.servlet.MockMvc mockMvc;

  @Autowired protected ObjectMapper objectMapper;

  @MockBean protected UserDetailsService userDetailsService;

  protected RequestPostProcessor auth(String email) {
    return user(email).roles("USER");
  }

  protected String readErrorCode(MvcResult result) throws Exception {
    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.path("error").path("code").asText();
  }
}
