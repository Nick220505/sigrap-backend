package com.sigrap.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = BaseTestConfiguration.class
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@ExtendWith(SpringExtension.class)
public abstract class BaseIntegrationTest {

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.main.allow-bean-definition-overriding", () -> "true");
    registry.add("spring.main.allow-circular-references", () -> "false");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    registry.add("spring.jpa.show-sql", () -> "false");
  }

  static {
    System.setProperty("spring.profiles.active", "test");
  }
}
