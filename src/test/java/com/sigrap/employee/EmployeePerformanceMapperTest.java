package com.sigrap.employee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeePerformanceMapperTest {

  private EmployeePerformanceMapper mapper;
  private EmployeePerformance performance;
  private EmployeePerformanceData data;
  private Employee employee;

  @BeforeEach
  void setUp() {
    mapper = new EmployeePerformanceMapper();

    employee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .build();

    performance = EmployeePerformance.builder()
      .id(1L)
      .employee(employee)
      .periodStart(LocalDateTime.now().minusDays(7))
      .periodEnd(LocalDateTime.now())
      .salesCount(10)
      .salesTotal(BigDecimal.valueOf(1000))
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    data = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(LocalDateTime.now().minusDays(7))
      .periodEnd(LocalDateTime.now())
      .salesCount(10)
      .salesTotal(BigDecimal.valueOf(1000))
      .build();
  }

  @Test
  void toInfo_ShouldMapAllFields() {
    EmployeePerformanceInfo info = mapper.toInfo(performance);

    assertNotNull(info);
    assertEquals(performance.getId(), info.getId());
    assertEquals(employee.getId(), info.getEmployeeId());
    assertEquals("John Doe", info.getEmployeeName());
    assertEquals(performance.getPeriodStart(), info.getPeriodStart());
    assertEquals(performance.getPeriodEnd(), info.getPeriodEnd());
    assertEquals(performance.getSalesCount(), info.getSalesCount());
    assertEquals(performance.getSalesTotal(), info.getSalesTotal());
    assertEquals(
      0,
      BigDecimal.valueOf(100).compareTo(info.getAverageTransactionValue())
    );
    assertEquals(performance.getCreatedAt(), info.getCreatedAt());
    assertEquals(performance.getUpdatedAt(), info.getUpdatedAt());
  }

  @Test
  void toInfo_ShouldReturnNull_WhenEntityIsNull() {
    assertNull(mapper.toInfo(null));
  }

  @Test
  void toInfoList_ShouldMapAllEntities() {
    List<EmployeePerformanceInfo> infos = mapper.toInfoList(
      List.of(performance)
    );

    assertNotNull(infos);
    assertEquals(1, infos.size());

    EmployeePerformanceInfo info = infos.get(0);
    assertEquals(performance.getId(), info.getId());
    assertEquals(employee.getId(), info.getEmployeeId());
    assertEquals("John Doe", info.getEmployeeName());
    assertEquals(performance.getPeriodStart(), info.getPeriodStart());
    assertEquals(performance.getPeriodEnd(), info.getPeriodEnd());
    assertEquals(performance.getSalesCount(), info.getSalesCount());
    assertEquals(performance.getSalesTotal(), info.getSalesTotal());
    assertEquals(
      0,
      BigDecimal.valueOf(100).compareTo(info.getAverageTransactionValue())
    );
    assertEquals(performance.getCreatedAt(), info.getCreatedAt());
    assertEquals(performance.getUpdatedAt(), info.getUpdatedAt());
  }

  @Test
  void toInfoList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
    List<EmployeePerformanceInfo> infos = mapper.toInfoList(null);

    assertNotNull(infos);
    assertEquals(0, infos.size());
  }

  @Test
  void toEntity_ShouldMapAllFields() {
    EmployeePerformance entity = mapper.toEntity(data, employee);

    assertNotNull(entity);
    assertEquals(employee, entity.getEmployee());
    assertEquals(data.getPeriodStart(), entity.getPeriodStart());
    assertEquals(data.getPeriodEnd(), entity.getPeriodEnd());
    assertEquals(data.getSalesCount(), entity.getSalesCount());
    assertEquals(data.getSalesTotal(), entity.getSalesTotal());
  }

  @Test
  void toEntity_ShouldReturnNull_WhenDataIsNull() {
    assertNull(mapper.toEntity(null, employee));
  }

  @Test
  void updateEntity_ShouldUpdateAllFields() {
    EmployeePerformance entity = EmployeePerformance.builder()
      .id(1L)
      .employee(employee)
      .periodStart(LocalDateTime.now().minusDays(14))
      .periodEnd(LocalDateTime.now().minusDays(7))
      .salesCount(5)
      .salesTotal(BigDecimal.valueOf(500))
      .build();

    mapper.updateEntity(entity, data);

    assertEquals(data.getPeriodStart(), entity.getPeriodStart());
    assertEquals(data.getPeriodEnd(), entity.getPeriodEnd());
    assertEquals(data.getSalesCount(), entity.getSalesCount());
    assertEquals(data.getSalesTotal(), entity.getSalesTotal());
  }

  @Test
  void updateEntity_ShouldNotUpdateFields_WhenDataIsNull() {
    EmployeePerformance entity = EmployeePerformance.builder()
      .id(1L)
      .employee(employee)
      .periodStart(LocalDateTime.now().minusDays(14))
      .periodEnd(LocalDateTime.now().minusDays(7))
      .salesCount(5)
      .salesTotal(BigDecimal.valueOf(500))
      .build();

    LocalDateTime originalPeriodStart = entity.getPeriodStart();
    LocalDateTime originalPeriodEnd = entity.getPeriodEnd();
    Integer originalSalesCount = entity.getSalesCount();
    BigDecimal originalSalesTotal = entity.getSalesTotal();

    mapper.updateEntity(entity, null);

    assertEquals(originalPeriodStart, entity.getPeriodStart());
    assertEquals(originalPeriodEnd, entity.getPeriodEnd());
    assertEquals(originalSalesCount, entity.getSalesCount());
    assertEquals(originalSalesTotal, entity.getSalesTotal());
  }
}
