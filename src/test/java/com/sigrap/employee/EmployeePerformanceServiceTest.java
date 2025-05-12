package com.sigrap.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeePerformanceServiceTest {

  @Mock
  private EmployeePerformanceRepository performanceRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private EmployeePerformanceMapper performanceMapper;

  @InjectMocks
  private EmployeePerformanceService performanceService;

  private Employee testEmployee;
  private EmployeePerformance testPerformance;
  private EmployeePerformanceInfo testPerformanceInfo;
  private EmployeePerformanceData testPerformanceData;
  private LocalDateTime periodStart;
  private LocalDateTime periodEnd;

  @BeforeEach
  void setUp() {
    periodStart = LocalDateTime.now().minusDays(30);
    periodEnd = LocalDateTime.now();

    testEmployee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .build();

    testPerformance = EmployeePerformance.builder()
      .id(1L)
      .employee(testEmployee)
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .transactionAverage(BigDecimal.valueOf(100))
      .rating(90)
      .notes("Great performance")
      .build();

    testPerformanceInfo = EmployeePerformanceInfo.builder()
      .id(1L)
      .employeeId(1L)
      .employeeName("John Doe")
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .transactionAverage(BigDecimal.valueOf(100))
      .rating(90)
      .notes("Great performance")
      .build();

    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .rating(90)
      .notes("Great performance")
      .build();
  }

  @Test
  void findAll_ShouldReturnAllPerformanceRecords() {
    when(performanceRepository.findAll()).thenReturn(List.of(testPerformance));
    when(performanceMapper.toInfoList(List.of(testPerformance))).thenReturn(
      List.of(testPerformanceInfo)
    );

    List<EmployeePerformanceInfo> result = performanceService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testPerformanceInfo.getId(), result.get(0).getId());
  }

  @Test
  void findById_ShouldReturnPerformanceRecord() {
    when(performanceRepository.findById(1L)).thenReturn(
      Optional.of(testPerformance)
    );
    when(performanceMapper.toInfo(testPerformance)).thenReturn(
      testPerformanceInfo
    );

    EmployeePerformanceInfo result = performanceService.findById(1L);

    assertNotNull(result);
    assertEquals(testPerformanceInfo.getId(), result.getId());
  }

  @Test
  void findById_ShouldThrowEntityNotFoundException() {
    when(performanceRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      performanceService.findById(1L)
    );
  }

  @Test
  void create_ShouldCreateNewPerformanceRecord() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(
      performanceMapper.toEntity(testPerformanceData, testEmployee)
    ).thenReturn(testPerformance);
    when(performanceRepository.save(any(EmployeePerformance.class))).thenReturn(
      testPerformance
    );
    when(performanceMapper.toInfo(testPerformance)).thenReturn(
      testPerformanceInfo
    );

    EmployeePerformanceInfo result = performanceService.create(
      testPerformanceData
    );

    assertNotNull(result);
    assertEquals(testPerformanceInfo.getId(), result.getId());
  }

  @Test
  void create_ShouldThrowEntityNotFoundException() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      performanceService.create(testPerformanceData)
    );
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenEmployeeIdIsNull() {
    testPerformanceData = EmployeePerformanceData.builder()
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .rating(90)
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals("Employee ID is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenPeriodStartIsNull() {
    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodEnd(periodEnd)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .rating(90)
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals("Period start date is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenPeriodEndIsNull() {
    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(periodStart)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .rating(90)
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals("Period end date is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenPeriodStartIsAfterPeriodEnd() {
    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(periodEnd)
      .periodEnd(periodStart)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .rating(90)
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals(
      "Period start date cannot be after period end date",
      exception.getMessage()
    );
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenSalesCountIsNull() {
    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesTotal(BigDecimal.valueOf(10000))
      .rating(90)
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals("Sales count is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenSalesCountIsNegative() {
    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(-1)
      .salesTotal(BigDecimal.valueOf(10000))
      .rating(90)
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals("Sales count cannot be negative", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenSalesTotalIsNull() {
    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(100)
      .rating(90)
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals("Sales total is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenSalesTotalIsNegative() {
    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(-10000))
      .rating(90)
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals("Sales total cannot be negative", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenRatingIsNull() {
    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals("Rating is required", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenRatingIsOutOfRange() {
    testPerformanceData = EmployeePerformanceData.builder()
      .employeeId(1L)
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .rating(101)
      .notes("Great performance")
      .build();

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> performanceService.create(testPerformanceData)
    );
    assertEquals("Rating must be between 0 and 100", exception.getMessage());
  }

  @Test
  void update_ShouldUpdatePerformanceRecord() {
    when(performanceRepository.findById(1L)).thenReturn(
      Optional.of(testPerformance)
    );
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(performanceRepository.save(any(EmployeePerformance.class))).thenReturn(
      testPerformance
    );
    when(performanceMapper.toInfo(testPerformance)).thenReturn(
      testPerformanceInfo
    );

    EmployeePerformanceInfo result = performanceService.update(
      1L,
      testPerformanceData
    );

    assertNotNull(result);
    assertEquals(testPerformanceInfo.getId(), result.getId());
  }

  @Test
  void update_ShouldThrowEntityNotFoundException() {
    when(performanceRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      performanceService.update(1L, testPerformanceData)
    );
  }

  @Test
  void delete_ShouldDeletePerformanceRecord() {
    when(performanceRepository.existsById(1L)).thenReturn(true);

    performanceService.delete(1L);

    verify(performanceRepository).deleteById(1L);
  }

  @Test
  void delete_ShouldThrowEntityNotFoundException() {
    when(performanceRepository.existsById(1L)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () ->
      performanceService.delete(1L)
    );
  }

  @Test
  void findByEmployeeId_ShouldReturnPerformanceRecords() {
    when(performanceRepository.findByEmployeeId(1L)).thenReturn(
      List.of(testPerformance)
    );
    when(performanceMapper.toInfoList(List.of(testPerformance))).thenReturn(
      List.of(testPerformanceInfo)
    );

    List<EmployeePerformanceInfo> result = performanceService.findByEmployeeId(
      1L
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testPerformanceInfo.getId(), result.get(0).getId());
  }

  @Test
  void findTopPerformers_ShouldReturnTopPerformers() {
    List<EmployeePerformance> performances = List.of(
      EmployeePerformance.builder()
        .id(1L)
        .rating(5)
        .salesTotal(BigDecimal.valueOf(1000))
        .salesCount(10)
        .build(),
      EmployeePerformance.builder()
        .id(2L)
        .rating(4)
        .salesTotal(BigDecimal.valueOf(800))
        .salesCount(8)
        .build()
    );

    when(
      performanceRepository.findTopPerformers(periodStart, periodEnd, 2)
    ).thenReturn(performances);
    when(performanceMapper.toInfoList(performances)).thenReturn(
      performances
        .stream()
        .map(p ->
          EmployeePerformanceInfo.builder()
            .id(p.getId())
            .rating(p.getRating())
            .salesTotal(p.getSalesTotal())
            .salesCount(p.getSalesCount())
            .build()
        )
        .toList()
    );

    List<EmployeePerformanceInfo> result = performanceService.findTopPerformers(
      periodStart,
      periodEnd,
      2
    );

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(1).getId()).isEqualTo(2L);
  }

  @Test
  void calculateTotalSales_ShouldReturnTotalSales() {
    when(
      performanceRepository.calculateTotalSales(1L, periodStart, periodEnd)
    ).thenReturn(BigDecimal.valueOf(10000));

    BigDecimal result = performanceService.calculateTotalSales(
      1L,
      periodStart,
      periodEnd
    );

    assertNotNull(result);
    assertEquals(BigDecimal.valueOf(10000), result);
  }
}
