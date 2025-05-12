package com.sigrap.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing notification preferences.
 * Provides endpoints for CRUD operations on notification preferences.
 */
@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
@Tag(
  name = "Notification Preferences",
  description = "Notification preference management API"
)
public class NotificationPreferenceController {

  private final NotificationPreferenceService notificationPreferenceService;

  /**
   * Retrieves all notification preferences.
   *
   * @return List of all notification preferences
   */
  @GetMapping
  @Operation(
    summary = "Get all notification preferences",
    description = "Retrieves a list of all notification preferences"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Notification preferences retrieved successfully"
  )
  public List<NotificationPreferenceInfo> findAll() {
    return notificationPreferenceService.findAll();
  }

  /**
   * Retrieves a notification preference by its ID.
   *
   * @param id The ID of the notification preference to retrieve
   * @return The notification preference information
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get notification preference by ID",
    description = "Retrieves a notification preference by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "Preference found"),
      @ApiResponse(
        responseCode = "404",
        description = "Preference not found",
        content = @Content
      ),
    }
  )
  public NotificationPreferenceInfo findById(
    @Parameter(
      description = "ID of the notification preference to retrieve"
    ) @PathVariable Integer id
  ) {
    return notificationPreferenceService.findById(id);
  }

  /**
   * Retrieves notification preferences for a specific user.
   *
   * @param userId The ID of the user
   * @return List of notification preferences for the user
   */
  @GetMapping("/users/{userId}")
  @Operation(
    summary = "Get notification preferences by user",
    description = "Retrieves notification preferences for a specific user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "User preferences retrieved successfully"
  )
  public List<NotificationPreferenceInfo> findByUserId(
    @Parameter(
      description = "ID of the user to retrieve preferences for"
    ) @PathVariable Long userId
  ) {
    return notificationPreferenceService.findByUserId(userId);
  }

  /**
   * Creates a new notification preference.
   *
   * @param preferenceData The notification preference data to create
   * @return The created notification preference
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create a new notification preference",
    description = "Creates a new notification preference"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "201",
        description = "Preference created successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
    }
  )
  public NotificationPreferenceInfo create(
    @Parameter(
      description = "Notification preference data to create",
      required = true
    ) @Valid @RequestBody NotificationPreferenceData preferenceData
  ) {
    return notificationPreferenceService.create(preferenceData);
  }

  /**
   * Updates an existing notification preference.
   *
   * @param id The ID of the notification preference to update
   * @param preferenceData The updated notification preference data
   * @return The updated notification preference
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update a notification preference",
    description = "Updates an existing notification preference by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Preference updated successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Preference not found",
        content = @Content
      ),
    }
  )
  public NotificationPreferenceInfo update(
    @Parameter(
      description = "ID of the notification preference to update"
    ) @PathVariable Integer id,
    @Parameter(
      description = "Updated notification preference data",
      required = true
    ) @Valid @RequestBody NotificationPreferenceData preferenceData
  ) {
    return notificationPreferenceService.update(id, preferenceData);
  }

  /**
   * Deletes a notification preference by its ID.
   *
   * @param id The ID of the notification preference to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete a notification preference",
    description = "Deletes a notification preference by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "204",
        description = "Preference deleted successfully"
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Preference not found",
        content = @Content
      ),
    }
  )
  public void delete(
    @Parameter(
      description = "ID of the notification preference to delete"
    ) @PathVariable Integer id
  ) {
    notificationPreferenceService.delete(id);
  }

  /**
   * Deletes multiple notification preferences by their IDs.
   *
   * @param ids List of notification preference IDs to delete
   */
  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete multiple notification preferences",
    description = "Deletes multiple notification preferences by their IDs"
  )
  @ApiResponse(
    responseCode = "204",
    description = "Preferences deleted successfully"
  )
  public void deleteAllById(
    @Parameter(
      description = "List of notification preference IDs to delete",
      required = true
    ) @RequestBody List<Integer> ids
  ) {
    notificationPreferenceService.deleteAllById(ids);
  }
}
