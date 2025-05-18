package com.sigrap.payment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Payment} entity.
 *
 * <p>This repository provides the mechanism for data access operations on {@code Payment} entities,
 * including standard CRUD operations and the ability to define custom query methods.
 * It extends {@link JpaRepository} for general JPA functionality and
 * {@link JpaSpecificationExecutor} for criteria-based queries.</p>
 *
 * <p>Example custom queries (if needed later):
 * <ul>
 *   <li>{@code List<Payment> findBySupplierId(Integer supplierId);}</li>
 *   <li>{@code List<Payment> findByDueDateBeforeAndStatusNot(LocalDate date, PaymentStatus status);}</li>
 * </ul>
 * </p>
 *
 * @see Payment
 * @see JpaRepository
 * @see JpaSpecificationExecutor
 */
@Repository
public interface PaymentRepository
  extends JpaRepository<Payment, Integer>, JpaSpecificationExecutor<Payment> {
  /**
   * Finds all payments associated with a specific supplier ID.
   *
   * @param supplierId The ID of the supplier.
   * @return A list of {@link Payment} entities for the given supplier.
   */
  List<Payment> findBySupplierId(Long supplierId);
}
