/**
 * This package contains all classes related to payment management in the Sigrap application.
 *
 * <p>The payment module handles the creation, tracking, and processing of payments made to suppliers.
 * It includes entities for payments, DTOs for data transfer, repositories for database interaction,
 * services for business logic, and controllers for exposing RESTful APIs.</p>
 *
 * <h2>Key Components:</h2>
 * <ul>
 *   <li>{@link com.sigrap.payment.Payment}: The main entity representing a payment.</li>
 *   <li>{@link com.sigrap.payment.PaymentStatus}: Enum defining the possible statuses of a payment.</li>
 *   <li>{@link com.sigrap.payment.PaymentInfo}: DTO for sending payment data to clients.</li>
 *   <li>{@link com.sigrap.payment.PaymentData}: DTO for receiving payment data from clients.</li>
 *   <li>{@link com.sigrap.payment.PaymentMapper}: MapStruct mapper for converting between entities and DTOs.</li>
 *   <li>{@link com.sigrap.payment.PaymentRepository}: Spring Data JPA repository for payment entities.</li>
 *   <li>{@link com.sigrap.payment.PaymentService}: Service layer handling business logic for payments.</li>
 *   <li>{@link com.sigrap.payment.PaymentController}: REST controller exposing payment management APIs.</li>
 * </ul>
 *
 * <p>This package interacts closely with the {@code com.sigrap.supplier} package, particularly
 * with {@link com.sigrap.supplier.Supplier} and {@link com.sigrap.supplier.PurchaseOrder} entities.</p>
 *
 * @since 1.0
 * @see com.sigrap.payment.Payment
 * @see com.sigrap.payment.PaymentController
 */
package com.sigrap.payment;
