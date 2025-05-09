package com.sigrap.common;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.boot.info.BuildProperties;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class StatusControllerTest {

  private MockMvc mockMvc;
  private BuildProperties buildProperties;

  @BeforeEach
  void setup() {
    buildProperties = mock(BuildProperties.class);
    StatusController controller = new StatusController(buildProperties);
    mockMvc = standaloneSetup(controller).build();
  }

  @Test
  void getStatus_shouldReturnStatusInfo_whenBuildPropertiesExist()
    throws Exception {
    String appName = "SIGRAP API";
    String version = "1.0.0";
    Instant buildTime = Instant.now();

    when(buildProperties.getName()).thenReturn(appName);
    when(buildProperties.getVersion()).thenReturn(version);
    when(buildProperties.getTime()).thenReturn(buildTime);

    mockMvc
      .perform(get("/api/status"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("UP"))
      .andExpect(jsonPath("$.timestamp").exists())
      .andExpect(jsonPath("$.application").value(appName))
      .andExpect(jsonPath("$.version").value(version))
      .andExpect(jsonPath("$.buildTime").exists());
  }

  @Test
  void getStatus_shouldReturnDefaultInfo_whenBuildPropertiesAreNull()
    throws Exception {
    StatusController controller = new StatusController(null);
    mockMvc = standaloneSetup(controller).build();

    mockMvc
      .perform(get("/api/status"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("UP"))
      .andExpect(jsonPath("$.timestamp").exists())
      .andExpect(jsonPath("$.application").value("SIGRAP API"))
      .andExpect(jsonPath("$.version").value("Development"))
      .andExpect(jsonPath("$.buildTime").doesNotExist());
  }
}
