package com.sigrap.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@Tag(name = "Status", description = "API status information endpoint")
public class StatusController {

  private final BuildProperties buildProperties;

  @GetMapping
  @Operation(summary = "Get API status", description = "Returns basic information about the API status and version")
  public Map<String, Object> getStatus() {
    Map<String, Object> status = new HashMap<>();
    status.put("status", "UP");
    status.put("timestamp", LocalDateTime.now());

    if (buildProperties != null) {
      status.put("application", buildProperties.getName());
      status.put("version", buildProperties.getVersion());
      status.put("buildTime", buildProperties.getTime());
    } else {
      status.put("application", "SIGRAP API");
      status.put("version", "Development");
    }

    return status;
  }
}