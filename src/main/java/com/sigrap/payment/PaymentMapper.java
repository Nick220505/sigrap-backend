package com.sigrap.payment;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between {@link Payment} entities and their DTOs
 * ({@link PaymentInfo}, {@link PaymentData}).
 *
 * <p>This mapper uses MapStruct to generate the implementation for converting payment objects.
 * It handles mapping of individual fields and also provides methods for mapping lists of objects.</p>
 *
 * <p>Key mappings include:
 * <ul>
 *   <li>Entity to Info DTO: Maps all relevant fields, including fetching supplier name and purchase order number.</li>
 *   <li>Data DTO to Entity: Maps input data to a new or existing entity, typically used for creation or updates.</li>
 * </ul>
 * </p>
 *
 * @see Payment
 * @see PaymentInfo
 * @see PaymentData
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {
  /**
   * Converts a {@link Payment} entity to a {@link PaymentInfo} DTO.
   *
   * @param payment The {@link Payment} entity to convert.
   * @return The corresponding {@link PaymentInfo} DTO.
   */
  @Mapping(
    target = "purchaseOrderId",
    expression = "java(payment.getPurchaseOrder() != null ? payment.getPurchaseOrder().getId() : null)"
  )
  @Mapping(
    target = "purchaseOrderNumber",
    expression = "java(payment.getPurchaseOrder() != null ? payment.getPurchaseOrder().getOrderNumber() : null)"
  )
  @Mapping(
    target = "supplierId",
    expression = "java(payment.getSupplier() != null ? payment.getSupplier().getId() : null)"
  )
  @Mapping(
    target = "supplierName",
    expression = "java(payment.getSupplier() != null ? payment.getSupplier().getName() : null)"
  )
  PaymentInfo toInfo(Payment payment);

  /**
   * Converts a list of {@link Payment} entities to a list of {@link PaymentInfo} DTOs.
   *
   * @param payments The list of {@link Payment} entities.
   * @return A list of {@link PaymentInfo} DTOs.
   */
  List<PaymentInfo> toInfoList(List<Payment> payments);

  /**
   * Converts a {@link PaymentData} DTO to a {@link Payment} entity.
   *
   * <p>This method is typically used when creating a new payment. The {@code supplier}
   * and {@code purchaseOrder} fields in the resulting {@link Payment} entity will be null
   * and should be set separately in the service layer by fetching them based on the IDs
   * provided in {@code PaymentData}.</p>
   *
   * @param paymentData The {@link PaymentData} DTO.
   * @return The corresponding {@link Payment} entity.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "supplier", ignore = true)
  @Mapping(target = "purchaseOrder", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Payment toEntity(PaymentData paymentData);

  /**
   * Updates an existing {@link Payment} entity with data from a {@link PaymentData} DTO.
   *
   * <p>Similar to {@code toEntity}, the {@code supplier} and {@code purchaseOrder} relationships
   * should be handled in the service layer if they are updatable via IDs in {@code PaymentData}.</p>
   *
   * @param paymentData The {@link PaymentData} DTO containing updated information.
   * @param payment The {@link Payment} entity to update.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "supplier", ignore = true)
  @Mapping(target = "purchaseOrder", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(PaymentData paymentData, @MappingTarget Payment payment);
}
