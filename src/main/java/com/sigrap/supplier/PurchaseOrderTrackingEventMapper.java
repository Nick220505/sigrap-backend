package com.sigrap.supplier;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between PurchaseOrderTrackingEvent entity and its DTO representation.
 * Uses MapStruct for automatic implementation.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderTrackingEventMapper {
  /**
   * Converts a PurchaseOrderTrackingEvent entity to its info representation.
   *
   * @param event The PurchaseOrderTrackingEvent entity to convert.
   * @return PurchaseOrderTrackingEventInfo containing the tracking event data.
   */
  @Mapping(source = "id", target = "id")
  @Mapping(source = "eventTimestamp", target = "eventTimestamp")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "location", target = "location")
  @Mapping(source = "notes", target = "notes")
  PurchaseOrderTrackingEventInfo toInfo(PurchaseOrderTrackingEvent event);

  /**
   * Converts a list of PurchaseOrderTrackingEvent entities to a list of PurchaseOrderTrackingEventInfo DTOs.
   *
   * @param events The list of PurchaseOrderTrackingEvent entities to convert.
   * @return List of PurchaseOrderTrackingEventInfo DTOs.
   */
  List<PurchaseOrderTrackingEventInfo> toInfoList(
    List<PurchaseOrderTrackingEvent> events
  );
}
