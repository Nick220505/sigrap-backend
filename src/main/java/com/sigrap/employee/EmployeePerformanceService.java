package com.sigrap.employee;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for employee performance management operations.
 * Handles business logic for performance-related functionality.
 *
 * <p>This service provides:
 * <ul>
 *   <li>Performance CRUD operations</li>
 *   <li>Performance metrics calculation</li>
 *   <li>Performance reporting</li>
 *   <li>Top performers identification</li>
 * </ul></p>
 */
@Service
@RequiredArgsConstructor
public class EmployeePerformanceService {

  private final EmployeePerformanceRepository performanceRepository;
  private final EmployeeRepository employeeRepository;
  private final EmployeePerformanceMapper performanceMapper;

  /**
   * Retrieves all performance records.
   *
   * @return List of EmployeePerformanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeePerformanceInfo> findAll() {
    List<EmployeePerformance> performances = performanceRepository.findAll();
    return performanceMapper.toInfoList(performances);
  }

  /**
   * Retrieves a performance record by its ID.
   *
   * @param id The ID of the performance record to retrieve
   * @return EmployeePerformanceInfo containing the performance's information
   * @throws EntityNotFoundException if the performance record is not found
   */
  @Transactional(readOnly = true)
  public EmployeePerformanceInfo findById(Long id) {
    EmployeePerformance performance = performanceRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee Performance not found: " + id)
      );
    return performanceMapper.toInfo(performance);
  }

  /**
   * Finds all performance records for a specific employee.
   *
   * @param employeeId The ID of the employee
   * @return List of EmployeePerformanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeePerformanceInfo> findByEmployeeId(Long employeeId) {
    List<EmployeePerformance> performances =
      performanceRepository.findByEmployeeId(employeeId);
    return performanceMapper.toInfoList(performances);
  }

  /**
   * Finds all performance records for a specific date.
   *
   * @param date The date to search for
   * @return List of EmployeePerformanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeePerformanceInfo> findByDate(LocalDateTime date) {
    List<EmployeePerformance> performances =
      performanceRepository.findByPeriodStart(date);
    return performanceMapper.toInfoList(performances);
  }

  /**
   * Generates a performance report for a specific employee between two dates.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of EmployeePerformanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeePerformanceInfo> generatePerformanceReport(
    Long employeeId,
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    List<EmployeePerformance> performances =
      performanceRepository.findByEmployeeIdAndPeriodStartBetween(
        employeeId,
        startDate,
        endDate
      );
    return performanceMapper.toInfoList(performances);
  }

  /**
   * Calculates total sales for an employee between two dates.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return Total sales amount
   */
  @Transactional(readOnly = true)
  public BigDecimal calculateTotalSales(
    Long employeeId,
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return performanceRepository.calculateTotalSales(
      employeeId,
      startDate,
      endDate
    );
  }

  /**
   * Calculates average customer satisfaction for an employee between two dates.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return Average customer satisfaction
   */
  @Transactional(readOnly = true)
  public BigDecimal calculateAverageCustomerSatisfaction(
    Long employeeId,
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return performanceRepository.calculateAverageCustomerSatisfaction(
      employeeId,
      startDate,
      endDate
    );
  }

  /**
   * Calculates total transactions for an employee between two dates.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return Total transaction count
   */
  @Transactional(readOnly = true)
  public Integer calculateTotalTransactions(
    Long employeeId,
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return performanceRepository.calculateTotalTransactions(
      employeeId,
      startDate,
      endDate
    );
  }

  /**
   * Finds top performers by sales between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param limit Maximum number of results to return
   * @return List of EmployeePerformanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeePerformanceInfo> findTopPerformersBySales(
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer limit
  ) {
    List<EmployeePerformance> performances =
      performanceRepository.findTopPerformersBySales(startDate, endDate, limit);
    return performanceMapper.toInfoList(performances);
  }

  /**
   * Finds top performers by customer satisfaction between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param limit Maximum number of results to return
   * @return List of EmployeePerformanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeePerformanceInfo> findTopPerformersByCustomerSatisfaction(
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer limit
  ) {
    List<EmployeePerformance> performances =
      performanceRepository.findByPeriodAndMinimumRating(
        startDate,
        endDate,
        limit
      );
    return performanceMapper.toInfoList(performances);
  }

  /**
   * Finds all performance records for a specific employee and period.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of EmployeePerformanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeePerformanceInfo> findByEmployeeAndPeriod(
    Long employeeId,
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return performanceRepository
      .findByEmployeeIdAndPeriodStartBetween(employeeId, startDate, endDate)
      .stream()
      .map(performanceMapper::toInfo)
      .toList();
  }

  /**
   * Finds top performers between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param limit Maximum number of results to return
   * @return List of EmployeePerformanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeePerformanceInfo> findTopPerformers(
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer limit
  ) {
    List<EmployeePerformance> performances =
      performanceRepository.findTopPerformers(startDate, endDate, limit);
    return performanceMapper.toInfoList(performances);
  }

  /**
   * Finds performance records by period and minimum rating.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param minRating Minimum rating to filter by
   * @return List of EmployeePerformanceInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeePerformanceInfo> findByPeriodAndMinimumRating(
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer minRating
  ) {
    return performanceRepository
      .findByPeriodAndMinimumRating(startDate, endDate, minRating)
      .stream()
      .map(performanceMapper::toInfo)
      .toList();
  }

  /**
   * Creates a new performance record.
   *
   * @param data The data for the new performance record
   * @return EmployeePerformanceInfo containing the created performance's information
   * @throws EntityNotFoundException if the referenced employee is not found
   */
  @Transactional
  public EmployeePerformanceInfo create(EmployeePerformanceData data) {
    validatePerformanceData(data);

    Employee employee = employeeRepository
      .findById(data.getEmployeeId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee not found: " + data.getEmployeeId()
        )
      );

    EmployeePerformance performance = performanceMapper.toEntity(
      data,
      employee
    );
    performance = performanceRepository.save(performance);
    return performanceMapper.toInfo(performance);
  }

  /**
   * Updates an existing performance record.
   *
   * @param id The ID of the performance record to update
   * @param data The new data for the performance record
   * @return EmployeePerformanceInfo containing the updated performance's information
   * @throws EntityNotFoundException if the performance record is not found
   */
  @Transactional
  public EmployeePerformanceInfo update(Long id, EmployeePerformanceData data) {
    validatePerformanceData(data);

    EmployeePerformance performance = performanceRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee Performance not found: " + id)
      );

    Employee employee = employeeRepository
      .findById(data.getEmployeeId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee not found: " + data.getEmployeeId()
        )
      );

    performanceMapper.updateEntity(performance, data);
    performance.setEmployee(employee);
    performance = performanceRepository.save(performance);
    return performanceMapper.toInfo(performance);
  }

  /**
   * Deletes a performance record.
   *
   * @param id The ID of the performance record to delete
   * @throws EntityNotFoundException if the performance record is not found
   */
  @Transactional
  public void delete(Long id) {
    if (!performanceRepository.existsById(id)) {
      throw new EntityNotFoundException(
        "Employee Performance not found: " + id
      );
    }
    performanceRepository.deleteById(id);
  }

  private void validatePerformanceData(EmployeePerformanceData data) {
    if (data.getEmployeeId() == null) {
      throw new IllegalArgumentException("Employee ID is required");
    }

    if (data.getPeriodStart() == null) {
      throw new IllegalArgumentException("Period start date is required");
    }

    if (data.getPeriodEnd() == null) {
      throw new IllegalArgumentException("Period end date is required");
    }

    if (data.getPeriodStart().isAfter(data.getPeriodEnd())) {
      throw new IllegalArgumentException(
        "Period start date cannot be after period end date"
      );
    }

    if (data.getSalesCount() == null) {
      throw new IllegalArgumentException("Sales count is required");
    }

    if (data.getSalesCount() < 0) {
      throw new IllegalArgumentException("Sales count cannot be negative");
    }

    if (data.getSalesTotal() == null) {
      throw new IllegalArgumentException("Sales total is required");
    }

    if (data.getSalesTotal().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Sales total cannot be negative");
    }

    if (data.getRating() == null) {
      throw new IllegalArgumentException("Rating is required");
    }

    if (data.getRating() < 0 || data.getRating() > 100) {
      throw new IllegalArgumentException("Rating must be between 0 and 100");
    }
  }
}
