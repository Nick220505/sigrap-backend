package com.sigrap.sale;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for exporting sales data to flat files.
 * Handles the creation of daily sales reports in flat file format.
 */
@Service
@RequiredArgsConstructor
public class SaleExportService {

  private final SaleService saleService;

  // DIAN business code
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
  public String generateDailySalesReport(LocalDate date, String exportPath)
    throws IOException {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

    List<SaleInfo> sales = saleService.findByCreatedDateRange(
      startOfDay,
      endOfDay
    );

    File directory = new File(exportPath);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yy"));
    String filename =
      "PAPELERIA" + BUSINESS_CODE + "_" + formattedDate + ".txt";

    String filePath = exportPath + File.separator + filename;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      writer.write(
        "CÉDULA_CLIENTE|FECHA_VENTA|VALOR_TOTAL|VALOR_TOTAL_CON_IVA"
      );
      writer.newLine();

      for (SaleInfo sale : sales) {
        if (sale.getCustomer().getDocumentId() != null) {
          String line = String.format(
            "%s|%s|%d|%d",
            sale.getCustomer().getDocumentId(),
            sale
              .getCreatedAt()
              .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            sale.getTotalAmount().intValue(),
            sale.getFinalAmount().intValue()
          );

          writer.write(line);
          writer.newLine();
        }
      }
    }

    return filePath;
  }
}
