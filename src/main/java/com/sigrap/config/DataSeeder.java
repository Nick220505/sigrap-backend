package com.sigrap.config;

// Audit module - hexagonal architecture
import com.sigrap.audit.domain.model.AuditAction;
import com.sigrap.audit.domain.model.AuditLog;
import com.sigrap.audit.domain.model.AuditStatus;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.audit.domain.port.AuditLogRepositoryPort;

// Category module - hexagonal architecture
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;

// Customer module - hexagonal architecture
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.model.CustomerName;
import com.sigrap.customer.domain.model.CustomerEmail;
import com.sigrap.customer.domain.model.CustomerPhone;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;

// Employee module - hexagonal architecture
import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceStatus;
import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.port.AttendanceRepositoryPort;
import com.sigrap.employee.domain.port.ScheduleRepositoryPort;

// Product module - hexagonal architecture
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductId;
import com.sigrap.product.domain.model.ProductName;
import com.sigrap.product.domain.model.ProductPrice;
import com.sigrap.product.domain.model.ProductStock;
import com.sigrap.product.domain.port.ProductRepositoryPort;

// Sale module - hexagonal architecture
import com.sigrap.sale.domain.model.PaymentMethod;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.model.SaleItemId;
import com.sigrap.sale.domain.model.SaleNumber;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.model.SaleReturnNumber;
import com.sigrap.sale.domain.port.SaleItemRepositoryPort;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import com.sigrap.sale.domain.port.SaleReturnRepositoryPort;

// Supplier module - hexagonal architecture
import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.model.PurchaseOrderNumber;
import com.sigrap.supplier.domain.model.PurchaseOrderStatus;
import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.model.SupplierName;
import com.sigrap.supplier.domain.model.SupplierEmail;
import com.sigrap.supplier.domain.model.SupplierPhone;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort;

