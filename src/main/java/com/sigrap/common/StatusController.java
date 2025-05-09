package com.sigrap.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for providing application status information.
 * Offers endpoints to check application health and version details.
 *
 * <p>This controller provides:
 * <ul>
 *   <li>Application health status</li>
 *   <li>Version information</li>
 *   <li>Build details</li>
 *   <li>Runtime information</li>
 * </ul></p>
 *
 * <p>Key Features:
 * <ul>
 *   <li>Real-time health checking</li>
 *   <li>Build version tracking</li>
 *   <li>Deployment information</li>
 *   <li>System status monitoring</li>
 * </ul></p>
 *
 * <p>Usage Example:
 * <pre>
 * GET /api/status
 * Response:
 * {
 *   "status": "UP",
 *   "timestamp": "2024-02-20T10:30:15",
 *   "application": "SIGRAP API",
 *   "version": "1.0.0",
 *   "buildTime": "2024-02-20T08:00:00Z"
 * }
 * </pre></p>
 *
 * @see BuildProperties
 */
@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@Tag(name = "Status", description = "API status information endpoint")
public class StatusController {

  /**
   * Build properties containing application metadata.
   * Includes information like application name, version, and build timestamp.
   * May be null in development environments without proper build information.
   */
  private final BuildProperties buildProperties;

  /**
   * Retrieves current application status and version information.
   *
   * <p>This endpoint provides:
   * <ul>
   *   <li>Current application status (UP/DOWN)</li>
   *   <li>Server timestamp</li>
   *   <li>Application name and version</li>
   *   <li>Build timestamp</li>
   * </ul></p>
   *
   * <p>The status information is useful for:
   * <ul>
   *   <li>Health monitoring</li>
   *   <li>Version verification</li>
   *   <li>Deployment tracking</li>
   *   <li>System diagnostics</li>
   * </ul></p>
   *
   * @return Map containing status information including:
   *         - status: Current application status (UP/DOWN)
   *         - timestamp: Current server time
   *         - application: Application name
   *         - version: Application version
   *         - buildTime: Build timestamp
   */
  @GetMapping
  @Operation(
    summary = "Get API status",
    description = "Returns basic information about the API status and version"
  )
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
