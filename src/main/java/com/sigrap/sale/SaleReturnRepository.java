package com.sigrap.sale;

import com.sigrap.customer.Customer;
import com.sigrap.user.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SaleReturn entities.
 * Provides methods to interact with the sales returns data in the database.
 */
@Repository
public interface SaleReturnRepository
  extends JpaRepository<SaleReturn, Integer> {
  /**
   * Find sales returns by the original sale.
   *
   * @param originalSale The original sale
   * @return List of sales returns related to the original sale
   */
  List<SaleReturn> findByOriginalSale(Sale originalSale);

  /**
   * Find sales returns by employee who processed them.
   *
   * @param employee The employee who processed the sales returns
   * @return List of sales returns processed by the given employee
   */
  List<SaleReturn> findByEmployee(User employee);

  /**
   * Find sales returns by customer who made them.
   *
   * @param customer The customer who made the sales returns
   * @return List of sales returns made by the given customer
   */
  List<SaleReturn> findByCustomer(Customer customer);

  /**
   * Find sales returns created between two dates.
   *
   * @param startDate The start date (inclusive)
   * @param endDate The end date (inclusive)
   * @return List of sales returns created within the given date range
   */
  List<SaleReturn> findByCreatedAtBetween(
    LocalDateTime startDate,
    LocalDateTime endDate
  );
}
