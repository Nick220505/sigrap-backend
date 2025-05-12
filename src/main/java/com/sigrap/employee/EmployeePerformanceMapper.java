package com.sigrap.employee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between EmployeePerformance entities and DTOs.
 * Handles the conversion of data between different representations.
 *
 * <p>This class provides methods for:
 * <ul>
 *   <li>Converting entities to DTOs</li>
 *   <li>Converting DTOs to entities</li>
 *   <li>Updating entities from DTOs</li>
 *   <li>Batch conversions</li>
 * </ul></p>
 */
@Component
@RequiredArgsConstructor
public class EmployeePerformanceMapper {

  /**
   * Converts an EmployeePerformance entity to EmployeePerformanceInfo DTO.
   *
   * @param performance The performance entity to convert
   * @return EmployeePerformanceInfo containing the performance's information
   */
  public EmployeePerformanceInfo toInfo(EmployeePerformance performance) {
    if (performance == null) {
      return null;
    }

    return EmployeePerformanceInfo.builder()
      .id(performance.getId())
      .employeeId(performance.getEmployee().getId())
      .employeeName(getFullName(performance.getEmployee()))
      .periodStart(performance.getPeriodStart())
      .periodEnd(performance.getPeriodEnd())
      .salesCount(performance.getSalesCount())
      .salesTotal(performance.getSalesTotal())
      .averageTransactionValue(calculateAverageTransactionValue(performance))
      .createdAt(performance.getCreatedAt())
      .updatedAt(performance.getUpdatedAt())
      .build();
  }

  private String getFullName(Employee employee) {
    return employee.getFirstName() + " " + employee.getLastName();
  }

  /**
   * Converts a list of EmployeePerformance entities to a list of EmployeePerformanceInfo DTOs.
   *
   * @param performances List of performance entities to convert
   * @return List of EmployeePerformanceInfo DTOs
   */
  public List<EmployeePerformanceInfo> toInfoList(
    List<EmployeePerformance> performances
  ) {
    if (performances == null) {
      return Collections.emptyList();
    }

    return performances.stream().map(this::toInfo).toList();
  }

  private BigDecimal calculateAverageTransactionValue(
    EmployeePerformance performance
  ) {
    if (performance.getSalesCount() == 0) {
      return BigDecimal.ZERO;
    }
    return performance
      .getSalesTotal()
      .divide(
        BigDecimal.valueOf(performance.getSalesCount()),
        2,
        RoundingMode.HALF_UP
      );
  }

  /**
   * Creates a new EmployeePerformance entity from EmployeePerformanceData DTO.
   *
   * @param data The DTO containing performance data
   * @param employee The employee associated with the performance
   * @return New EmployeePerformance entity
   */
  public EmployeePerformance toEntity(
    EmployeePerformanceData data,
    Employee employee
  ) {
    if (data == null) {
      return null;
    }

    return EmployeePerformance.builder()
      .employee(employee)
      .periodStart(data.getPeriodStart())
      .periodEnd(data.getPeriodEnd())
      .salesCount(data.getSalesCount())
      .salesTotal(data.getSalesTotal())
      .build();
  }

  /**
   * Updates an existing EmployeePerformance entity with data from EmployeePerformanceData DTO.
   *
   * @param entity The performance entity to update
   * @param data The DTO containing new performance data
   */
  public void updateEntity(
    EmployeePerformance entity,
    EmployeePerformanceData data
  ) {
    if (data == null) {
      return;
    }

    entity.setPeriodStart(data.getPeriodStart());
    entity.setPeriodEnd(data.getPeriodEnd());
    entity.setSalesCount(data.getSalesCount());
    entity.setSalesTotal(data.getSalesTotal());
  }
}
