package com.sigrap.sale;

import com.sigrap.customer.Customer;
import com.sigrap.user.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Sale entities.
 * Provides methods to interact with the sales data in the database.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Integer> {
  /**
   * Find sales by status.
   *
   * @param status The status to search for
   * @return List of sales with the given status
   */
  List<Sale> findByStatus(SaleStatus status);

  /**
   * Find sales by employee.
   *
   * @param employee The employee who processed the sales
   * @return List of sales processed by the given employee
   */
  List<Sale> findByEmployee(User employee);

  /**
   * Find sales by customer.
   *
   * @param customer The customer who made the purchases
   * @return List of sales made by the given customer
   */
  List<Sale> findByCustomer(Customer customer);

  /**
   * Find sales created between two dates.
   *
   * @param startDate The start date (inclusive)
   * @param endDate The end date (inclusive)
   * @return List of sales created within the given date range
   */
  List<Sale> findByCreatedAtBetween(
    LocalDateTime startDate,
    LocalDateTime endDate
  );

  /**
   * Find sales by payment method.
   *
   * @param paymentMethod The payment method to search for
   * @return List of sales with the given payment method
   */
  List<Sale> findByPaymentMethod(PaymentMethod paymentMethod);
}
