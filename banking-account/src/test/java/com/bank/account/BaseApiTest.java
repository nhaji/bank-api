package com.bank.account;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = AccountModuleTestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseApiTest {

  protected static final String PRIMARY_USER = "user1@example.com";
  protected static final String SECONDARY_USER = "user2@example.com";

  @Autowired protected MockMvc mockMvc;

  @Autowired protected ObjectMapper objectMapper;

  @MockitoBean protected UserDetailsService userDetailsService;

  protected RequestPostProcessor auth(String email) {
    return user(email).roles("USER");
  }

  protected String readErrorCode(MvcResult result) throws Exception {
    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.path("error").path("code").asText();
  }
}
