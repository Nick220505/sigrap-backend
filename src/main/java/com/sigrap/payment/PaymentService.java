package com.sigrap.payment;

import com.sigrap.supplier.PurchaseOrder;
import com.sigrap.supplier.PurchaseOrderRepository;
import com.sigrap.supplier.Supplier;
import com.sigrap.supplier.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing payments.
 *
 * <p>This service provides business logic for payment operations, including creating, retrieving,
 * updating, and deleting payments. It interacts with the {@link PaymentRepository},
 * {@link SupplierRepository}, and {@link PurchaseOrderRepository} to manage payment data
 * and its relationships with suppliers and purchase orders.</p>
 *
 * <p>Key functionalities:
 * <ul>
 *   <li>Retrieving all payments or a specific payment by ID.</li>
 *   <li>Creating new payments, associating them with suppliers and optionally purchase orders.</li>
 *   <li>Updating existing payment details.</li>
 *   <li>Deleting payments.</li>
 *   <li>Finding payments by various criteria such as supplier.</li>
 * </ul>
 * </p>
 *
 * @see Payment
 * @see PaymentInfo
 * @see PaymentData
 * @see PaymentMapper
 * @see PaymentRepository
 * @see SupplierRepository
 * @see PurchaseOrderRepository
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;
  private final SupplierRepository supplierRepository;
  private final PurchaseOrderRepository purchaseOrderRepository;

  /**
   * Retrieves all payments and converts them to {@link PaymentInfo} DTOs.
   *
   * @return A list of {@link PaymentInfo} objects representing all payments.
   */
  @Transactional(readOnly = true)
  public List<PaymentInfo> findAll() {
    List<Payment> payments = paymentRepository.findAll();
    return paymentMapper.toInfoList(payments);
  }

  /**
   * Retrieves a specific payment by its ID.
   *
   * @param id The ID of the payment to retrieve.
   * @return The {@link PaymentInfo} DTO for the found payment.
   * @throws EntityNotFoundException if no payment with the given ID is found.
   */
  @Transactional(readOnly = true)
  public PaymentInfo findById(Integer id) {
    Payment payment = paymentRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Payment not found with id: " + id)
      );
    return paymentMapper.toInfo(payment);
  }

  /**
   * Creates a new payment based on the provided {@link PaymentData}.
   *
   * <p>This method resolves the {@link Supplier} and, if provided, the {@link PurchaseOrder}
   * based on their IDs in the {@code paymentData}. It then saves the new payment entity.</p>
   *
   * @param paymentData The DTO containing the data for the new payment.
   * @return The {@link PaymentInfo} DTO of the newly created payment.
   * @throws EntityNotFoundException if the specified supplier or purchase order is not found.
   */
  @Transactional
  public PaymentInfo create(PaymentData paymentData) {
    Supplier supplier = supplierRepository
      .findById(paymentData.getSupplierId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Supplier not found with id: " + paymentData.getSupplierId()
        )
      );

    Payment payment = paymentMapper.toEntity(paymentData);
    payment.setSupplier(supplier);

    if (paymentData.getPurchaseOrderId() != null) {
      PurchaseOrder purchaseOrder = purchaseOrderRepository
        .findById(paymentData.getPurchaseOrderId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "PurchaseOrder not found with id: " +
            paymentData.getPurchaseOrderId()
          )
        );
      payment.setPurchaseOrder(purchaseOrder);
    }

    Payment savedPayment = paymentRepository.save(payment);
    return paymentMapper.toInfo(savedPayment);
  }

  /**
   * Updates an existing payment with the given ID using data from {@link PaymentData}.
   *
   * <p>This method first retrieves the existing payment. If the supplier ID or purchase order ID
   * in {@code paymentData} is different from the existing one, it fetches and updates these associations.</p>
   *
   * @param id The ID of the payment to update.
   * @param paymentData The DTO containing the updated payment data.
   * @return The {@link PaymentInfo} DTO of the updated payment.
   * @throws EntityNotFoundException if the payment, supplier, or purchase order is not found.
   */
  @Transactional
  public PaymentInfo update(Integer id, PaymentData paymentData) {
    Payment existingPayment = paymentRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Payment not found with id: " + id)
      );

    if (
      paymentData.getSupplierId() != null &&
      !paymentData.getSupplierId().equals(existingPayment.getSupplier().getId())
    ) {
      Supplier supplier = supplierRepository
        .findById(paymentData.getSupplierId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "Supplier not found with id: " + paymentData.getSupplierId()
          )
        );
      existingPayment.setSupplier(supplier);
    }

    if (paymentData.getPurchaseOrderId() != null) {
      if (
        existingPayment.getPurchaseOrder() == null ||
        !paymentData
          .getPurchaseOrderId()
          .equals(existingPayment.getPurchaseOrder().getId())
      ) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository
          .findById(paymentData.getPurchaseOrderId())
          .orElseThrow(() ->
            new EntityNotFoundException(
              "PurchaseOrder not found with id: " +
              paymentData.getPurchaseOrderId()
            )
          );
        existingPayment.setPurchaseOrder(purchaseOrder);
      }
    } else {
      existingPayment.setPurchaseOrder(null);
    }

    paymentMapper.updateEntity(paymentData, existingPayment);
    Payment updatedPayment = paymentRepository.save(existingPayment);
    return paymentMapper.toInfo(updatedPayment);
  }

  /**
   * Deletes a payment by its ID.
   *
   * @param id The ID of the payment to delete.
   * @throws EntityNotFoundException if no payment with the given ID is found.
   */
  @Transactional
  public void delete(Integer id) {
    if (!paymentRepository.existsById(id)) {
      throw new EntityNotFoundException("Payment not found with id: " + id);
    }
    paymentRepository.deleteById(id);
  }

  /**
   * Deletes multiple payments by their IDs.
   * Ensures that all specified payments exist before attempting deletion.
   *
   * @param ids A list of IDs for the payments to be deleted.
   * @throws EntityNotFoundException if any payment with the given IDs is not found.
   */
  @Transactional
  public void deleteAllById(List<Integer> ids) {
    ids.forEach(id -> {
      if (!paymentRepository.existsById(id)) {
        throw new EntityNotFoundException(
          "Payment with id " + id + " not found"
        );
      }
    });
    paymentRepository.deleteAllById(ids);
  }

  /**
   * Retrieves all payments for a specific supplier.
   *
   * @param supplierId The ID of the supplier.
   * @return A list of {@link PaymentInfo} DTOs for the given supplier.
   */
  @Transactional(readOnly = true)
  public List<PaymentInfo> findBySupplier(Long supplierId) {
    List<Payment> payments = paymentRepository.findBySupplierId(supplierId);
    return paymentMapper.toInfoList(payments);
  }
}