// User module - hexagonal architecture
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserId;
import com.sigrap.user.domain.model.Username;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleName;
import com.sigrap.user.domain.port.UserRepositoryPort;
import com.sigrap.user.domain.port.RoleRepositoryPort;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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

  private final AuditLogRepositoryPort auditLogRepository;
  private final CategoryRepositoryPort categoryRepository;
  private final ProductRepositoryPort productRepository;
  private final UserRepositoryPort userRepository;
  private final RoleRepositoryPort roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final ScheduleRepositoryPort scheduleRepository;
  private final AttendanceRepositoryPort attendanceRepository;
  private final SupplierRepositoryPort supplierRepository;
  private final PurchaseOrderRepositoryPort purchaseOrderRepository;
  private final CustomerRepositoryPort customerRepository;
  private final SaleRepositoryPort saleRepository;
  private final SaleItemRepositoryPort saleItemRepository;
  private final SaleReturnRepositoryPort saleReturnRepository;

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
    seedRoles();
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
   * Seeds initial roles into the database.
   * Creates default roles (ADMINISTRATOR and EMPLOYEE) with their permissions.
   * Only executes if the roles table is empty.
   */
  private void seedRoles() {
    List<Role> existingRoles = roleRepository.findAll();
    if (existingRoles.isEmpty()) {
      log.info("Seeding roles...");

      roleRepository.save(new Role(new RoleName("ADMINISTRATOR"), "Full system access with all permissions"));
      roleRepository.save(new Role(new RoleName("EMPLOYEE"), "Standard employee access with limited permissions"));
      
      log.info("Roles seeded successfully.");
    } else {
      log.info("Roles already exist, skipping seeding.");
    }
  }

  /**
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
          new Category(new CategoryName("School Supplies"), "Basic supplies for elementary, middle, and high school students."),
          new Category(new CategoryName("Office Supplies"), "Professional supplies for office and administrative work."),
          new Category(new CategoryName("General Stationery"), "Paper, envelopes, notebooks, and other everyday stationery items."),
          new Category(new CategoryName("Gifts and Wrapping"), "Decorative items, greeting cards, and gift packaging."),
          new Category(new CategoryName("Basic Technology"), "Computer accessories, storage devices, and simple electronics."),
          new Category(new CategoryName("Art and Drawing"), "Materials for artistic expression, technical drawing, and crafts."),
          new Category(new CategoryName("Writing Instruments"), "Pens, pencils, markers, and other writing tools."),
          new Category(new CategoryName("Notebooks and Journals"), "Different notebook formats for school and professional use."),
          new Category(new CategoryName("Organization and Filing"), "Products to sort, store, and organize documents."),
          new Category(new CategoryName("Backpacks and Bags"), "Solutions for carrying school and office materials."),
          new Category(new CategoryName("Educational Materials"), "Learning resources to support education across different subjects."),
          new Category(new CategoryName("Craft Supplies"), "Various materials for creative and decorative projects.")
        )
      );
      log.info("Categories seeded successfully.");
    } else {
      log.info("Categories already exist, skipping seeding.");
    }
  }

  /**
   * Helper method to create a Product domain entity with random stock values.
   */
  private Product createProduct(String name, String description, String costPrice, String salePrice, CategoryId categoryId) {
    return new Product(
      new ProductName(name),
      description,
      new ProductPrice(new BigDecimal(costPrice)),
      new ProductPrice(new BigDecimal(salePrice)),
      new ProductStock(random.nextInt(131) + 20),
      new ProductStock(random.nextInt(16) + 5),
      categoryId
    );
  }


  /**
   * Seeds initial products into the database.
   * Creates a diverse catalog of products across all categories with realistic pricing.
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
          createProduct("Norma College Ruled Notebook", "College-ruled notebook with 100 sheets, grid layout, hard cover", "3.00", "4.99", categories.get(0).getId()),
          createProduct("HB Woodcase Pencil", "HB graphite woodcase pencil with eraser and hexagonal body", "0.25", "0.49", categories.get(0).getId()),
          createProduct("Faber-Castell Colored Pencils, 12-Pack", "Box of 12 colored pencils with durable tips and vibrant colors", "4.00", "6.99", categories.get(0).getId()),
          createProduct("Faber-Castell Geometry Set", "Geometry set with 12-inch ruler, triangles, protractor, and precision compass", "4.50", "7.99", categories.get(0).getId()),
          createProduct("Soft Vinyl Eraser", "Soft vinyl eraser that removes graphite cleanly without smudging", "0.30", "0.99", categories.get(0).getId()),
          createProduct("Metal Desktop Stapler", "Metal desktop stapler, 20-sheet capacity, black finish", "6.00", "10.99", categories.get(1).getId()),
          createProduct("Two-Hole Paper Punch", "Metal 2-hole paper punch, 20-sheet capacity, black finish", "7.00", "12.99", categories.get(1).getId()),
          createProduct("Standard Paper Clips, 100-Pack", "Box of 100 standard metal paper clips, 33 mm, silver finish", "1.00", "2.49", categories.get(1).getId()),
          createProduct("Stackable Letter Trays, 3-Pack", "Set of 3 stackable plastic letter trays for documents, black", "7.50", "13.99", categories.get(1).getId()),
          createProduct("Desktop Tape Dispenser", "Desktop tape dispenser with weighted, non-slip base", "3.00", "5.49", categories.get(1).getId()),
          createProduct("A4 Copy Paper Ream (500 Sheets)", "Ream of A4 copy paper, 75 gsm, 500 sheets, bright white", "4.00", "7.99", categories.get(2).getId()),
          createProduct("Letter-Size Manila Envelopes, 50-Pack", "Pack of 50 letter-size manila envelopes, kraft color", "3.00", "5.99", categories.get(2).getId()),
          createProduct("White Cardstock Sheet", "White cardstock sheet, 27x39 in, 180 gsm, matte finish", "0.25", "0.75", categories.get(2).getId()),
          createProduct("Letter-Size Colored Paper Pad", "Letter-size pad of colored paper, 20 sheets, assorted colors", "2.50", "4.99", categories.get(2).getId()),
          createProduct("Letter-Size Adhesive Paper, 20-Pack", "Pack of 20 white letter-size adhesive paper sheets", "5.00", "8.99", categories.get(2).getId()),
          createProduct("Assorted Greeting Card with Envelope", "Greeting card with envelope, assorted designs for any occasion", "1.00", "2.49", categories.get(3).getId()),
          createProduct("Assorted Gift Wrap Sheet", "Gift wrap sheet, 27x39 in, assorted designs", "0.75", "1.99", categories.get(3).getId()),
          createProduct("Small Anime Figurine", "Decorative anime character figurine, 4 in tall, assorted characters", "4.00", "7.99", categories.get(3).getId()),
          createProduct("Decorative Gift Box, Medium", "Medium decorative gift box with bow, suitable for gifts", "2.00", "4.99", categories.get(3).getId()),
          createProduct("Motivational Quote Cards, 10-Pack", "Set of 10 small cards with inspirational messages, assorted designs", "2.50", "4.99", categories.get(3).getId()),
          createProduct("16GB USB 2.0 Flash Drive", "16 GB USB 2.0 flash drive with durable plastic housing", "5.00", "9.99", categories.get(4).getId()),
          createProduct("Wired Optical USB Mouse", "Wired optical USB mouse, 1000 DPI resolution, ergonomic design", "4.00", "7.99", categories.get(4).getId()),
          createProduct("14-Inch Laptop Sleeve", "Padded, water-resistant sleeve for 14-inch laptops", "6.00", "11.99", categories.get(4).getId()),
          createProduct("Wired On-Ear Headphones", "Wired on-ear headphones with 3.5 mm connector and in-line volume control", "4.00", "7.99", categories.get(4).getId()),
          createProduct("HDMI to VGA Adapter", "HDMI to VGA adapter compatible with PCs and mobile devices", "6.00", "11.99", categories.get(4).getId()),
          createProduct("Watercolor Paint Set, 12 Colors", "Set of 12 watercolor pans with brush, assorted vibrant colors", "5.00", "9.99", categories.get(5).getId()),
          createProduct("A4 Watercolor Paper Pad", "A4 watercolor paper pad, 10 sheets, 140 lb (300 gsm), cold press", "4.00", "7.99", categories.get(5).getId()),
          createProduct("Artist Brush Set, 5-Pack", "Set of 5 assorted-size brushes for wet media techniques", "3.00", "5.99", categories.get(5).getId()),
          createProduct("Stretched Canvas 12x16 in", "Pre-primed cotton canvas on wooden frame, 12x16 in", "6.00", "10.99", categories.get(5).getId()),
          createProduct("Professional Drawing Pencils, 6-Pack", "Set of 6 graphite drawing pencils in assorted hardness grades", "4.00", "7.49", categories.get(5).getId()),
          createProduct("Ballpoint Pens, 12-Pack", "Box of 12 medium-point blue ballpoint pens", "2.50", "4.49", categories.get(6).getId()),
          createProduct("Classic Fountain Pen", "Metal-body fountain pen with refillable cartridge and fine nib", "7.00", "13.99", categories.get(6).getId()),
          createProduct("Permanent Markers, 4-Pack", "Set of 4 permanent markers, assorted basic colors, chisel tip", "3.00", "5.99", categories.get(6).getId()),
          createProduct("Neon Highlighters, 5-Pack", "Set of 5 neon highlighters, chisel tip, fluorescent ink", "3.00", "5.99", categories.get(6).getId()),
          createProduct("Liquid Correction Pen", "Liquid correction pen with fast-drying formula and precision metal tip", "1.50", "2.99", categories.get(6).getId()),
          createProduct("5-Subject Spiral Notebook", "5-subject spiral notebook, 200 sheets, color dividers", "5.00", "8.99", categories.get(7).getId()),
          createProduct("Pocket Grid Notebook", "Pocket-size grid notebook, 80 sheets, hard cover, 3.5x5.5 in", "1.50", "2.99", categories.get(7).getId()),
          createProduct("Executive Daily Planner", "Executive daily planner, one page per day, hard cover, ribbon marker", "7.00", "12.99", categories.get(7).getId()),
          createProduct("Neon Sticky Notes Block", "Block of sticky notes in 5 neon colors, 100 sheets per color", "2.00", "3.99", categories.get(7).getId()),
          createProduct("Personal Diary with Lock", "Personal diary with lock, padded cover, 200 pages, 6x8 in", "6.00", "11.99", categories.get(7).getId()),
          createProduct("Letter-Size Lever Arch File", "Letter-size lever arch file with wide spine and metal mechanism", "4.00", "7.99", categories.get(8).getId()),
          createProduct("Plastic File Folder with Fastener", "Plastic file folder with fastener, letter-size, assorted colors", "1.50", "2.99", categories.get(8).getId()),
          createProduct("Plastic Divider Set, 5 Tabs", "Set of 5 plastic dividers, letter-size, assorted colors", "1.25", "2.49", categories.get(8).getId()),
          createProduct("Clear Plastic Storage Box, 5L", "Clear plastic storage box with lid, 5-liter capacity", "4.00", "7.49", categories.get(8).getId()),
          createProduct("Basic School Backpack", "Basic school backpack with two compartments and side pocket", "12.00", "24.99", categories.get(9).getId()),
          createProduct("Simple Fabric Pencil Case", "Fabric pencil case with zipper, holds up to 10 pencils, assorted designs", "2.00", "3.99", categories.get(9).getId()),
          createProduct("Color Counting Abacus", "Wooden abacus with colorful beads, 10 rows of 10 beads each", "6.00", "11.99", categories.get(10).getId()),
          createProduct("Educational World Map Poster", "Laminated educational world map poster, 20x28 in, double-sided", "4.00", "7.99", categories.get(10).getId()),
          createProduct("Modeling Clay Set, 4 Colors", "Set of modeling clay in 4 basic colors, non-toxic, 17.6 oz total", "3.50", "6.99", categories.get(11).getId()),
          createProduct("Basic Jewelry Craft Kit", "Jewelry-making kit with assorted beads, cords, and tools", "7.00", "13.99", categories.get(11).getId())
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

      // Fetch roles from the database
      Role adminRole = roleRepository.findByName(new RoleName("ADMINISTRATOR"))
        .orElseThrow(() -> new IllegalStateException("ADMINISTRATOR role not found. Please ensure roles are seeded first."));
      Role employeeRole = roleRepository.findByName(new RoleName("EMPLOYEE"))
        .orElseThrow(() -> new IllegalStateException("EMPLOYEE role not found. Please ensure roles are seeded first."));

      // Create admin user
      User adminUser = new User(
        new Username("admin"),
        new UserEmail("admin@sigrap.com"),
        passwordEncoder.encode("Admin123*")
      );
      adminUser.addRole(adminRole);

      // Create employee user
      User employeeUser = new User(
        new Username("employee"),
        new UserEmail("employee@sigrap.com"),
        passwordEncoder.encode("Employee123*")
      );
      employeeUser.addRole(employeeRole);

      userRepository.saveAll(List.of(adminUser, employeeUser));
      log.info("Users seeded successfully.");
    } else {
      log.info("Users already exist, skipping seeding.");
    }
  }


  /**
   * Seeds initial schedules into the database.
   * Creates weekly schedules for employees.
   * Only executes if the schedules table is empty.
   */
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
      if (!"employee@sigrap.com".equals(user.getEmail().value())) {
        continue;
      }

      schedules.add(
        new Schedule(user.getId(), "Monday", defaultStartTime, defaultEndTime, "Regular")
      );

      schedules.add(
        new Schedule(user.getId(), "Tuesday", defaultStartTime, defaultEndTime, "Regular")
      );

      schedules.add(
        new Schedule(user.getId(), "Wednesday", defaultStartTime, defaultEndTime, "Regular")
      );

      schedules.add(
        new Schedule(user.getId(), "Thursday", defaultStartTime, defaultEndTime, "Regular")
      );

      schedules.add(
        new Schedule(user.getId(), "Friday", defaultStartTime, defaultEndTime, "Regular")
      );

      schedules.add(
        new Schedule(user.getId(), "Saturday", defaultStartTime, saturdayEndTime, "Weekend")
      );

      schedules.add(
        new Schedule(user.getId(), "Wednesday", extraHoursStartTime, extraHoursEndTime, "Overtime")
      );
    }

    scheduleRepository.saveAll(schedules);
    log.info("Schedules seeded successfully.");
  }

  /**
   * Seeds initial attendance records into the database.
   * Creates sample attendance records for the past week.
   * Only executes if the attendance table is empty.
   */
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
      .filter(user -> "employee@sigrap.com".equals(user.getEmail().value()))
      .findFirst()
      .orElse(null);

    if (targetEmployee == null) {
      log.warn("Target employee not found, skipping attendance seeding.");
      return;
    }

    LocalDateTime lastWeekMonday = now
      .minusWeeks(1)
      .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

    // Monday - Present
    LocalDateTime mondayClockIn = lastWeekMonday.withHour(8).withMinute(5);
    LocalDateTime mondayClockOut = lastWeekMonday.withHour(17).withMinute(10);
    double mondayHours = calculateHoursWorked(mondayClockIn, mondayClockOut);

    Attendance mondayAttendance = new Attendance(
      targetEmployee.getId(),
      lastWeekMonday.toLocalDate().atStartOfDay(),
      AttendanceStatus.PRESENT
    );
    mondayAttendance.clockIn(mondayClockIn);
    mondayAttendance.clockOut(mondayClockOut);
    attendanceRecords.add(mondayAttendance);

    // Tuesday - Late
    LocalDateTime lastWeekTuesday = lastWeekMonday.plusDays(1);
    LocalDateTime tuesdayClockIn = lastWeekTuesday.withHour(8).withMinute(45);
    LocalDateTime tuesdayClockOut = lastWeekTuesday.withHour(17).withMinute(15);

    Attendance tuesdayAttendance = new Attendance(
      targetEmployee.getId(),
      lastWeekTuesday.toLocalDate().atStartOfDay(),
      AttendanceStatus.LATE
    );
    tuesdayAttendance.clockIn(tuesdayClockIn);
    tuesdayAttendance.clockOut(tuesdayClockOut);
    attendanceRecords.add(tuesdayAttendance);

    // Wednesday - Early Departure
    LocalDateTime lastWeekWednesday = lastWeekTuesday.plusDays(1);
    LocalDateTime wednesdayClockIn = lastWeekWednesday.withHour(8).withMinute(0);
    LocalDateTime wednesdayClockOut = lastWeekWednesday.withHour(15).withMinute(30);

    Attendance wednesdayAttendance = new Attendance(
      targetEmployee.getId(),
      lastWeekWednesday.toLocalDate().atStartOfDay(),
      AttendanceStatus.EARLY_DEPARTURE
    );
    wednesdayAttendance.clockIn(wednesdayClockIn);
    wednesdayAttendance.clockOut(wednesdayClockOut);
    attendanceRecords.add(wednesdayAttendance);

    // Thursday - Present
    LocalDateTime lastWeekThursday = lastWeekWednesday.plusDays(1);
    LocalDateTime thursdayClockIn = lastWeekThursday.withHour(7).withMinute(55);
    LocalDateTime thursdayClockOut = lastWeekThursday.withHour(17).withMinute(5);

    Attendance thursdayAttendance = new Attendance(
      targetEmployee.getId(),
      lastWeekThursday.toLocalDate().atStartOfDay(),
      AttendanceStatus.PRESENT
    );
    thursdayAttendance.clockIn(thursdayClockIn);
    thursdayAttendance.clockOut(thursdayClockOut);
    attendanceRecords.add(thursdayAttendance);

    // Friday - On Leave
    LocalDateTime lastWeekFriday = lastWeekThursday.plusDays(1);

    Attendance fridayAttendance = new Attendance(
      targetEmployee.getId(),
      lastWeekFriday.toLocalDate().atStartOfDay(),
      AttendanceStatus.ON_LEAVE
    );
    attendanceRecords.add(fridayAttendance);

    attendanceRepository.saveAll(attendanceRecords);
    log.info("Attendance records seeded successfully ({} records).", attendanceRecords.size());
  }

  /**
   * Calculate hours worked between clock-in and clock-out times.
   *
   * @param clockIn Clock-in time
   * @param clockOut Clock-out time
   * @return Hours worked as a double with 2 decimal places
   */
  private double calculateHoursWorked(LocalDateTime clockIn, LocalDateTime clockOut) {
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
        new Supplier(
          new SupplierName("Office Depot"),
          "Michael Sanders",
          new SupplierEmail("msanders@officedepot.com"),
          new SupplierPhone("(214) 555-2700"),
          "123 Market St, Dallas, TX 75201"
        )
      );

      suppliers.add(
        new Supplier(
          new SupplierName("Downtown Stationery"),
          "Amy Campbell",
          new SupplierEmail("amy.campbell@downtownstationery.com"),
          new SupplierPhone("(312) 555-1900"),
          "200 W Lake St, Chicago, IL 60606"
        )
      );

      suppliers.add(
        new Supplier(
          new SupplierName("Artisan Art Supplies"),
          "Robert Gutierrez",
          new SupplierEmail("robert.gutierrez@artisanart.com"),
          new SupplierPhone("(206) 555-4400"),
          "410 Pine St, Seattle, WA 98101"
        )
      );

      suppliers.add(
        new Supplier(
          new SupplierName("Scribe Paper Co."),
          "Mary Torres",
          new SupplierEmail("mary.torres@scribepaper.com"),
          new SupplierPhone("(303) 555-6600"),
          "1500 Blake St, Denver, CO 80202"
        )
      );

      suppliers.add(
        new Supplier(
          new SupplierName("Faber-Castell USA"),
          "George Reategui",
          new SupplierEmail("george.reategui@faber-castell.com"),
          new SupplierPhone("(216) 555-4800"),
          "500 Artway Dr, Cleveland, OH 44115"
        )
      );

      suppliers.add(
        new Supplier(
          new SupplierName("QuickBond Adhesives"),
          "Lucy Perez",
          new SupplierEmail("lucy.perez@quickbond.com"),
          new SupplierPhone("(305) 555-2300"),
          "890 Industrial Rd, Miami, FL 33101"
        )
      );

      suppliers.add(
        new Supplier(
          new SupplierName("Norma USA"),
          "Daniel Quinn",
          new SupplierEmail("daniel.quinn@normausa.com"),
          new SupplierPhone("(973) 555-8000"),
          "250 Stationery Blvd, Newark, NJ 07102"
        )
      );

      suppliers.add(
        new Supplier(
          new SupplierName("DistriOffice USA"),
          "Patricia Rogers",
          new SupplierEmail("patricia.rogers@distriofficeusa.com"),
          new SupplierPhone("(213) 555-1800"),
          "4100 Sunset Blvd, Los Angeles, CA 90029"
        )
      );

      suppliers.add(
        new Supplier(
          new SupplierName("Pilot USA"),
          "Carlos Mendoza",
          new SupplierEmail("carlos.mendoza@pilotusa.com"),
          new SupplierPhone("(818) 555-5200"),
          "100 Pilot Plaza, Burbank, CA 91502"
        )
      );

      suppliers.add(
        new Supplier(
          new SupplierName("National Paper Co."),
          "Fernando Torres",
          new SupplierEmail("fernando.torres@nationalpaper.com"),
          new SupplierPhone("(412) 555-9000"),
          "75 Mill Rd, Pittsburgh, PA 15222"
        )
      );

      supplierRepository.saveAll(suppliers);
      log.info("Suppliers seeded successfully.");
    } else {
      log.info("Suppliers already exist, skipping seeding.");
    }
  }


  /**
   * Seeds initial purchase orders into the database.
   * Creates sample purchase orders with different statuses.
   * Only executes if the purchase_orders table is empty.
   */
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

    Supplier downtownStationerySupplier = findSupplierByName(suppliers, "Downtown Stationery");
    Supplier faberCastellSupplier = findSupplierByName(suppliers, "Faber-Castell USA");
    Supplier artisanArtSuppliesSupplier = findSupplierByName(suppliers, "Artisan Art Supplies");
    Supplier officeDepotSupplier = findSupplierByName(suppliers, "Office Depot");
    Supplier normaUsaSupplier = findSupplierByName(suppliers, "Norma USA");

    // Simplified purchase order seeding (without line items)
    List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    // Order 1 - Received
    PurchaseOrder order1 = new PurchaseOrder(
      new PurchaseOrderNumber("PO-2024-001"),
      downtownStationerySupplier.getId(),
      LocalDate.now().minusDays(34),
      LocalDate.now().minusDays(30),
      new BigDecimal("395.00"),
      "Notebooks, pencils, and erasers"
    );
    order1.approve();
    order1.receive();
    purchaseOrders.add(order1);

    // Order 2 - Received
    PurchaseOrder order2 = new PurchaseOrder(
      new PurchaseOrderNumber("PO-2024-002"),
      faberCastellSupplier.getId(),
      LocalDate.now().minusDays(22),
      LocalDate.now().minusDays(18),
      new BigDecimal("335.00"),
      "Colored pencils and geometry sets"
    );
    order2.approve();
    order2.receive();
    purchaseOrders.add(order2);

    // Order 3 - Received
    PurchaseOrder order3 = new PurchaseOrder(
      new PurchaseOrderNumber("PO-2024-003"),
      officeDepotSupplier.getId(),
      LocalDate.now().minusDays(8),
      LocalDate.now().minusDays(4),
      new BigDecimal("500.00"),
      "Copy paper, cardstock, and colored paper"
    );
    order3.approve();
    order3.receive();
    purchaseOrders.add(order3);

    // Order 4 - Approved
    PurchaseOrder order4 = new PurchaseOrder(
      new PurchaseOrderNumber("PO-2024-004"),
      artisanArtSuppliesSupplier.getId(),
      LocalDate.now().minusDays(3),
      LocalDate.now().plusDays(3),
      new BigDecimal("120.00"),
      "Permanent markers"
    );
    order4.approve();
    purchaseOrders.add(order4);

    // Order 5 - Pending
    PurchaseOrder order5 = new PurchaseOrder(
      new PurchaseOrderNumber("PO-2024-005"),
      normaUsaSupplier.getId(),
      LocalDate.now().minusDays(1),
      LocalDate.now().plusDays(10),
      new BigDecimal("325.00"),
      "Notebooks and spiral notebooks"
    );
    purchaseOrders.add(order5);

    // Order 6 - Pending
    PurchaseOrder order6 = new PurchaseOrder(
      new PurchaseOrderNumber("PO-2024-006"),
      downtownStationerySupplier.getId(),
      LocalDate.now(),
      LocalDate.now().plusDays(15),
      new BigDecimal("67.50"),
      "Pencils and erasers"
    );
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
      .filter(s -> s.getName().value().equals(name))
      .findFirst()
      .orElse(suppliers.get(0));
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
          new Customer(new CustomerName("John Smith"), "200000001", new CustomerEmail("john.smith@example.com"), new CustomerPhone("(212) 555-0101"), "145 West 34th St, New York, NY 10001"),
          new Customer(new CustomerName("Emily Johnson"), "200000002", new CustomerEmail("emily.johnson@example.com"), new CustomerPhone("(415) 555-0102"), "600 Market St Apt 4B, San Francisco, CA 94104"),
          new Customer(new CustomerName("Michael Brown"), "200000003", new CustomerEmail("michael.brown@example.com"), new CustomerPhone("(773) 555-0103"), "8200 N Clark St, Chicago, IL 60626"),
          new Customer(new CustomerName("Sarah Davis"), "200000004", new CustomerEmail("sarah.davis@example.com"), new CustomerPhone("(305) 555-0104"), "150 Ocean Dr, Miami Beach, FL 33139"),
          new Customer(new CustomerName("David Wilson"), "200000005", new CustomerEmail("david.wilson@example.com"), new CustomerPhone("(469) 555-0105"), "420 Main St, Dallas, TX 75202"),
          new Customer(new CustomerName("Olivia Martinez"), "200000006", new CustomerEmail("olivia.martinez@example.com"), new CustomerPhone("(602) 555-0106"), "700 E Camelback Rd, Phoenix, AZ 85014"),
          new Customer(new CustomerName("Andrew Thompson"), "200000007", new CustomerEmail("andrew.thompson@example.com"), new CustomerPhone("(404) 555-0107"), "250 Peachtree St NE, Atlanta, GA 30303"),
          new Customer(new CustomerName("Sophia Ramirez"), "200000008", new CustomerEmail("sophia.ramirez@example.com"), new CustomerPhone("(206) 555-0108"), "900 S Jackson St, Seattle, WA 98104"),
          new Customer(new CustomerName("Jacob Torres"), "200000009", new CustomerEmail("jacob.torres@example.com"), new CustomerPhone("(702) 555-0109"), "500 Fremont St, Las Vegas, NV 89101"),
          new Customer(new CustomerName("Daniela Harris"), "200000010", new CustomerEmail("daniela.harris@example.com"), new CustomerPhone("(303) 555-0110"), "1800 Larimer St, Denver, CO 80202"),
          new Customer(new CustomerName("Sebastian Carter"), "200000011", new CustomerEmail("sebastian.carter@example.com"), new CustomerPhone("(512) 555-0111"), "400 Congress Ave, Austin, TX 78701"),
          new Customer(new CustomerName("Victoria Ortiz"), "200000012", new CustomerEmail("victoria.ortiz@example.com"), new CustomerPhone("(215) 555-0112"), "700 S 4th St, Philadelphia, PA 19147"),
          new Customer(new CustomerName("Cameron Reyes"), "200000013", new CustomerEmail("cameron.reyes@example.com"), new CustomerPhone("(916) 555-0113"), "500 J St, Sacramento, CA 95814"),
          new Customer(new CustomerName("Isabella Vargas"), "200000014", new CustomerEmail("isabella.vargas@example.com"), new CustomerPhone("(713) 555-0114"), "1200 McKinney St, Houston, TX 77010"),
          new Customer(new CustomerName("Samuel Morris"), "200000015", new CustomerEmail("samuel.morris@example.com"), new CustomerPhone("(617) 555-0115"), "800 Boylston St, Boston, MA 02199")
        )
      );

      log.info("Successfully seeded 15 customers");
    }
  }


  /**
   * Seeds initial sales into the database.
   * Creates a variety of sales with different customers, employees, products, and dates.
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
      log.warn("No customers found, skipping sales seeding as customer is now mandatory for sales.");
      return;
    }

    List<User> employees = userRepository.findAll();
    if (employees.isEmpty()) {
      log.warn("No employees found, skipping sales seeding.");
      return;
    }

    List<Sale> salesToCreate = new ArrayList<>();
    int numberOfSales = Math.min(20, random.nextInt(31) + 10); // Create 10-40 sales

    for (int i = 0; i < numberOfSales; i++) {
      User assignedEmployee = employees.get(random.nextInt(employees.size()));
      Customer customer = customers.get(random.nextInt(customers.size()));

      LocalDateTime saleDateTime = LocalDateTime.now()
        .minusDays(random.nextInt(180))
        .minusHours(random.nextInt(24))
        .minusMinutes(random.nextInt(60));

      // Generate sale number
      String saleNumberStr = String.format("SALE-%d-%05d", 
        saleDateTime.getYear(), 
        i + 1);
      SaleNumber saleNumber = new SaleNumber(saleNumberStr);

      // Randomly select payment method
      PaymentMethod[] paymentMethods = PaymentMethod.values();
      PaymentMethod paymentMethod = paymentMethods[random.nextInt(paymentMethods.length)];

      // Create sale
      Sale sale = new Sale(
        saleNumber,
        customer.getId().value(),
        assignedEmployee.getId().value(),
        saleDateTime,
        paymentMethod,
        "Seeded sale"
      );

      salesToCreate.add(sale);
    }

    if (!salesToCreate.isEmpty()) {
      // Save sales first
      List<Sale> savedSales = saleRepository.saveAll(salesToCreate);
      
      // Now create and save sale items with the saved sale's ID
      List<Product> productsToUpdate = new ArrayList<>();
      for (Sale savedSale : savedSales) {
        int numberOfItemsInSale = random.nextInt(5) + 1;

        for (int j = 0; j < numberOfItemsInSale; j++) {
          Product product = products.get(random.nextInt(products.size()));
          int quantity = random.nextInt(5) + 1;

          if (product.getStock().value() < quantity) {
            if (product.getStock().value() > 0) {
              quantity = product.getStock().value();
            } else {
              continue;
            }
          }

          // Decrease product stock
          product.decreaseStock(quantity);

          // Create sale item with the saved sale's ID
          SaleItem itemWithSaleId = new SaleItem(
            savedSale.getId(),
            product.getId().value(),
            quantity,
            product.getSalePrice().value()
          );
          saleItemRepository.save(itemWithSaleId);
          
          // Add item to sale for total calculation
          savedSale.addItem(itemWithSaleId);
          
          // Collect products that need stock update
          if (!productsToUpdate.contains(product)) {
            productsToUpdate.add(product);
          }
        }
        
        // Complete the sale
        if (!savedSale.getItems().isEmpty()) {
          savedSale.complete();
        }
      }

      // Save updated sales with completed status and totals
      saleRepository.saveAll(savedSales);

      // Save updated product stocks
      productRepository.saveAll(productsToUpdate);

      log.info("Successfully seeded {} sales (with items).", savedSales.size());
    } else {
      log.info("No sales were generated to seed.");
    }
  }

  /**
   * Seeds initial sales returns into the database.
   * Creates a few sample returns based on existing sales.
   * Only executes if the sales_returns table is empty and sales exist.
   */
  private void seedSaleReturns() {
    List<SaleReturn> existingReturns = saleReturnRepository.findAll();
    if (!existingReturns.isEmpty()) {
      log.info("Sale returns already exist, skipping seeding.");
      return;
    }

    List<Sale> sales = saleRepository.findAll();
    if (sales.isEmpty()) {
      log.warn("No sales found, skipping sales returns seeding.");
      return;
    }

    log.info("Seeding sales returns...");
    List<SaleReturn> returnsToCreate = new ArrayList<>();
    int numberOfReturnsToSeed = Math.min(sales.size(), 5);

    for (int i = 0; i < numberOfReturnsToSeed; i++) {
      Sale originalSale = sales.get(random.nextInt(sales.size()));
      
      // Only create returns for completed sales
      if (originalSale.getItems().isEmpty()) {
        continue;
      }

      // Generate return number
      String returnNumberStr = String.format("RET-%d-%05d", 
        LocalDateTime.now().getYear(), 
        i + 1);
      SaleReturnNumber returnNumber = new SaleReturnNumber(returnNumberStr);

      // Get a random item from the sale to calculate refund
      List<SaleItem> items = new ArrayList<>(originalSale.getItems());
      if (items.isEmpty()) {
        continue;
      }
      
      SaleItem itemToReturn = items.get(random.nextInt(items.size()));
      int quantityToReturn = 1;
      if (itemToReturn.getQuantity() > 1) {
        quantityToReturn = random.nextInt(itemToReturn.getQuantity()) + 1;
      }

      // Calculate refund amount based on item
      BigDecimal refundAmount = itemToReturn.getUnitPrice()
        .multiply(BigDecimal.valueOf(quantityToReturn))
        .setScale(2, java.math.RoundingMode.HALF_UP);

      LocalDateTime returnDate = originalSale.getCreatedAt().plusDays(random.nextInt(5) + 1);

      SaleReturn saleReturn = new SaleReturn(
        returnNumber,
        originalSale.getId(),
        returnDate,
        getRandomReturnReason(),
        refundAmount,
        "Seeded return for product quantity: " + quantityToReturn
      );

      returnsToCreate.add(saleReturn);
    }

    if (!returnsToCreate.isEmpty()) {
      for (SaleReturn saleReturn : returnsToCreate) {
        saleReturnRepository.save(saleReturn);
      }
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
    List<AuditLog> existingLogs = auditLogRepository.findAll();
    if (!existingLogs.isEmpty()) {
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
      .filter(u -> "admin@sigrap.com".equals(u.getEmail().value()))
      .findFirst()
      .orElse(null);

    User employeeUser = users
      .stream()
      .filter(u -> "employee@sigrap.com".equals(u.getEmail().value()))
      .findFirst()
      .orElse(null);

    if (adminUser != null) {
      auditLogs.add(
        new AuditLog(
          adminUser.getEmail().value(),
          AuditAction.LOGIN,
          EntityType.USER,
          null,
          LocalDateTime.now().minusDays(7).withHour(8).withMinute(0),
          "192.168.1.100",
          "Mozilla/5.0",
          "Admin user logged in",
          AuditStatus.SUCCESS,
          150L
        )
      );

      auditLogs.add(
        new AuditLog(
          adminUser.getEmail().value(),
          AuditAction.CREATE,
          EntityType.PRODUCT,
          null,
          LocalDateTime.now().minusDays(7).withHour(9).withMinute(15),
          "192.168.1.100",
          "Mozilla/5.0",
          "Created new product",
          AuditStatus.SUCCESS,
          250L
        )
      );

      auditLogs.add(
        new AuditLog(
          adminUser.getEmail().value(),
          AuditAction.UPDATE,
          EntityType.SUPPLIER,
          null,
          LocalDateTime.now().minusDays(6).withHour(10).withMinute(30),
          "192.168.1.100",
          "Mozilla/5.0",
          "Updated supplier information",
          AuditStatus.SUCCESS,
          180L
        )
      );
    }

    if (employeeUser != null) {
      auditLogs.add(
        new AuditLog(
          employeeUser.getEmail().value(),
          AuditAction.LOGIN,
          EntityType.USER,
          null,
          LocalDateTime.now().minusDays(7).withHour(8).withMinute(30),
          "192.168.1.101",
          "Mozilla/5.0",
          "Employee user logged in",
          AuditStatus.SUCCESS,
          120L
        )
      );

      auditLogs.add(
        new AuditLog(
          employeeUser.getEmail().value(),
          AuditAction.CREATE,
          EntityType.SALE,
          null,
          LocalDateTime.now().minusDays(7).withHour(9).withMinute(45),
          "192.168.1.101",
          "Mozilla/5.0",
          "Created new sale",
          AuditStatus.SUCCESS,
          300L
        )
      );

      auditLogs.add(
        new AuditLog(
          employeeUser.getEmail().value(),
          AuditAction.UPDATE,
          EntityType.PRODUCT,
          null,
          LocalDateTime.now().minusDays(7).withHour(9).withMinute(45),
          "192.168.1.101",
          "Mozilla/5.0",
          "Updated product stock",
          AuditStatus.SUCCESS,
          200L
        )
      );
    }

    for (AuditLog auditLog : auditLogs) {
      auditLogRepository.save(auditLog);
    }
    log.info("Successfully seeded {} audit logs.", auditLogs.size());
  }
}
