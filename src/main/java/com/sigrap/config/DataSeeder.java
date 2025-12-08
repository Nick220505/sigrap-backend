package com.sigrap.config;

import com.sigrap.audit.AuditLog;
import com.sigrap.audit.AuditLogRepository;
import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.customer.Customer;
import com.sigrap.customer.CustomerRepository;
import com.sigrap.employee.attendance.Attendance;
import com.sigrap.employee.attendance.AttendanceRepository;
import com.sigrap.employee.attendance.AttendanceStatus;
import com.sigrap.employee.schedule.Schedule;
import com.sigrap.employee.schedule.ScheduleRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.sale.Sale;
import com.sigrap.sale.SaleItem;
import com.sigrap.sale.SaleRepository;
import com.sigrap.sale.SaleReturn;
import com.sigrap.sale.SaleReturnItem;
import com.sigrap.sale.SaleReturnRepository;
import com.sigrap.supplier.PurchaseOrder;
import com.sigrap.supplier.PurchaseOrderItem;
import com.sigrap.supplier.PurchaseOrderRepository;
import com.sigrap.supplier.PurchaseOrderStatus;
import com.sigrap.supplier.Supplier;
import com.sigrap.supplier.SupplierRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserRole;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configuration component for seeding initial data into the database.
 * Automatically runs on application startup when enabled.
 *
 * <p>
 * This seeder ensures the application has necessary initial data for proper
 * operation.
 * It only seeds data if the respective tables are empty, preventing duplicate
 * entries.
 * </p>
 *
 * <p>
 * The seeder can be disabled using the property
 * 'app.data-seeder.enabled=false'.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
  name = "app.data-seeder.enabled",
  havingValue = "true",
  matchIfMissing = true
)
public class DataSeeder implements CommandLineRunner {

  private final AuditLogRepository auditLogRepository;

  /**
   * Repository for category database operations.
   * Used to check if categories exist and to save new categories during seeding.
   */
  private final CategoryRepository categoryRepository;

  /**
   * Repository for product database operations.
   * Used to check if products exist and to save new products during seeding.
   */
  private final ProductRepository productRepository;

  /**
   * Repository for user database operations.
   * Used to check if users exist and to save new user accounts during seeding.
   */
  private final UserRepository userRepository;

  /**
   * Password encoder for securely hashing user passwords before storing them.
   * Ensures that seeded user account passwords are properly secured.
   */
  private final PasswordEncoder passwordEncoder;

  /**
   * Repository for schedule database operations.
   * Used to check if schedules exist and to save new schedules during seeding.
   */
  private final ScheduleRepository scheduleRepository;

  /**
   * Repository for attendance database operations.
   * Used to check if attendance records exist and to save new attendance records
   * during seeding.
   */
  private final AttendanceRepository attendanceRepository;

  /**
   * Repository for supplier database operations.
   * Used to check if suppliers exist and to save new suppliers during seeding.
   */
  private final SupplierRepository supplierRepository;

  /**
   * Repository for purchase order database operations.
   * Used to check if purchase orders exist and to save new purchase orders during
   * seeding.
   */
  private final PurchaseOrderRepository purchaseOrderRepository;

  /**
   * Repository for customer database operations.
   * Used to check if customers exist and to save new customers during seeding.
   */
  private final CustomerRepository customerRepository;

  private final SaleRepository saleRepository;
  private final SaleReturnRepository saleReturnRepository;

  private final Random random = new Random();

  /**
   * Executes the data seeding process on application startup.
   * Ensures all required initial data is present in the database.
   *
   * @param args Command line arguments (not used)
   * @throws Exception if any seeding operation fails
   */
  @Override
  @Transactional
  public void run(String... args) throws Exception {
    log.info("Starting data seeding...");
    seedCategories();
    seedProducts();
    seedUsers();
    seedSchedules();
    seedAttendance();
    seedSuppliers();
    seedPurchaseOrders();
    seedCustomers();
    seedSales();
    seedSaleReturns();
    seedAuditLogs();
    log.info("Data seeding completed.");
  }

  /**
   * Seeds initial user accounts into the database.
   * Creates default administrative and employee accounts with secure passwords.
   * Seeds initial product categories into the database.
   * Creates a comprehensive set of categories covering different types of
   * stationery items.
   * Only executes if the categories table is empty.
   */
  private void seedCategories() {
    if (categoryRepository.count() == 0) {
      log.info("Seeding categories...");

      categoryRepository.saveAll(
        List.of(
          Category.builder()
            .name("School Supplies")
            .description(
              "Basic supplies for elementary, middle, and high school students."
            )
            .build(),
          Category.builder()
            .name("Office Supplies")
            .description(
              "Professional supplies for office and administrative work."
            )
            .build(),
          Category.builder()
            .name("General Stationery")
            .description(
              "Paper, envelopes, notebooks, and other everyday stationery items."
            )
            .build(),
          Category.builder()
            .name("Gifts and Wrapping")
            .description(
              "Decorative items, greeting cards, and gift packaging."
            )
            .build(),
          Category.builder()
            .name("Basic Technology")
            .description(
              "Computer accessories, storage devices, and simple electronics."
            )
            .build(),
          Category.builder()
            .name("Art and Drawing")
            .description(
              "Materials for artistic expression, technical drawing, and crafts."
            )
            .build(),
          Category.builder()
            .name("Writing Instruments")
            .description(
              "Pens, pencils, markers, and other writing tools."
            )
            .build(),
          Category.builder()
            .name("Notebooks and Journals")
            .description(
              "Different notebook formats for school and professional use."
            )
            .build(),
          Category.builder()
            .name("Organization and Filing")
            .description(
              "Products to sort, store, and organize documents."
            )
            .build(),
          Category.builder()
            .name("Backpacks and Bags")
            .description(
              "Solutions for carrying school and office materials."
            )
            .build(),
          Category.builder()
            .name("Educational Materials")
            .description(
              "Learning resources to support education across different subjects."
            )
            .build(),
          Category.builder()
            .name("Craft Supplies")
            .description(
              "Various materials for creative and decorative projects."
            )
            .build()
        )
      );
      log.info("Categories seeded successfully.");
    } else {
      log.info("Categories already exist, skipping seeding.");
    }
  }

