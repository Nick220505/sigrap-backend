package com.sigrap.sale;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for exporting sales data.
 */
@RestController
@RequestMapping("/api/sales/export")
@RequiredArgsConstructor
@Tag(name = "Sales Export", description = "Operations for exporting sales data")
public class SaleExportController {

  private final SaleExportService saleExportService;

  /**
   * Generate a flat file with sales data for the specified date.
   *
   * @param date       The date for which to generate the report (defaults to today if not provided)
   * @param exportPath The directory path where to save the file or "AUTO" for direct download
   * @return The path to the generated file or the file content for direct download
   */
  @GetMapping("/daily")
  @Operation(
    summary = "Generate daily sales report",
    description = "Generates a flat file containing sales data for the specified date"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Report generated successfully"
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Error generating report"
      ),
    }
  )
  public ResponseEntity<String> generateDailySalesReport(
    @Parameter(description = "Date for the report (yyyy-MM-dd)") @RequestParam(
      required = false
    ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
    @Parameter(
      description = "Directory path where to save the file or 'AUTO' for direct download"
    ) @RequestParam String exportPath
  ) {
    try {
      LocalDate reportDate = date != null ? date : LocalDate.now();

      if ("AUTO".equalsIgnoreCase(exportPath)) {
        String formattedDate = reportDate.format(
          DateTimeFormatter.ofPattern("dd-MM-yy")
        );
        String filename = "PAPELERIA020_" + formattedDate + ".txt";

        String fileContent = saleExportService.generateDailySalesReportContent(
          reportDate
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
      } else {
        String filePath = saleExportService.generateDailySalesReport(
          reportDate,
          exportPath
        );
        return ResponseEntity.ok(filePath);
      }
    } catch (IOException e) {
      return ResponseEntity.internalServerError()
        .body("Error generating report: " + e.getMessage());
    }
  }
}
