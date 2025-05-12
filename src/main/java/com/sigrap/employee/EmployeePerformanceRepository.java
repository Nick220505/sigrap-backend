package com.sigrap.employee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for EmployeePerformance entity.
 * Provides database operations for performance records.
 *
 * <p>This repository includes:
 * <ul>
 *   <li>Basic CRUD operations</li>
 *   <li>Custom search methods</li>
 *   <li>Employee-based queries</li>
 *   <li>Date-based queries</li>
 *   <li>Performance metrics queries</li>
 * </ul></p>
 */
@Repository
public interface EmployeePerformanceRepository
  extends JpaRepository<EmployeePerformance, Long> {
  /**
   * Finds all performance records for a specific employee.
   *
   * @param employeeId ID of the employee
   * @return List of performance records for the employee
   */
  List<EmployeePerformance> findByEmployeeId(Long employeeId);

  /**
   * Finds all performance records for a specific employee between two dates.
   *
   * @param employeeId ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of performance records matching the criteria
   */
  List<EmployeePerformance> findByEmployeeIdAndPeriodStartBetween(
    Long employeeId,
    LocalDateTime startDate,
    LocalDateTime endDate
  );

  /**
   * Finds all performance records for a specific date.
   *
   * @param date Date to search for
   * @return List of performance records for the date
   */
  List<EmployeePerformance> findByPeriodStart(LocalDateTime date);

  /**
   * Calculates total sales amount for an employee between two dates.
   *
   * @param employeeId ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return Total sales amount
   */
  @Query(
    """
      SELECT COALESCE(SUM(p.salesTotal), 0)
      FROM EmployeePerformance p
      WHERE p.employee.id = :employeeId
      AND p.periodStart >= :startDate
      AND p.periodEnd <= :endDate
    """
  )
  BigDecimal calculateTotalSales(
    @Param("employeeId") Long employeeId,
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate
  );

  /**
   * Calculates average customer satisfaction for an employee between two dates.
   *
   * @param employeeId ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return Average customer satisfaction
   */
  @Query(
    "SELECT COALESCE(AVG(p.rating), 0) FROM EmployeePerformance p " +
    "WHERE p.employee.id = :employeeId " +
    "AND p.periodStart >= :startDate AND p.periodEnd <= :endDate"
  )
  BigDecimal calculateAverageCustomerSatisfaction(
    @Param("employeeId") Long employeeId,
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate
  );

  /**
   * Calculates total transaction count for an employee between two dates.
   *
   * @param employeeId ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return Total transaction count
   */
  @Query(
    "SELECT COALESCE(SUM(p.salesCount), 0) FROM EmployeePerformance p " +
    "WHERE p.employee.id = :employeeId " +
    "AND p.periodStart >= :startDate AND p.periodEnd <= :endDate"
  )
  Integer calculateTotalTransactions(
    @Param("employeeId") Long employeeId,
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate
  );

  /**
   * Finds top performers by sales amount between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param limit Maximum number of results to return
   * @return List of performance records for top performers
   */
  @Query(
    value = """
    SELECT p FROM EmployeePerformance p
    WHERE p.periodStart >= :startDate
    AND p.periodEnd <= :endDate
    ORDER BY p.salesTotal DESC, p.salesCount DESC
    LIMIT :limit
    """
  )
  List<EmployeePerformance> findTopPerformers(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate,
    @Param("limit") Integer limit
  );

  /**
   * Finds top performers by customer satisfaction between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param minRating Minimum rating to include
   * @return List of performance records for top performers
   */
  @Query(
    """
      SELECT p FROM EmployeePerformance p
      WHERE p.periodStart >= :startDate
      AND p.periodEnd <= :endDate
      AND p.rating >= :minRating
      ORDER BY p.rating DESC
    """
  )
  List<EmployeePerformance> findByPeriodAndMinimumRating(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate,
    @Param("minRating") Integer minRating
  );

  /**
   * Finds top performers by sales amount between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param limit Maximum number of results to return
   * @return List of performance records for top performers
   */
  @Query(
    value = """
    SELECT p FROM EmployeePerformance p
    WHERE p.periodStart >= :startDate
    AND p.periodEnd <= :endDate
    ORDER BY p.salesTotal DESC
    LIMIT :limit
    """
  )
  List<EmployeePerformance> findTopPerformersBySales(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate,
    @Param("limit") Integer limit
  );
}
