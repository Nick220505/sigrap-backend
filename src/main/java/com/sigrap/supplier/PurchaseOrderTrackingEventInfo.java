package com.sigrap.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing purchase order tracking event information.
 * Used for returning tracking event data in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
  description = "Response object containing purchase order tracking event information"
)
public class PurchaseOrderTrackingEventInfo {

  @Schema(
    description = "Unique identifier of the tracking event",
    example = "1"
  )
  private Integer id;

  @Schema(
    description = "Timestamp when the event occurred",
    example = "2023-05-15T10:30:00"
  )
  private LocalDateTime eventTimestamp;

  @Schema(description = "The status or type of the event", example = "Shipped")
  private String status;

  @Schema(
    description = "Detailed description of the event",
    example = "Package left the warehouse."
  )
  private String description;

  @Schema(
    description = "Location where the event occurred, if applicable",
    example = "Lima Warehouse"
  )
  private String location;

  @Schema(
    description = "Additional notes or comments related to this tracking event",
    example = "Handled by John Doe."
  )
  private String notes;
}
