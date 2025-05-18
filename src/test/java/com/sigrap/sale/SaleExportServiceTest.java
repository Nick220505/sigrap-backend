package com.sigrap.sale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.sigrap.customer.CustomerInfo;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleExportServiceTest {

  @Mock
  private SaleService saleService;

  @InjectMocks
  private SaleExportService saleExportService;

  private List<SaleInfo> mockSales;
  private LocalDate testDate;

  @BeforeEach
  void setUp() {
    testDate = LocalDate.of(2023, 5, 15);

    CustomerInfo customer1 = CustomerInfo.builder()
      .id(1L)
      .fullName("Test Customer")
      .documentId("123456789")
      .build();

    CustomerInfo customer2 = CustomerInfo.builder()
      .id(2L)
      .fullName("Other Customer")
      .documentId("987654321")
      .build();

    SaleInfo sale1 = SaleInfo.builder()
      .id(1)
      .totalAmount(new BigDecimal("100.00"))
      .finalAmount(new BigDecimal("119.00"))
      .customer(customer1)
      .createdAt(LocalDateTime.of(testDate, LocalTime.of(10, 30)))
      .build();

    SaleInfo sale2 = SaleInfo.builder()
      .id(2)
      .totalAmount(new BigDecimal("200.00"))
      .finalAmount(new BigDecimal("238.00"))
      .customer(customer2)
      .createdAt(LocalDateTime.of(testDate, LocalTime.of(14, 45)))
      .build();

    mockSales = Arrays.asList(sale1, sale2);
  }

  @AfterEach
  void tearDown() {
    // Clean up any temporary files created during tests
  }

  @Test
  void generateDailySalesReport_createsFileWithCorrectContent()
    throws IOException {
    // Arrange
    LocalDateTime startOfDay = testDate.atStartOfDay();
    LocalDateTime endOfDay = testDate.atTime(LocalTime.MAX);

    when(saleService.findByCreatedDateRange(startOfDay, endOfDay)).thenReturn(
      mockSales
    );

    String tempDir = System.getProperty("java.io.tmpdir");

    // Act
    String filePath = saleExportService.generateDailySalesReport(
      testDate,
      tempDir
    );

    // Assert
    assertTrue(filePath.contains("PAPELERIA020_15-05-23.txt"));
    Path path = Path.of(filePath);
    assertTrue(Files.exists(path));

    List<String> lines = Files.readAllLines(path);
    assertEquals(3, lines.size()); // Header + 2 sales
    assertEquals(
      "CÉDULA_CLIENTE|FECHA_VENTA|VALOR_TOTAL|VALOR_TOTAL_CON_IVA",
      lines.get(0)
    );
    assertTrue(lines.get(1).startsWith("123456789|15/05/2023|100|119"));
    assertTrue(lines.get(2).startsWith("987654321|15/05/2023|200|238"));

    // Clean up
    Files.deleteIfExists(path);
  }

  @Test
  void generateDailySalesReportContent_returnsCorrectContent()
    throws IOException {
    // Arrange
    LocalDateTime startOfDay = testDate.atStartOfDay();
    LocalDateTime endOfDay = testDate.atTime(LocalTime.MAX);

    when(saleService.findByCreatedDateRange(startOfDay, endOfDay)).thenReturn(
      mockSales
    );

    // Act
    String content = saleExportService.generateDailySalesReportContent(
      testDate
    );

    // Assert
    String[] lines = content.split("\\r?\\n");
    assertEquals(3, lines.length); // Header + 2 sales
    assertEquals(
      "CÉDULA_CLIENTE|FECHA_VENTA|VALOR_TOTAL|VALOR_TOTAL_CON_IVA",
      lines[0]
    );
    assertTrue(lines[1].startsWith("123456789|15/05/2023|100|119"));
    assertTrue(lines[2].startsWith("987654321|15/05/2023|200|238"));
  }

  @Test
  void generateDailySalesReport_withNoSales_createFileWithHeaderOnly()
    throws IOException {
    // Arrange
    LocalDateTime startOfDay = testDate.atStartOfDay();
    LocalDateTime endOfDay = testDate.atTime(LocalTime.MAX);

    when(saleService.findByCreatedDateRange(startOfDay, endOfDay)).thenReturn(
      List.of()
    );

    String tempDir = System.getProperty("java.io.tmpdir");

    // Act
    String filePath = saleExportService.generateDailySalesReport(
      testDate,
      tempDir
    );

    // Assert
    Path path = Path.of(filePath);
    assertTrue(Files.exists(path));

    List<String> lines = Files.readAllLines(path);
    assertEquals(1, lines.size()); // Only header
    assertEquals(
      "CÉDULA_CLIENTE|FECHA_VENTA|VALOR_TOTAL|VALOR_TOTAL_CON_IVA",
      lines.get(0)
    );

    // Clean up
    Files.deleteIfExists(path);
  }

  @Test
  void generateDailySalesReport_createsDirectoryIfNotExists()
    throws IOException {
    // Arrange
    LocalDateTime startOfDay = testDate.atStartOfDay();
    LocalDateTime endOfDay = testDate.atTime(LocalTime.MAX);

    when(saleService.findByCreatedDateRange(startOfDay, endOfDay)).thenReturn(
      mockSales
    );

    String tempDir =
      System.getProperty("java.io.tmpdir") +
      "/sigrap_test_" +
      System.currentTimeMillis();
    Path tempDirPath = Path.of(tempDir);

    // Act
    String filePath = saleExportService.generateDailySalesReport(
      testDate,
      tempDir
    );

    // Assert
    assertTrue(Files.exists(tempDirPath));
    Path path = Path.of(filePath);
    assertTrue(Files.exists(path));

    // Clean up
    Files.deleteIfExists(path);
    Files.deleteIfExists(tempDirPath);
  }

  @Test
  void generateDailySalesReportContent_handlesNullDocumentId()
    throws IOException {
    // Arrange
    CustomerInfo customerWithNullId = CustomerInfo.builder()
      .id(3L)
      .fullName("Null ID Customer")
      .documentId(null) // Explicitly null document ID
      .build();

    SaleInfo saleWithNullCustomerDoc = SaleInfo.builder()
      .id(3)
      .totalAmount(new BigDecimal("300.00"))
      .finalAmount(new BigDecimal("357.00"))
      .customer(customerWithNullId)
      .createdAt(LocalDateTime.of(testDate, LocalTime.of(16, 20)))
      .build();

    List<SaleInfo> salesWithNull = Arrays.asList(
      mockSales.get(0),
      saleWithNullCustomerDoc
    );

    LocalDateTime startOfDay = testDate.atStartOfDay();
    LocalDateTime endOfDay = testDate.atTime(LocalTime.MAX);

    when(saleService.findByCreatedDateRange(startOfDay, endOfDay)).thenReturn(
      salesWithNull
    );

    // Act
    String content = saleExportService.generateDailySalesReportContent(
      testDate
    );

    // Assert
    String[] lines = content.split("\\r?\\n");
    assertEquals(2, lines.length); // Header + 1 valid sale (null ID sale should be skipped)
    assertEquals(
      "CÉDULA_CLIENTE|FECHA_VENTA|VALOR_TOTAL|VALOR_TOTAL_CON_IVA",
      lines[0]
    );
    assertTrue(lines[1].startsWith("123456789"));
  }
}
