package com.sigrap.config;

import com.sigrap.SigrapBackendApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = SigrapBackendApplication.class
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import({ TestSecurityConfig.class })
public abstract class BaseIntegrationTest {
  static {
    System.setProperty("spring.profiles.active", "test");
  }
}