  /**
   * Seeds initial products into the database.
   * Creates a diverse catalog of products across all categories with realistic
   * pricing.
   * Only executes if the products table is empty and categories exist.
   */
  private void seedProducts() {
    if (productRepository.count() == 0) {
      log.info("Seeding products...");

      List<Category> categories = categoryRepository.findAll();
      if (categories.isEmpty()) {
        log.warn("No categories found for product seeding.");
        return;
      }

      productRepository.saveAll(
        List.of(
          Product.builder()
            .name("Norma College Ruled Notebook")
            .description(
              "College-ruled notebook with 100 sheets, grid layout, hard cover"
            )
            .costPrice(new BigDecimal("3.00"))
            .salePrice(new BigDecimal("4.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("HB Woodcase Pencil")
            .description("HB graphite woodcase pencil with eraser and hexagonal body")
            .costPrice(new BigDecimal("0.25"))
            .salePrice(new BigDecimal("0.49"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Faber-Castell Colored Pencils, 12-Pack")
            .description(
              "Box of 12 colored pencils with durable tips and vibrant colors"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("6.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Faber-Castell Geometry Set")
            .description(
              "Geometry set with 12-inch ruler, triangles, protractor, and precision compass"
            )
            .costPrice(new BigDecimal("4.50"))
            .salePrice(new BigDecimal("7.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Soft Vinyl Eraser")
            .description(
              "Soft vinyl eraser that removes graphite cleanly without smudging"
            )
            .costPrice(new BigDecimal("0.30"))
            .salePrice(new BigDecimal("0.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Metal Desktop Stapler")
            .description(
              "Metal desktop stapler, 20-sheet capacity, black finish"
            )
            .costPrice(new BigDecimal("6.00"))
            .salePrice(new BigDecimal("10.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Two-Hole Paper Punch")
            .description(
              "Metal 2-hole paper punch, 20-sheet capacity, black finish"
            )
            .costPrice(new BigDecimal("7.00"))
            .salePrice(new BigDecimal("12.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Standard Paper Clips, 100-Pack")
            .description(
              "Box of 100 standard metal paper clips, 33 mm, silver finish"
            )
            .costPrice(new BigDecimal("1.00"))
            .salePrice(new BigDecimal("2.49"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Stackable Letter Trays, 3-Pack")
            .description(
              "Set of 3 stackable plastic letter trays for documents, black"
            )
            .costPrice(new BigDecimal("7.50"))
            .salePrice(new BigDecimal("13.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Desktop Tape Dispenser")
            .description(
              "Desktop tape dispenser with weighted, non-slip base"
            )
            .costPrice(new BigDecimal("3.00"))
            .salePrice(new BigDecimal("5.49"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("A4 Copy Paper Ream (500 Sheets)")
            .description(
              "Ream of A4 copy paper, 75 gsm, 500 sheets, bright white"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("7.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Letter-Size Manila Envelopes, 50-Pack")
            .description(
              "Pack of 50 letter-size manila envelopes, kraft color"
            )
            .costPrice(new BigDecimal("3.00"))
            .salePrice(new BigDecimal("5.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("White Cardstock Sheet")
            .description(
              "White cardstock sheet, 27x39 in, 180 gsm, matte finish"
            )
            .costPrice(new BigDecimal("0.25"))
            .salePrice(new BigDecimal("0.75"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Letter-Size Colored Paper Pad")
            .description(
              "Letter-size pad of colored paper, 20 sheets, assorted colors"
            )
            .costPrice(new BigDecimal("2.50"))
            .salePrice(new BigDecimal("4.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Letter-Size Adhesive Paper, 20-Pack")
            .description(
              "Pack of 20 white letter-size adhesive paper sheets"
            )
            .costPrice(new BigDecimal("5.00"))
            .salePrice(new BigDecimal("8.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Assorted Greeting Card with Envelope")
            .description(
              "Greeting card with envelope, assorted designs for any occasion"
            )
            .costPrice(new BigDecimal("1.00"))
            .salePrice(new BigDecimal("2.49"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Assorted Gift Wrap Sheet")
            .description(
              "Gift wrap sheet, 27x39 in, assorted designs"
            )
            .costPrice(new BigDecimal("0.75"))
            .salePrice(new BigDecimal("1.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Small Anime Figurine")
            .description(
              "Decorative anime character figurine, 4 in tall, assorted characters"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("7.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Decorative Gift Box, Medium")
            .description(
              "Medium decorative gift box with bow, suitable for gifts"
            )
            .costPrice(new BigDecimal("2.00"))
            .salePrice(new BigDecimal("4.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Motivational Quote Cards, 10-Pack")
            .description(
              "Set of 10 small cards with inspirational messages, assorted designs"
            )
            .costPrice(new BigDecimal("2.50"))
            .salePrice(new BigDecimal("4.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("16GB USB 2.0 Flash Drive")
            .description(
              "16 GB USB 2.0 flash drive with durable plastic housing"
            )
            .costPrice(new BigDecimal("5.00"))
            .salePrice(new BigDecimal("9.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Wired Optical USB Mouse")
            .description(
              "Wired optical USB mouse, 1000 DPI resolution, ergonomic design"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("7.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("14-Inch Laptop Sleeve")
            .description(
              "Padded, water-resistant sleeve for 14-inch laptops"
            )
            .costPrice(new BigDecimal("6.00"))
            .salePrice(new BigDecimal("11.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Wired On-Ear Headphones")
            .description(
              "Wired on-ear headphones with 3.5 mm connector and in-line volume control"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("7.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("HDMI to VGA Adapter")
            .description(
              "HDMI to VGA adapter compatible with PCs and mobile devices"
            )
            .costPrice(new BigDecimal("6.00"))
            .salePrice(new BigDecimal("11.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Watercolor Paint Set, 12 Colors")
            .description(
              "Set of 12 watercolor pans with brush, assorted vibrant colors"
            )
            .costPrice(new BigDecimal("5.00"))
            .salePrice(new BigDecimal("9.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("A4 Watercolor Paper Pad")
            .description(
              "A4 watercolor paper pad, 10 sheets, 140 lb (300 gsm), cold press"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("7.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Artist Brush Set, 5-Pack")
            .description(
              "Set of 5 assorted-size brushes for wet media techniques"
            )
            .costPrice(new BigDecimal("3.00"))
            .salePrice(new BigDecimal("5.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Stretched Canvas 12x16 in")
            .description(
              "Pre-primed cotton canvas on wooden frame, 12x16 in"
            )
            .costPrice(new BigDecimal("6.00"))
            .salePrice(new BigDecimal("10.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Professional Drawing Pencils, 6-Pack")
            .description(
              "Set of 6 graphite drawing pencils in assorted hardness grades"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("7.49"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Ballpoint Pens, 12-Pack")
            .description(
              "Box of 12 medium-point blue ballpoint pens"
            )
            .costPrice(new BigDecimal("2.50"))
            .salePrice(new BigDecimal("4.49"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Classic Fountain Pen")
            .description(
              "Metal-body fountain pen with refillable cartridge and fine nib"
            )
            .costPrice(new BigDecimal("7.00"))
            .salePrice(new BigDecimal("13.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Permanent Markers, 4-Pack")
            .description(
              "Set of 4 permanent markers, assorted basic colors, chisel tip"
            )
            .costPrice(new BigDecimal("3.00"))
            .salePrice(new BigDecimal("5.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Neon Highlighters, 5-Pack")
            .description(
              "Set of 5 neon highlighters, chisel tip, fluorescent ink"
            )
            .costPrice(new BigDecimal("3.00"))
            .salePrice(new BigDecimal("5.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Liquid Correction Pen")
            .description(
              "Liquid correction pen with fast-drying formula and precision metal tip"
            )
            .costPrice(new BigDecimal("1.50"))
            .salePrice(new BigDecimal("2.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("5-Subject Spiral Notebook")
            .description(
              "5-subject spiral notebook, 200 sheets, color dividers"
            )
            .costPrice(new BigDecimal("5.00"))
            .salePrice(new BigDecimal("8.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Pocket Grid Notebook")
            .description(
              "Pocket-size grid notebook, 80 sheets, hard cover, 3.5x5.5 in"
            )
            .costPrice(new BigDecimal("1.50"))
            .salePrice(new BigDecimal("2.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Executive Daily Planner")
            .description(
              "Executive daily planner, one page per day, hard cover, ribbon marker"
            )
            .costPrice(new BigDecimal("7.00"))
            .salePrice(new BigDecimal("12.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Neon Sticky Notes Block")
            .description(
              "Block of sticky notes in 5 neon colors, 100 sheets per color"
            )
            .costPrice(new BigDecimal("2.00"))
            .salePrice(new BigDecimal("3.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Personal Diary with Lock")
            .description(
              "Personal diary with lock, padded cover, 200 pages, 6x8 in"
            )
            .costPrice(new BigDecimal("6.00"))
            .salePrice(new BigDecimal("11.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Letter-Size Lever Arch File")
            .description(
              "Letter-size lever arch file with wide spine and metal mechanism"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("7.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Plastic File Folder with Fastener")
            .description(
              "Plastic file folder with fastener, letter-size, assorted colors"
            )
            .costPrice(new BigDecimal("1.50"))
            .salePrice(new BigDecimal("2.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Plastic Divider Set, 5 Tabs")
            .description(
              "Set of 5 plastic dividers, letter-size, assorted colors"
            )
            .costPrice(new BigDecimal("1.25"))
            .salePrice(new BigDecimal("2.49"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Clear Plastic Storage Box, 5L")
            .description(
              "Clear plastic storage box with lid, 5-liter capacity"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("7.49"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Basic School Backpack")
            .description(
              "Basic school backpack with two compartments and side pocket"
            )
            .costPrice(new BigDecimal("12.00"))
            .salePrice(new BigDecimal("24.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(9))
            .build(),
          Product.builder()
            .name("Simple Fabric Pencil Case")
            .description(
              "Fabric pencil case with zipper, holds up to 10 pencils, assorted designs"
            )
            .costPrice(new BigDecimal("2.00"))
            .salePrice(new BigDecimal("3.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(9))
            .build(),
          Product.builder()
            .name("Color Counting Abacus")
            .description(
              "Wooden abacus with colorful beads, 10 rows of 10 beads each"
            )
            .costPrice(new BigDecimal("6.00"))
            .salePrice(new BigDecimal("11.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(10))
            .build(),
          Product.builder()
            .name("Educational World Map Poster")
            .description(
              "Laminated educational world map poster, 20x28 in, double-sided"
            )
            .costPrice(new BigDecimal("4.00"))
            .salePrice(new BigDecimal("7.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(10))
            .build(),
          Product.builder()
            .name("Modeling Clay Set, 4 Colors")
            .description(
              "Set of modeling clay in 4 basic colors, non-toxic, 17.6 oz total"
            )
            .costPrice(new BigDecimal("3.50"))
            .salePrice(new BigDecimal("6.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(11))
            .build(),
          Product.builder()
            .name("Basic Jewelry Craft Kit")
            .description(
              "Jewelry-making kit with assorted beads, cords, and tools"
            )
            .costPrice(new BigDecimal("7.00"))
            .salePrice(new BigDecimal("13.99"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(11))
            .build()
        )
      );
      log.info("Products seeded successfully.");
    } else {
      log.info("Products already exist, skipping seeding.");
    }
  }

  /**
   * Seeds initial user accounts into the database.
   * Creates default administrative and employee accounts with secure passwords.
   * Only executes if the users table is empty.
   */
  private void seedUsers() {
    if (userRepository.count() == 0) {
      log.info("Seeding users...");

      User adminUser = User.builder()
        .name("Admin User")
        .email("admin@sigrap.com")
        .password(passwordEncoder.encode("Admin123*"))
        .role(UserRole.ADMINISTRATOR)
        .documentId("100000001")
        .phone("+1-555-0100")
        .build();

      User employeeUser = User.builder()
        .name("Employee User")
        .email("employee@sigrap.com")
        .password(passwordEncoder.encode("Employee123*"))
        .role(UserRole.EMPLOYEE)
        .documentId("100000002")
        .phone("+1-555-0101")
        .build();

      userRepository.saveAll(List.of(adminUser, employeeUser));
      log.info("Users seeded successfully.");
    } else {
      log.info("Users already exist, skipping seeding.");
    }
  }

  private void seedSchedules() {
    if (scheduleRepository.count() > 0) {
      log.info("Schedules already seeded.");
      return;
    }

    log.info("Seeding schedules...");

    List<Schedule> schedules = new ArrayList<>();
    List<User> users = userRepository.findAll();

    LocalTime defaultStartTime = LocalTime.of(8, 0);
    LocalTime defaultEndTime = LocalTime.of(17, 0);
    LocalTime saturdayEndTime = LocalTime.of(13, 0);
    LocalTime extraHoursStartTime = LocalTime.of(18, 0);
    LocalTime extraHoursEndTime = LocalTime.of(21, 0);

    for (User user : users) {
      if (!"employee@sigrap.com".equals(user.getEmail())) {
        continue;
      }

      schedules.add(
        Schedule.builder()
          .user(user)
          .day("Monday")
          .startTime(defaultStartTime)
          .endTime(defaultEndTime)
          .type("Regular")
          .isActive(true)
          .build()
      );

      schedules.add(
        Schedule.builder()
          .user(user)
          .day("Tuesday")
          .startTime(defaultStartTime)
          .endTime(defaultEndTime)
          .type("Regular")
          .isActive(true)
          .build()
      );

      schedules.add(
        Schedule.builder()
          .user(user)
          .day("Wednesday")
          .startTime(defaultStartTime)
          .endTime(defaultEndTime)
          .type("Regular")
          .isActive(true)
          .build()
      );

      schedules.add(
        Schedule.builder()
          .user(user)
          .day("Thursday")
          .startTime(defaultStartTime)
          .endTime(defaultEndTime)
          .type("Regular")
          .isActive(true)
          .build()
      );

      schedules.add(
        Schedule.builder()
          .user(user)
          .day("Friday")
          .startTime(defaultStartTime)
          .endTime(defaultEndTime)
          .type("Regular")
          .isActive(true)
          .build()
      );

      schedules.add(
        Schedule.builder()
          .user(user)
          .day("Saturday")
          .startTime(defaultStartTime)
          .endTime(saturdayEndTime)
          .type("Weekend")
          .isActive(true)
          .build()
      );

      schedules.add(
        Schedule.builder()
          .user(user)
          .day("Wednesday")
          .startTime(extraHoursStartTime)
          .endTime(extraHoursEndTime)
          .type("Overtime")
          .isActive(true)
          .build()
      );
    }

    scheduleRepository.saveAll(schedules);
    log.info("Schedules seeded successfully.");
  }

  private void seedAttendance() {
    if (attendanceRepository.count() > 0) {
      log.info("Attendance records already seeded.");
      return;
    }

    log.info("Seeding attendance records...");

    List<Attendance> attendanceRecords = new ArrayList<>();
    List<User> users = userRepository.findAll();
    LocalDateTime now = LocalDateTime.now();

    User targetEmployee = users
      .stream()
      .filter(user -> "employee@sigrap.com".equals(user.getEmail()))
      .findFirst()
      .orElse(null);

    if (targetEmployee == null) {
      log.warn("Target employee not found, skipping attendance seeding.");
      return;
    }

    LocalDateTime lastWeekMonday = now
      .minusWeeks(1)
      .with(
        java.time.temporal.TemporalAdjusters.previousOrSame(
          java.time.DayOfWeek.MONDAY
        )
      );

    LocalDateTime mondayClockIn = lastWeekMonday.withHour(8).withMinute(5);
    LocalDateTime mondayClockOut = lastWeekMonday.withHour(17).withMinute(10);
    double mondayHours = calculateHoursWorked(mondayClockIn, mondayClockOut);

    attendanceRecords.add(
      Attendance.builder()
        .user(targetEmployee)
        .date(lastWeekMonday.toLocalDate().atStartOfDay())
        .clockInTime(mondayClockIn)
        .clockOutTime(mondayClockOut)
        .totalHours(mondayHours)
        .status(AttendanceStatus.PRESENT)
        .build()
    );

    LocalDateTime lastWeekTuesday = lastWeekMonday.plusDays(1);
    LocalDateTime tuesdayClockIn = lastWeekTuesday.withHour(8).withMinute(45);
    LocalDateTime tuesdayClockOut = lastWeekTuesday.withHour(17).withMinute(15);
    double tuesdayHours = calculateHoursWorked(tuesdayClockIn, tuesdayClockOut);

    attendanceRecords.add(
      Attendance.builder()
        .user(targetEmployee)
        .date(lastWeekTuesday.toLocalDate().atStartOfDay())
        .clockInTime(tuesdayClockIn)
        .clockOutTime(tuesdayClockOut)
        .totalHours(tuesdayHours)
        .status(AttendanceStatus.LATE)
        .build()
    );

    LocalDateTime lastWeekWednesday = lastWeekTuesday.plusDays(1);
    LocalDateTime wednesdayClockIn = lastWeekWednesday
      .withHour(8)
      .withMinute(0);
    LocalDateTime wednesdayClockOut = lastWeekWednesday
      .withHour(15)
      .withMinute(30);
    double wednesdayHours = calculateHoursWorked(
      wednesdayClockIn,
      wednesdayClockOut
    );

    attendanceRecords.add(
      Attendance.builder()
        .user(targetEmployee)
        .date(lastWeekWednesday.toLocalDate().atStartOfDay())
        .clockInTime(wednesdayClockIn)
        .clockOutTime(wednesdayClockOut)
        .totalHours(wednesdayHours)
        .status(AttendanceStatus.EARLY_DEPARTURE)
        .build()
    );

    LocalDateTime lastWeekThursday = lastWeekWednesday.plusDays(1);
    LocalDateTime thursdayClockIn = lastWeekThursday.withHour(7).withMinute(55);
    LocalDateTime thursdayClockOut = lastWeekThursday
      .withHour(17)
      .withMinute(5);
    double thursdayHours = calculateHoursWorked(
      thursdayClockIn,
      thursdayClockOut
    );

    attendanceRecords.add(
      Attendance.builder()
        .user(targetEmployee)
        .date(lastWeekThursday.toLocalDate().atStartOfDay())
        .clockInTime(thursdayClockIn)
        .clockOutTime(thursdayClockOut)
        .totalHours(thursdayHours)
        .status(AttendanceStatus.PRESENT)
        .build()
    );

    LocalDateTime lastWeekFriday = lastWeekThursday.plusDays(1);

    attendanceRecords.add(
      Attendance.builder()
        .user(targetEmployee)
        .date(lastWeekFriday.toLocalDate().atStartOfDay())
        .clockInTime(null)
        .clockOutTime(null)
        .totalHours(null)
        .status(AttendanceStatus.ON_LEAVE)
        .build()
    );

    attendanceRepository.saveAll(attendanceRecords);
    log.info(
      "Attendance records seeded successfully ({} records).",
      attendanceRecords.size()
    );
  }

  /**
   * Calculate hours worked between clock-in and clock-out times.
   *
   * @param clockIn Clock-in time
   * @param clockOut Clock-out time
   * @return Hours worked as a double with 2 decimal places
   */
  private double calculateHoursWorked(
    LocalDateTime clockIn,
    LocalDateTime clockOut
  ) {
    if (clockIn == null || clockOut == null) {
      return 0.0;
    }

    Duration duration = Duration.between(clockIn, clockOut);
    double hours = duration.toMinutes() / 60.0;

    return Math.round(hours * 100.0) / 100.0;
  }

  /**
   * Seeds initial suppliers into the database.
   * Creates a diverse set of suppliers with realistic business information.
   * Only executes if the suppliers table is empty.
   */
  private void seedSuppliers() {
    if (supplierRepository.count() == 0) {
      log.info("Seeding suppliers...");

      List<Supplier> suppliers = new ArrayList<>();

      suppliers.add(
        Supplier.builder()
          .name("Office Depot")
          .contactPerson("Michael Sanders")
          .email("msanders@officedepot.com")
          .phone("(214) 555-2700")
          .address("123 Market St, Dallas, TX 75201")
          .website("https://www.officedepot.com")
          .paymentTerms("Net 30 days")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Downtown Stationery")
          .contactPerson("Amy Campbell")
          .email("amy.campbell@downtownstationery.com")
          .phone("(312) 555-1900")
          .address("200 W Lake St, Chicago, IL 60606")
          .website("https://www.downtownstationery.com")
          .paymentTerms("Net 15 days")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Artisan Art Supplies")
          .contactPerson("Robert Gutierrez")
          .email("robert.gutierrez@artisanart.com")
          .phone("(206) 555-4400")
          .address("410 Pine St, Seattle, WA 98101")
          .website("https://www.artisanart.com")
          .paymentTerms("Net 45 days")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Scribe Paper Co.")
          .contactPerson("Mary Torres")
          .email("mary.torres@scribepaper.com")
          .phone("(303) 555-6600")
          .address("1500 Blake St, Denver, CO 80202")
          .website("https://www.scribepaper.com")
          .paymentTerms("Net 30 days")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Faber-Castell USA")
          .contactPerson("George Reategui")
          .email("george.reategui@faber-castell.com")
          .phone("(216) 555-4800")
          .address("500 Artway Dr, Cleveland, OH 44115")
          .website("https://www.faber-castell.com")
          .paymentTerms("Net 45 days")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("QuickBond Adhesives")
          .contactPerson("Lucy Perez")
          .email("lucy.perez@quickbond.com")
          .phone("(305) 555-2300")
          .address("890 Industrial Rd, Miami, FL 33101")
          .website("https://www.quickbond.com")
          .paymentTerms("Cash on delivery")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Norma USA")
          .contactPerson("Daniel Quinn")
          .email("daniel.quinn@normausa.com")
          .phone("(973) 555-8000")
          .address("250 Stationery Blvd, Newark, NJ 07102")
          .website("https://www.normausa.com")
          .paymentTerms("Net 30 days")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("DistriOffice USA")
          .contactPerson("Patricia Rogers")
          .email("patricia.rogers@distriofficeusa.com")
          .phone("(213) 555-1800")
          .address("4100 Sunset Blvd, Los Angeles, CA 90029")
          .website("https://www.distriofficeusa.com")
          .paymentTerms("Net 45 days")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Pilot USA")
          .contactPerson("Carlos Mendoza")
          .email("carlos.mendoza@pilotusa.com")
          .phone("(818) 555-5200")
          .address("100 Pilot Plaza, Burbank, CA 91502")
          .website("https://www.pilotpen.us")
          .paymentTerms("Net 30 days")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("National Paper Co.")
          .contactPerson("Fernando Torres")
          .email("fernando.torres@nationalpaper.com")
          .phone("(412) 555-9000")
          .address("75 Mill Rd, Pittsburgh, PA 15222")
          .website("https://www.nationalpaper.com")
          .paymentTerms("Net 60 days")
          .build()
      );

      supplierRepository.saveAll(suppliers);
      log.info("Suppliers seeded successfully.");
    } else {
      log.info("Suppliers already exist, skipping seeding.");
    }
  }

  private void seedPurchaseOrders() {
    if (purchaseOrderRepository.count() > 0) {
      log.info("Purchase orders already exist, skipping seeding.");
      return;
    }

    log.info("Seeding purchase orders...");

    List<Supplier> suppliers = supplierRepository.findAll();
    if (suppliers.isEmpty()) {
      log.warn("No suppliers found. Skipping purchase order seeding.");
      return;
    }

    List<Product> products = productRepository.findAll();
    if (products.isEmpty()) {
      log.warn("No products found. Skipping purchase order seeding.");
      return;
    }

    Supplier downtownStationerySupplier = findSupplierByName(
      suppliers,
      "Downtown Stationery"
    );
    Supplier faberCastellSupplier = findSupplierByName(
      suppliers,
      "Faber-Castell USA"
    );
    Supplier artisanArtSuppliesSupplier = findSupplierByName(
      suppliers,
      "Artisan Art Supplies"
    );
    Supplier officeDepotSupplier = findSupplierByName(
      suppliers,
      "Office Depot"
    );
    Supplier normaUsaSupplier = findSupplierByName(suppliers, "Norma USA");

    Product normaNotebook = findProductByName(
      products,
      "Norma College Ruled Notebook"
    );
    Product hbWoodcasePencil = findProductByName(
      products,
      "HB Woodcase Pencil"
    );
    Product coloredPencils12Pack = findProductByName(
      products,
      "Faber-Castell Colored Pencils, 12-Pack"
    );
    Product geometrySet = findProductByName(
      products,
      "Faber-Castell Geometry Set"
    );
    Product softVinylEraser = findProductByName(
      products,
      "Soft Vinyl Eraser"
    );
    Product a4CopyPaperReam = findProductByName(
      products,
      "A4 Copy Paper Ream (500 Sheets)"
    );
    Product whiteCardstockSheet = findProductByName(
      products,
      "White Cardstock Sheet"
    );
    Product coloredPaperPad = findProductByName(
      products,
      "Letter-Size Colored Paper Pad"
    );
    Product permanentMarkers4Pack = findProductByName(
      products,
      "Permanent Markers, 4-Pack"
    );
    Product spiralNotebook5Subject = findProductByName(
      products,
      "5-Subject Spiral Notebook"
    );

    List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    PurchaseOrder order1 = PurchaseOrder.builder()
      .supplier(downtownStationerySupplier)
      .deliveryDate(LocalDate.now().minusDays(34))
      .status(PurchaseOrderStatus.DELIVERED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item11 = PurchaseOrderItem.builder()
      .product(normaNotebook)
      .quantity(100)
      .unitPrice(new BigDecimal("3.00"))
      .receivedQuantity(100)
      .build();

    PurchaseOrderItem item12 = PurchaseOrderItem.builder()
      .product(hbWoodcasePencil)
      .quantity(200)
      .unitPrice(new BigDecimal("0.25"))
      .receivedQuantity(200)
      .build();

    PurchaseOrderItem item13 = PurchaseOrderItem.builder()
      .product(softVinylEraser)
      .quantity(150)
      .unitPrice(new BigDecimal("0.30"))
      .receivedQuantity(150)
      .build();

    item11.setPurchaseOrder(order1);
    item12.setPurchaseOrder(order1);
    item13.setPurchaseOrder(order1);
    order1.getItems().add(item11);
    order1.getItems().add(item12);
    order1.getItems().add(item13);

    BigDecimal total1 = calculateOrderTotal(order1);
    order1.setTotalAmount(total1);
    purchaseOrders.add(order1);

    PurchaseOrder order2 = PurchaseOrder.builder()
      .supplier(faberCastellSupplier)
      .deliveryDate(LocalDate.now().minusDays(22))
      .status(PurchaseOrderStatus.DELIVERED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item21 = PurchaseOrderItem.builder()
      .product(coloredPencils12Pack)
      .quantity(50)
      .unitPrice(new BigDecimal("4.00"))
      .receivedQuantity(50)
      .build();

    PurchaseOrderItem item22 = PurchaseOrderItem.builder()
      .product(geometrySet)
      .quantity(30)
      .unitPrice(new BigDecimal("4.50"))
      .receivedQuantity(30)
      .build();

    item21.setPurchaseOrder(order2);
    item22.setPurchaseOrder(order2);
    order2.getItems().add(item21);
    order2.getItems().add(item22);

    BigDecimal total2 = calculateOrderTotal(order2);
    order2.setTotalAmount(total2);
    purchaseOrders.add(order2);

    PurchaseOrder order3 = PurchaseOrder.builder()
      .supplier(officeDepotSupplier)
      .deliveryDate(LocalDate.now().minusDays(8))
      .status(PurchaseOrderStatus.DELIVERED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item31 = PurchaseOrderItem.builder()
      .product(a4CopyPaperReam)
      .quantity(80)
      .unitPrice(new BigDecimal("4.00"))
      .receivedQuantity(80)
      .build();

    PurchaseOrderItem item32 = PurchaseOrderItem.builder()
      .product(whiteCardstockSheet)
      .quantity(120)
      .unitPrice(new BigDecimal("0.25"))
      .receivedQuantity(120)
      .build();

    PurchaseOrderItem item33 = PurchaseOrderItem.builder()
      .product(coloredPaperPad)
      .quantity(60)
      .unitPrice(new BigDecimal("2.50"))
      .receivedQuantity(60)
      .build();

    item31.setPurchaseOrder(order3);
    item32.setPurchaseOrder(order3);
    item33.setPurchaseOrder(order3);
    order3.getItems().add(item31);
    order3.getItems().add(item32);
    order3.getItems().add(item33);

    BigDecimal total3 = calculateOrderTotal(order3);
    order3.setTotalAmount(total3);
    purchaseOrders.add(order3);

    PurchaseOrder order4 = PurchaseOrder.builder()
      .supplier(artisanArtSuppliesSupplier)
      .deliveryDate(LocalDate.now().plusDays(3))
      .status(PurchaseOrderStatus.CONFIRMED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item41 = PurchaseOrderItem.builder()
      .product(permanentMarkers4Pack)
      .quantity(40)
      .unitPrice(new BigDecimal("3.00"))
      .receivedQuantity(0)
      .build();

    item41.setPurchaseOrder(order4);
    order4.getItems().add(item41);

    BigDecimal total4 = calculateOrderTotal(order4);
    order4.setTotalAmount(total4);
    purchaseOrders.add(order4);

    PurchaseOrder order5 = PurchaseOrder.builder()
      .supplier(normaUsaSupplier)
      .deliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.SUBMITTED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item51 = PurchaseOrderItem.builder()
      .product(normaNotebook)
      .quantity(50)
      .unitPrice(new BigDecimal("3.00"))
      .receivedQuantity(0)
      .build();

    PurchaseOrderItem item52 = PurchaseOrderItem.builder()
      .product(spiralNotebook5Subject)
      .quantity(35)
      .unitPrice(new BigDecimal("5.00"))
      .receivedQuantity(0)
      .build();

    item51.setPurchaseOrder(order5);
    item52.setPurchaseOrder(order5);
    order5.getItems().add(item51);
    order5.getItems().add(item52);

    BigDecimal total5 = calculateOrderTotal(order5);
    order5.setTotalAmount(total5);
    purchaseOrders.add(order5);

    PurchaseOrder order6 = PurchaseOrder.builder()
      .supplier(downtownStationerySupplier)
      .deliveryDate(LocalDate.now().plusDays(15))
      .status(PurchaseOrderStatus.DRAFT)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item61 = PurchaseOrderItem.builder()
      .product(hbWoodcasePencil)
      .quantity(150)
      .unitPrice(new BigDecimal("0.25"))
      .receivedQuantity(0)
      .build();

    PurchaseOrderItem item62 = PurchaseOrderItem.builder()
      .product(softVinylEraser)
      .quantity(100)
      .unitPrice(new BigDecimal("0.30"))
      .receivedQuantity(0)
      .build();

    item61.setPurchaseOrder(order6);
    item62.setPurchaseOrder(order6);
    order6.getItems().add(item61);
    order6.getItems().add(item62);

    BigDecimal total6 = calculateOrderTotal(order6);
    order6.setTotalAmount(total6);
    purchaseOrders.add(order6);

    purchaseOrderRepository.saveAll(purchaseOrders);
    log.info("{} purchase orders seeded successfully.", purchaseOrders.size());
  }

  /**
   * Find a supplier by name in a list of suppliers.
   *
   * @param suppliers List of suppliers to search in
   * @param name      Name of the supplier to find
   * @return The found supplier or the first supplier if not found
   */
  private Supplier findSupplierByName(List<Supplier> suppliers, String name) {
    return suppliers
      .stream()
      .filter(s -> s.getName().equals(name))
      .findFirst()
      .orElse(suppliers.get(0));
  }

  /**
   * Find a product by name in a list of products.
   *
   * @param products List of products to search in
   * @param name     Name of the product to find
   * @return The found product or the first product if not found
   */
  private Product findProductByName(List<Product> products, String name) {
    return products
      .stream()
      .filter(p -> p.getName().equals(name))
      .findFirst()
      .orElse(products.get(0));
  }

  /**
   * Calculate total amount for an order based on its items.
   *
   * @param order The purchase order
   * @return The total amount
   */
  private BigDecimal calculateOrderTotal(PurchaseOrder order) {
    return order
      .getItems()
      .stream()
      .map(item ->
        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
      )
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Seeds initial customers into the database.
   * Creates a diverse set of customers with realistic personal information.
   * Only executes if the customers table is empty.
   */
  private void seedCustomers() {
    if (customerRepository.count() == 0) {
      log.info("Seeding customers...");

      customerRepository.saveAll(
        List.of(
          Customer.builder()
            .fullName("John Smith")
            .documentId("200000001")
            .email("john.smith@example.com")
            .phoneNumber("(212) 555-0101")
            .address("145 West 34th St, New York, NY 10001")
            .build(),
          Customer.builder()
            .fullName("Emily Johnson")
            .documentId("200000002")
            .email("emily.johnson@example.com")
            .phoneNumber("(415) 555-0102")
            .address("600 Market St Apt 4B, San Francisco, CA 94104")
            .build(),
          Customer.builder()
            .fullName("Michael Brown")
            .documentId("200000003")
            .email("michael.brown@example.com")
            .phoneNumber("(773) 555-0103")
            .address("8200 N Clark St, Chicago, IL 60626")
            .build(),
          Customer.builder()
            .fullName("Sarah Davis")
            .documentId("200000004")
            .email("sarah.davis@example.com")
            .phoneNumber("(305) 555-0104")
            .address("150 Ocean Dr, Miami Beach, FL 33139")
            .build(),
          Customer.builder()
            .fullName("David Wilson")
            .documentId("200000005")
            .email("david.wilson@example.com")
            .phoneNumber("(469) 555-0105")
            .address("420 Main St, Dallas, TX 75202")
            .build(),
          Customer.builder()
            .fullName("Olivia Martinez")
            .documentId("200000006")
            .email("olivia.martinez@example.com")
            .phoneNumber("(602) 555-0106")
            .address("700 E Camelback Rd, Phoenix, AZ 85014")
            .build(),
          Customer.builder()
            .fullName("Andrew Thompson")
            .documentId("200000007")
            .email("andrew.thompson@example.com")
            .phoneNumber("(404) 555-0107")
            .address("250 Peachtree St NE, Atlanta, GA 30303")
            .build(),
          Customer.builder()
            .fullName("Sophia Ramirez")
            .documentId("200000008")
            .email("sophia.ramirez@example.com")
            .phoneNumber("(206) 555-0108")
            .address("900 S Jackson St, Seattle, WA 98104")
            .build(),
          Customer.builder()
            .fullName("Jacob Torres")
            .documentId("200000009")
            .email("jacob.torres@example.com")
            .phoneNumber("(702) 555-0109")
            .address("500 Fremont St, Las Vegas, NV 89101")
            .build(),
          Customer.builder()
            .fullName("Daniela Harris")
            .documentId("200000010")
            .email("daniela.harris@example.com")
            .phoneNumber("(303) 555-0110")
            .address("1800 Larimer St, Denver, CO 80202")
            .build(),
          Customer.builder()
            .fullName("Sebastian Carter")
            .documentId("200000011")
            .email("sebastian.carter@example.com")
            .phoneNumber("(512) 555-0111")
            .address("400 Congress Ave, Austin, TX 78701")
            .build(),
          Customer.builder()
            .fullName("Victoria Ortiz")
            .documentId("200000012")
            .email("victoria.ortiz@example.com")
            .phoneNumber("(215) 555-0112")
            .address("700 S 4th St, Philadelphia, PA 19147")
            .build(),
          Customer.builder()
            .fullName("Cameron Reyes")
            .documentId("200000013")
            .email("cameron.reyes@example.com")
            .phoneNumber("(916) 555-0113")
            .address("500 J St, Sacramento, CA 95814")
            .build(),
          Customer.builder()
            .fullName("Isabella Vargas")
            .documentId("200000014")
            .email("isabella.vargas@example.com")
            .phoneNumber("(713) 555-0114")
            .address("1200 McKinney St, Houston, TX 77010")
            .build(),
          Customer.builder()
            .fullName("Samuel Morris")
            .documentId("200000015")
            .email("samuel.morris@example.com")
            .phoneNumber("(617) 555-0115")
            .address("800 Boylston St, Boston, MA 02199")
            .build()
        )
      );

      log.info("Successfully seeded 15 customers");
    }
  }

  /**
   * Seeds initial sales into the database.
   * Creates a variety of sales with different customers, employees, products, and
   * dates.
   * Only executes if the sales table is empty.
   */
  private void seedSales() {
    if (saleRepository.count() > 0) {
      log.info("Sales already exist, skipping seeding.");
      return;
    }
    log.info("Seeding sales...");

    List<Product> products = productRepository.findAll();
    if (products.isEmpty()) {
      log.warn("No products found, skipping sales seeding.");
      return;
    }

    List<Customer> customers = customerRepository.findAll();

    if (customers.isEmpty()) {
      log.warn(
        "No customers found, skipping sales seeding as customer is now mandatory for sales."
      );
      return;
    }

    List<User> employees = userRepository
      .findAll()
      .stream()
      .filter(
        user ->
          user.getRole() == UserRole.EMPLOYEE ||
          user.getRole() == UserRole.ADMINISTRATOR
      )
      .toList();

    if (employees.isEmpty()) {
      log.warn(
        "No employees (EMPLOYEE or ADMINISTRATOR role) found, skipping sales seeding."
      );
      return;
    }

    List<Sale> salesToCreate = new ArrayList<>();
    int numberOfSales = random.nextInt(151) + 50;

    for (int i = 0; i < numberOfSales; i++) {
      Sale.SaleBuilder saleBuilder = Sale.builder();

      User assignedEmployee = employees.get(random.nextInt(employees.size()));
      saleBuilder.employee(assignedEmployee);

      saleBuilder.customer(customers.get(random.nextInt(customers.size())));

      LocalDateTime saleDateTime = LocalDateTime.now()
        .minusDays(random.nextInt(180))
        .minusHours(random.nextInt(24))
        .minusMinutes(random.nextInt(60));
      saleBuilder
        .createdAt(saleDateTime)
        .updatedAt(saleDateTime.plusMinutes(random.nextInt(30)));

      List<SaleItem> saleItems = new ArrayList<>();
      BigDecimal totalAmount = BigDecimal.ZERO;
      int numberOfItemsInSale = random.nextInt(8) + 1;

      for (int j = 0; j < numberOfItemsInSale; j++) {
        Product product = products.get(random.nextInt(products.size()));
        int quantity = random.nextInt(5) + 1;

        if (product.getStock() < quantity) {
          if (product.getStock() > 0) {
            quantity = product.getStock();
          } else {
            continue;
          }
        }

        product.setStock(product.getStock() - quantity);

        SaleItem saleItem = SaleItem.builder()
          .product(product)
          .quantity(quantity)
          .unitPrice(product.getSalePrice())
          .build();
        saleItem.calculateSubtotal();
        saleItems.add(saleItem);
        totalAmount = totalAmount.add(saleItem.getSubtotal());
      }

      if (saleItems.isEmpty()) {
        continue;
      }

      saleBuilder.items(saleItems);
      saleBuilder.totalAmount(totalAmount);

      BigDecimal discountAmount = BigDecimal.ZERO;
      double discountChance = random.nextDouble() * 0.4 + 0.1;
      if (random.nextDouble() < discountChance) {
        double discountPercentage = (random.nextInt(20) + 1) / 100.0;
        discountAmount = totalAmount.multiply(
          BigDecimal.valueOf(discountPercentage)
        );
        discountAmount = discountAmount.setScale(
          2,
          java.math.RoundingMode.HALF_UP
        );
      }
      saleBuilder.discountAmount(discountAmount);

      BigDecimal taxableAmount = totalAmount.subtract(discountAmount);
      BigDecimal taxAmount = taxableAmount.multiply(BigDecimal.valueOf(0.19));
      taxAmount = taxAmount.setScale(2, java.math.RoundingMode.HALF_UP);
      saleBuilder.taxAmount(taxAmount);

      BigDecimal finalAmount = taxableAmount.add(taxAmount);
      saleBuilder.finalAmount(finalAmount);

      Sale sale = saleBuilder.build();

      for (SaleItem item : saleItems) {
        item.setSale(sale);
      }
      salesToCreate.add(sale);
    }

    if (!salesToCreate.isEmpty()) {
      saleRepository.saveAll(salesToCreate);

      productRepository.saveAll(products);
      log.info("Successfully seeded {} sales.", salesToCreate.size());
    } else {
      log.info("No sales were generated to seed.");
    }
  }

  /**
   * Seeds initial sales returns into the database.
   * Creates a few sample returns based on existing sales and products.
   * Only executes if the sales_returns table is empty and sales exist.
   */
  private void seedSaleReturns() {
    if (saleReturnRepository.count() > 0) {
      log.info("Sale returns already exist, skipping seeding.");
      return;
    }

    List<Sale> sales = saleRepository.findAll();
    if (sales.isEmpty()) {
      log.warn("No sales found, skipping sales returns seeding.");
      return;
    }

    List<User> employees = userRepository
      .findAll()
      .stream()
      .filter(
        user ->
          user.getRole() == UserRole.EMPLOYEE ||
          user.getRole() == UserRole.ADMINISTRATOR
      )
      .toList();

    if (employees.isEmpty()) {
      log.warn("No employees found for seeding returns, skipping.");
      return;
    }

    log.info("Seeding sales returns...");
    List<SaleReturn> returnsToCreate = new ArrayList<>();
    int numberOfReturnsToSeed = Math.min(sales.size(), 5);

    for (int i = 0; i < numberOfReturnsToSeed; i++) {
      Sale originalSale = sales.get(random.nextInt(sales.size()));
      if (originalSale.getItems().isEmpty()) {
        continue;
      }

      SaleItem itemToReturn = originalSale
        .getItems()
        .get(random.nextInt(originalSale.getItems().size()));
      int quantityToReturn = 1;
      if (itemToReturn.getQuantity() > 1) {
        quantityToReturn = random.nextInt(itemToReturn.getQuantity()) + 1;
      }

      Product productBeingReturned = itemToReturn.getProduct();

      SaleReturn.SaleReturnBuilder returnBuilder = SaleReturn.builder();
      returnBuilder.originalSale(originalSale);
      returnBuilder.customer(originalSale.getCustomer());
      returnBuilder.employee(employees.get(random.nextInt(employees.size())));
      returnBuilder.reason(getRandomReturnReason());
      returnBuilder.createdAt(
        originalSale.getCreatedAt().plusDays(random.nextInt(5) + 1)
      );

      BigDecimal itemSubtotal = itemToReturn
        .getUnitPrice()
        .multiply(BigDecimal.valueOf(quantityToReturn));
      returnBuilder.totalReturnAmount(itemSubtotal);

      SaleReturn saleReturn = returnBuilder.build();

      SaleReturnItem returnItem = SaleReturnItem.builder()
        .saleReturn(saleReturn)
        .product(productBeingReturned)
        .quantity(quantityToReturn)
        .unitPrice(itemToReturn.getUnitPrice())
        .subtotal(itemSubtotal)
        .build();

      saleReturn.addItem(returnItem);

      returnsToCreate.add(saleReturn);
    }

    if (!returnsToCreate.isEmpty()) {
      saleReturnRepository.saveAll(returnsToCreate);
      log.info("Successfully seeded {} sales returns.", returnsToCreate.size());
    } else {
      log.info("No sales returns were generated to seed.");
    }
  }

  private String getRandomReturnReason() {
    String[] reasons = {
      "Defective product",
      "Incorrect size",
      "Customer changed mind",
      "Item damaged during shipping",
      "Did not meet expectations",
    };
    return reasons[random.nextInt(reasons.length)];
  }

  /**
   * Seeds initial audit logs into the database.
   * Creates a historical record of user actions.
   * Only executes if the audit_logs table is empty.
   */
  private void seedAuditLogs() {
    if (auditLogRepository.count() > 0) {
      log.info("Audit logs already exist, skipping seeding.");
      return;
    }

    log.info("Seeding audit logs...");
    List<User> users = userRepository.findAll();
    if (users.isEmpty()) {
      log.warn("No users found, skipping audit log seeding.");
      return;
    }

    List<AuditLog> auditLogs = new ArrayList<>();

    User adminUser = users
      .stream()
      .filter(u -> "admin@sigrap.com".equals(u.getEmail()))
      .findFirst()
      .orElse(null);

    User employeeUser = users
      .stream()
      .filter(u -> "employee@sigrap.com".equals(u.getEmail()))
      .findFirst()
      .orElse(null);

    if (adminUser != null) {
      auditLogs.add(
        AuditLog.builder()
          .username(adminUser.getEmail())
          .action("LOGIN")
          .entityName("USER")
          .timestamp(LocalDateTime.now().minusDays(7).withHour(8).withMinute(0))
          .build()
      );

      auditLogs.add(
        AuditLog.builder()
          .username(adminUser.getEmail())
          .action("CREATE")
          .entityName("PRODUCT")
          .timestamp(
            LocalDateTime.now().minusDays(7).withHour(9).withMinute(15)
          )
          .build()
      );

      auditLogs.add(
        AuditLog.builder()
          .username(adminUser.getEmail())
          .action("UPDATE")
          .entityName("SUPPLIER")
          .timestamp(
            LocalDateTime.now().minusDays(6).withHour(10).withMinute(30)
          )
          .build()
      );
    }

    if (employeeUser != null) {
      auditLogs.add(
        AuditLog.builder()
          .username(employeeUser.getEmail())
          .action("LOGIN")
          .entityName("USER")
          .timestamp(
            LocalDateTime.now().minusDays(7).withHour(8).withMinute(30)
          )
          .build()
      );

      auditLogs.add(
        AuditLog.builder()
          .username(employeeUser.getEmail())
          .action("CREATE")
          .entityName("SALE")
          .timestamp(
            LocalDateTime.now().minusDays(7).withHour(9).withMinute(45)
          )
          .build()
      );

      auditLogs.add(
        AuditLog.builder()
          .username(employeeUser.getEmail())
          .action("UPDATE")
          .entityName("PRODUCT")
          .timestamp(
            LocalDateTime.now().minusDays(7).withHour(9).withMinute(45)
          )
          .build()
      );
    }

    auditLogRepository.saveAll(auditLogs);
    log.info("Successfully seeded {} audit logs.", auditLogs.size());
  }
}
