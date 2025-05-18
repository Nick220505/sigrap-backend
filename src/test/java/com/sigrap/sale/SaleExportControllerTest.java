package com.sigrap.sale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleExportController.class)
class SaleExportControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SaleExportService saleExportService;

  private LocalDate testDate;
  private String formattedDate;

  @BeforeEach
  void setUp() {
    testDate = LocalDate.of(2023, 6, 15);
    formattedDate = testDate.format(DateTimeFormatter.ofPattern("dd-MM-yy"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void generateDailySalesReport_withAUTOExportPath_returnsFileContent()
    throws Exception {
    // Arrange
    String reportContent =
      "CÉDULA_CLIENTE|FECHA_VENTA|VALOR_TOTAL|VALOR_TOTAL_CON_IVA\n" +
      "123456789|15/06/2023|100|119";

    String expectedFilename = "PAPELERIA020_" + formattedDate + ".txt";

    when(
      saleExportService.generateDailySalesReportContent(eq(testDate))
    ).thenReturn(reportContent);

    // Act & Assert
    mockMvc
      .perform(
        get("/api/sales/export/daily")
          .param("date", testDate.toString())
          .param("exportPath", "AUTO")
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.TEXT_PLAIN))
      .andExpect(
        header()
          .string(
            "Content-Disposition",
            "form-data; name=\"attachment\"; filename=\"" +
            expectedFilename +
            "\""
          )
      )
      .andExpect(content().string(reportContent));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void generateDailySalesReport_withCustomExportPath_returnsFilePath()
    throws Exception {
    // Arrange
    String exportPath = "/custom/path";
    String generatedFilePath =
      exportPath + "/PAPELERIA020_" + formattedDate + ".txt";

    when(
      saleExportService.generateDailySalesReport(eq(testDate), eq(exportPath))
    ).thenReturn(generatedFilePath);

    // Act & Assert
    mockMvc
      .perform(
        get("/api/sales/export/daily")
          .param("date", testDate.toString())
          .param("exportPath", exportPath)
      )
      .andExpect(status().isOk())
      .andExpect(content().string(generatedFilePath));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void generateDailySalesReport_withNoDate_usesTodaysDate() throws Exception {
    // Arrange
    String exportPath = "/custom/path";
    String generatedFilePath = "/custom/path/PAPELERIA020_some-date.txt";

    when(
      saleExportService.generateDailySalesReport(
        any(LocalDate.class),
        eq(exportPath)
      )
    ).thenReturn(generatedFilePath);

    // Act & Assert
    mockMvc
      .perform(get("/api/sales/export/daily").param("exportPath", exportPath))
      .andExpect(status().isOk())
      .andExpect(content().string(generatedFilePath));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void generateDailySalesReport_withIOException_returnsErrorMessage()
    throws Exception {
    // Arrange
    String exportPath = "/invalid/path";
    String errorMessage = "Error writing to file";

    when(
      saleExportService.generateDailySalesReport(
        any(LocalDate.class),
        eq(exportPath)
      )
    ).thenThrow(new IOException(errorMessage));

    // Act & Assert
    mockMvc
      .perform(get("/api/sales/export/daily").param("exportPath", exportPath))
      .andExpect(status().isInternalServerError())
      .andExpect(content().string("Error generating report: " + errorMessage));
  }
}
