package com.sigrap.sale;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for exporting sales data to flat files.
 * Handles the creation of daily sales reports in flat file format.
 */
@Service
@RequiredArgsConstructor
public class SaleExportService {

  private final SaleService saleService;

  private static final String BUSINESS_CODE = "020";

  /**
   * Generates a flat file containing all sales for a specific date.
   * The file includes customer ID, sale date, total amount, and total with IVA for each sale.
   *
   * @param date The date for which to generate the report
   * @param exportPath The directory path where to save the generated file
   * @return The path of the generated file
   * @throws IOException If an error occurs while writing the file
   */
  @Transactional(readOnly = true)
  public String generateDailySalesReport(LocalDate date, String exportPath)
    throws IOException {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

    List<SaleInfo> sales = saleService.findByCreatedDateRange(
      startOfDay,
      endOfDay
    );

    String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yy"));
    String filename =
      "PAPELERIA" + BUSINESS_CODE + "_" + formattedDate + ".txt";

    File directory = new File(exportPath);
    if (!directory.exists()) {
      directory.mkdirs();
    }
    String filePath = exportPath + File.separator + filename;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      writeSalesData(sales, writer);
    }

    return filePath;
  }

  /**
   * Generates the content of a daily sales report as a String.
   * The report includes customer ID, sale date, total amount, and total with IVA for each sale.
   * This method is used for direct downloads without saving to disk.
   *
   * @param date The date for which to generate the report
   * @return The report content as a String
   * @throws IOException If an error occurs while writing the file
   */
  @Transactional(readOnly = true)
  public String generateDailySalesReportContent(LocalDate date)
    throws IOException {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

    List<SaleInfo> sales = saleService.findByCreatedDateRange(
      startOfDay,
      endOfDay
    );

    StringWriter stringWriter = new StringWriter();
    try (BufferedWriter writer = new BufferedWriter(stringWriter)) {
      writeSalesData(sales, writer);
    }

    return stringWriter.toString();
  }

  /**
   * Helper method to write sales data to a writer.
   *
   * @param sales The list of sales to write
   * @param writer The writer to write to
   * @throws IOException If an error occurs while writing
   */
  private void writeSalesData(List<SaleInfo> sales, BufferedWriter writer)
    throws IOException {
    writer.write("CÉDULA_CLIENTE|FECHA_VENTA|VALOR_TOTAL|VALOR_TOTAL_CON_IVA");
    writer.newLine();

    for (SaleInfo sale : sales) {
      if (sale.getCustomer().getDocumentId() != null) {
        String line = String.format(
          "%s|%s|%d|%d",
          sale.getCustomer().getDocumentId(),
          sale.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
          sale.getTotalAmount().intValue(),
          sale.getFinalAmount().intValue()
        );

        writer.write(line);
        writer.newLine();
      }
    }
  }

  /**
   * Exports sales data to Excel format for a specified date range.
   *
   * @param startDate The start date of the report period
   * @param endDate The end date of the report period
   * @return Excel file as byte array
   */
  @Transactional(readOnly = true)
  public byte[] exportSalesToExcel(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) throws IOException {
    List<SaleInfo> sales = saleService.findByCreatedDateRange(
      startDate,
      endDate
    );
    return generateExcelFile(sales);
  }

  /**
   * Exports all sales data to Excel format.
   *
   * @return Excel file as byte array
   */
  @Transactional(readOnly = true)
  public byte[] exportAllSalesToExcel() throws IOException {
    List<SaleInfo> sales = saleService.findAll();
    return generateExcelFile(sales);
  }

  /**
   * Generates an Excel file containing sales data.
   *
   * @param sales List of sales to include in the export
   * @return Excel file as byte array
   */
  private byte[] generateExcelFile(List<SaleInfo> sales) throws IOException {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * Exports sales data to CSV format for a specified date range.
   *
   * @param startDate The start date of the report period
   * @param endDate The end date of the report period
   * @return CSV content as string
   */
  @Transactional(readOnly = true)
  public String exportSalesToCsv(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    List<SaleInfo> sales = saleService.findByCreatedDateRange(
      startDate,
      endDate
    );
    return generateCsvContent(sales);
  }

  /**
   * Exports all sales data to CSV format.
   *
   * @return CSV content as string
   */
  @Transactional(readOnly = true)
  public String exportAllSalesToCsv() {
    List<SaleInfo> sales = saleService.findAll();
    return generateCsvContent(sales);
  }

  /**
   * Generates CSV content for sales data.
   *
   * @param sales List of sales to include in the export
   * @return CSV content as string
   */
  private String generateCsvContent(List<SaleInfo> sales) {
    throw new UnsupportedOperationException("Method not implemented");
  }
}
