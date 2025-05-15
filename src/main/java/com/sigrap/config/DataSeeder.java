package com.sigrap.config;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.employee.ActivityLog;
import com.sigrap.employee.ActivityLogRepository;
import com.sigrap.employee.Attendance;
import com.sigrap.employee.AttendanceRepository;
import com.sigrap.employee.Employee;
import com.sigrap.employee.EmployeePerformance;
import com.sigrap.employee.EmployeePerformanceRepository;
import com.sigrap.employee.EmployeeRepository;
import com.sigrap.employee.Schedule;
import com.sigrap.employee.ScheduleRepository;
import com.sigrap.payment.Payment;
import com.sigrap.payment.PaymentRepository;
import com.sigrap.payment.PaymentStatus;
import com.sigrap.permission.Permission;
import com.sigrap.permission.PermissionRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.role.Role;
import com.sigrap.role.RoleRepository;
import com.sigrap.supplier.PaymentMethod;
import com.sigrap.supplier.PurchaseOrder;
import com.sigrap.supplier.PurchaseOrderItem;
import com.sigrap.supplier.PurchaseOrderRepository;
import com.sigrap.supplier.PurchaseOrderTrackingEvent;
import com.sigrap.supplier.PurchaseOrderTrackingEventRepository;
import com.sigrap.supplier.Supplier;
import com.sigrap.supplier.SupplierRepository;
import com.sigrap.supplier.SupplierStatus;
import com.sigrap.user.User;
import com.sigrap.user.UserNotificationPreference;
import com.sigrap.user.UserNotificationPreference.NotificationType;
import com.sigrap.user.UserNotificationPreferenceRepository;
import com.sigrap.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * <p>This seeder ensures the application has necessary initial data for proper operation.
 * It only seeds data if the respective tables are empty, preventing duplicate entries.</p>
 *
 * <p>The seeder can be disabled using the property 'app.data-seeder.enabled=false'.</p>
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
   * Repository for role database operations.
   * Used to check if roles exist and to save new roles during seeding.
   */
  private final RoleRepository roleRepository;

  /**
   * Repository for permission database operations.
   * Used to check if permissions exist and to save new permissions during seeding.
   */
  private final PermissionRepository permissionRepository;

  /**
   * Repository for user notification preference database operations.
   * Used to save default notification preferences for users.
   */
  private final UserNotificationPreferenceRepository userNotificationPreferenceRepository;

  /**
   * Password encoder for securely hashing user passwords before storing them.
   * Ensures that seeded user account passwords are properly secured.
   */
  private final PasswordEncoder passwordEncoder;

  /**
   * Repository for employee database operations.
   * Used to check if employees exist and to save new employees during seeding.
   */
  private final EmployeeRepository employeeRepository;

  /**
   * Repository for schedule database operations.
   * Used to check if schedules exist and to save new schedules during seeding.
   */
  private final ScheduleRepository scheduleRepository;

  /**
   * Repository for attendance database operations.
   * Used to check if attendance records exist and to save new attendance records during seeding.
   */
  private final AttendanceRepository attendanceRepository;

  /**
   * Repository for employee performance database operations.
   * Used to check if employee performance records exist and to save new performance records during seeding.
   */
  private final EmployeePerformanceRepository employeePerformanceRepository;

  /**
   * Repository for activity log database operations.
   * Used to check if activity logs exist and to save new activity logs during seeding.
   */
  private final ActivityLogRepository activityLogRepository;

  /**
   * Repository for supplier database operations.
   * Used to check if suppliers exist and to save new suppliers during seeding.
   */
  private final SupplierRepository supplierRepository;

  private final PurchaseOrderRepository purchaseOrderRepository;
  private final PurchaseOrderTrackingEventRepository purchaseOrderTrackingEventRepository;
  private final PaymentRepository paymentRepository;

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
    seedPermissions();
    seedRoles();
    seedUsers();
    seedUserNotificationPreferences();
    seedEmployees();
    seedSchedules();
    seedAttendance();
    seedEmployeePerformance();
    seedActivityLogs();
    seedSuppliers();
    seedPurchaseOrders();
    seedPurchaseOrderTrackingEvents();
    seedPayments();
    log.info("Data seeding completed.");
  }

  /**
   * Seeds initial product categories into the database.
   * Creates a comprehensive set of categories covering different types of stationery items.
   * Only executes if the categories table is empty.
   */
  private void seedCategories() {
    if (categoryRepository.count() == 0) {
      log.info("Seeding categories...");

      categoryRepository.saveAll(
        List.of(
          Category.builder()
            .name("Útiles Escolares")
            .description(
              "Artículos básicos para estudiantes de primaria y secundaria."
            )
            .build(),
          Category.builder()
            .name("Artículos de Oficina")
            .description(
              "Suministros profesionales para el trabajo administrativo y de oficina."
            )
            .build(),
          Category.builder()
            .name("Papelería General")
            .description(
              "Papeles, sobres, cuadernos y otros artículos de uso cotidiano."
            )
            .build(),
          Category.builder()
            .name("Regalos y Detalles")
            .description(
              "Artículos decorativos, tarjetas y empaques para obsequios."
            )
            .build(),
          Category.builder()
            .name("Tecnología Básica")
            .description(
              "Accesorios de computación, almacenamiento y dispositivos sencillos."
            )
            .build(),
          Category.builder()
            .name("Arte y Dibujo")
            .description(
              "Materiales para expresión artística, dibujo técnico y manualidades creativas."
            )
            .build(),
          Category.builder()
            .name("Escritura")
            .description(
              "Bolígrafos, lápices, marcadores y otros instrumentos de escritura."
            )
            .build(),
          Category.builder()
            .name("Cuadernos y Libretas")
            .description(
              "Diversos formatos de cuadernos para diferentes usos escolares y profesionales."
            )
            .build(),
          Category.builder()
            .name("Organización y Archivo")
            .description(
              "Productos para clasificar, almacenar y organizar documentos."
            )
            .build(),
          Category.builder()
            .name("Mochilas y Bolsos")
            .description(
              "Soluciones para transportar materiales escolares y de oficina."
            )
            .build(),
          Category.builder()
            .name("Material Didáctico")
            .description(
              "Recursos educativos para facilitar el aprendizaje en diferentes áreas."
            )
            .build(),
          Category.builder()
            .name("Manualidades")
            .description(
              "Materiales diversos para proyectos creativos y decorativos."
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
          Product.builder()
            .name("Cuaderno Universitario Norma")
            .description(
              "Cuaderno universitario de 100 hojas, cuadriculado, pasta dura"
            )
            .costPrice(new BigDecimal("4000"))
            .salePrice(new BigDecimal("7000"))
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Lápiz Mirado HB")
            .description("Lápiz grafito HB, cuerpo hexagonal con goma incluida")
            .costPrice(new BigDecimal("500"))
            .salePrice(new BigDecimal("1000"))
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Caja de Colores Faber-Castell x12")
            .description(
              "Caja de 12 lápices de colores, punta resistente, colores vivos"
            )
            .costPrice(new BigDecimal("5500"))
            .salePrice(new BigDecimal("9500"))
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Kit Geométrico Faber-Castell")
            .description(
              "Kit de regla 30cm, escuadras, transportador y compás de precisión"
            )
            .costPrice(new BigDecimal("7500"))
            .salePrice(new BigDecimal("12000"))
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Borrador Nata Pelikan")
            .description(
              "Borrador de nata, suave, no mancha el papel, alta durabilidad"
            )
            .costPrice(new BigDecimal("800"))
            .salePrice(new BigDecimal("1500"))
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Grapadora Metálica Rank")
            .description(
              "Grapadora metálica de escritorio, capacidad 20 hojas, color negro"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("15000"))
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Perforadora de Papel Rank")
            .description(
              "Perforadora metálica de 2 huecos, capacidad 20 hojas, color negro"
            )
            .costPrice(new BigDecimal("10000"))
            .salePrice(new BigDecimal("18000"))
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Caja de Clips Estándar x100")
            .description(
              "Caja con 100 clips metálicos estándar, 33mm, acabado plateado"
            )
            .costPrice(new BigDecimal("1500"))
            .salePrice(new BigDecimal("3000"))
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Set de Bandejas Organizadoras x3")
            .description(
              "Set de 3 bandejas plásticas apilables para documentos, color negro"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("22000"))
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Dispensador de Cinta Adhesiva")
            .description(
              "Dispensador de cinta adhesiva de escritorio, base pesada antideslizante"
            )
            .costPrice(new BigDecimal("3500"))
            .salePrice(new BigDecimal("6500"))
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Resma Papel Bond A4")
            .description(
              "Resma de papel bond A4, 75g, 500 hojas, blanco, multifuncional"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("20000"))
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Sobre Manila Carta x50")
            .description(
              "Paquete de 50 sobres manila tamaño carta, color kraft"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("11000"))
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Cartulina Blanca Pliego")
            .description(
              "Pliego de cartulina blanca, 70x100cm, 180g, acabado mate"
            )
            .costPrice(new BigDecimal("700"))
            .salePrice(new BigDecimal("1300"))
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Block Iris Carta")
            .description(
              "Block de papel iris tamaño carta, 20 hojas, colores surtidos"
            )
            .costPrice(new BigDecimal("3500"))
            .salePrice(new BigDecimal("6000"))
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Papel Adhesivo Carta x20")
            .description(
              "Paquete de 20 hojas de papel adhesivo blanco, tamaño carta"
            )
            .costPrice(new BigDecimal("4000"))
            .salePrice(new BigDecimal("7500"))
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Tarjeta Felicitación Surtida")
            .description(
              "Tarjeta de felicitación con sobre, diseños variados para toda ocasión"
            )
            .costPrice(new BigDecimal("1500"))
            .salePrice(new BigDecimal("3500"))
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Papel de Regalo Surtido")
            .description(
              "Pliego de papel de regalo, diseños variados, 70x100cm"
            )
            .costPrice(new BigDecimal("1000"))
            .salePrice(new BigDecimal("2000"))
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Muñeco Anime Pequeño")
            .description(
              "Figura decorativa de personaje anime, 10cm de altura, varios modelos"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("12000"))
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Caja de Regalo Decorada")
            .description(
              "Caja de regalo decorativa, tamaño mediano, con moño incluido"
            )
            .costPrice(new BigDecimal("2500"))
            .salePrice(new BigDecimal("5500"))
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Set Tarjetas Motivacionales x10")
            .description(
              "Conjunto de 10 tarjetas pequeñas con mensajes motivacionales"
            )
            .costPrice(new BigDecimal("3000"))
            .salePrice(new BigDecimal("6000"))
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Memoria USB 16GB")
            .description(
              "Memoria USB 16GB, conexión 2.0, carcasa plástica resistente"
            )
            .costPrice(new BigDecimal("15000"))
            .salePrice(new BigDecimal("25000"))
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Mouse Óptico USB")
            .description(
              "Mouse óptico USB con cable, resolución 1000 DPI, diseño ergonómico"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("20000"))
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Funda para Laptop 14\"")
            .description(
              "Funda protectora para laptop de 14 pulgadas, acolchada, impermeable"
            )
            .costPrice(new BigDecimal("18000"))
            .salePrice(new BigDecimal("30000"))
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Audífonos con Cable")
            .description(
              "Audífonos con cable, conector 3.5mm, control de volumen integrado"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("15000"))
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Adaptador HDMI a VGA")
            .description(
              "Adaptador conversor de HDMI a VGA, compatible con PC y dispositivos móviles"
            )
            .costPrice(new BigDecimal("10000"))
            .salePrice(new BigDecimal("18000"))
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Set Acuarelas x12")
            .description(
              "Set de 12 pastillas de acuarela con pincel incluido, colores surtidos"
            )
            .costPrice(new BigDecimal("7000"))
            .salePrice(new BigDecimal("14000"))
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Block Papel Acuarela A4")
            .description(
              "Block de papel para acuarela, 10 hojas, 300g, tamaño A4, grano fino"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("12000"))
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Set Pinceles Artísticos x5")
            .description(
              "Set de 5 pinceles de diferentes tamaños para técnicas húmedas"
            )
            .costPrice(new BigDecimal("5000"))
            .salePrice(new BigDecimal("10000"))
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Lienzo para Pintura 30x40cm")
            .description(
              "Lienzo de algodón montado en bastidor de madera, 30x40cm, imprimado"
            )
            .costPrice(new BigDecimal("9000"))
            .salePrice(new BigDecimal("16000"))
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Lápices de Dibujo Profesionales x6")
            .description(
              "Set de 6 lápices de grafito para dibujo artístico, diferentes durezas"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("15000"))
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Bolígrafo Kilométrico 100 x12")
            .description(
              "Caja de 12 bolígrafos Kilométrico 100, punta media, color azul"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("10000"))
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Pluma Estilográfica Básica")
            .description(
              "Pluma estilográfica con cartucho recargable, cuerpo metálico, punta fina"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("22000"))
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Marcadores Permanentes x4")
            .description(
              "Set de 4 marcadores permanentes, colores básicos, punta biselada"
            )
            .costPrice(new BigDecimal("5000"))
            .salePrice(new BigDecimal("9000"))
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Resaltadores Neón x5")
            .description(
              "Set de 5 resaltadores en colores neón, punta biselada, tinta fluorescente"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("11000"))
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Lápiz Corrector Líquido")
            .description(
              "Lápiz corrector líquido de secado rápido, punta metálica de precisión"
            )
            .costPrice(new BigDecimal("2500"))
            .salePrice(new BigDecimal("4500"))
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Cuaderno Argollado 5 Materias")
            .description(
              "Cuaderno argollado de 5 materias, 200 hojas, separadores de colores"
            )
            .costPrice(new BigDecimal("9000"))
            .salePrice(new BigDecimal("16000"))
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Libreta de Bolsillo Cuadriculada")
            .description(
              "Libreta de bolsillo, 80 hojas cuadriculadas, tapa dura, 9x14cm"
            )
            .costPrice(new BigDecimal("2500"))
            .salePrice(new BigDecimal("5000"))
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Agenda Anual Ejecutiva")
            .description(
              "Agenda anual ejecutiva, una página por día, tapa dura, cinta separadora"
            )
            .costPrice(new BigDecimal("15000"))
            .salePrice(new BigDecimal("25000"))
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Block de Notas Adhesivas Neón")
            .description(
              "Block de 5 colores neón de notas adhesivas, 100 hojas cada color"
            )
            .costPrice(new BigDecimal("3000"))
            .salePrice(new BigDecimal("6000"))
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Diario Personal con Candado")
            .description(
              "Diario personal con candado, tapa acolchada, 200 páginas, 15x21cm"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("20000"))
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Archivador AZ Carta")
            .description(
              "Archivador AZ tamaño carta, lomo ancho, con palanca y gancho"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("15000"))
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Carpeta Plástica con Gancho")
            .description(
              "Carpeta plástica con gancho legajador, colores surtidos, tamaño carta"
            )
            .costPrice(new BigDecimal("2000"))
            .salePrice(new BigDecimal("4000"))
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Separadores Plásticos x5")
            .description(
              "Juego de 5 separadores plásticos, colores surtidos, tamaño carta"
            )
            .costPrice(new BigDecimal("1500"))
            .salePrice(new BigDecimal("3000"))
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Caja Organizadora Plástica")
            .description(
              "Caja organizadora plástica con tapa, capacidad 5 litros, transparente"
            )
            .costPrice(new BigDecimal("7000"))
            .salePrice(new BigDecimal("12000"))
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Mochila Escolar Básica")
            .description(
              "Mochila escolar con 2 compartimientos, bolsillo lateral, acolchada"
            )
            .costPrice(new BigDecimal("25000"))
            .salePrice(new BigDecimal("45000"))
            .category(categories.get(9))
            .build(),
          Product.builder()
            .name("Cartuchera de Tela Simple")
            .description(
              "Cartuchera de tela con cierre, capacidad para 10 lápices, diseños surtidos"
            )
            .costPrice(new BigDecimal("4000"))
            .salePrice(new BigDecimal("8000"))
            .category(categories.get(9))
            .build(),
          Product.builder()
            .name("Ábaco Infantil de Colores")
            .description(
              "Ábaco de madera con cuentas de colores, 10 filas, 10 cuentas por fila"
            )
            .costPrice(new BigDecimal("15000"))
            .salePrice(new BigDecimal("25000"))
            .category(categories.get(10))
            .build(),
          Product.builder()
            .name("Mapamundi Didáctico")
            .description(
              "Mapamundi escolar didáctico plastificado, 50x70cm, doble cara"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("20000"))
            .category(categories.get(10))
            .build(),
          Product.builder()
            .name("Set Arcilla para Modelar")
            .description(
              "Set de arcilla para modelar, 4 colores básicos, no tóxica, 500g"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("14000"))
            .category(categories.get(11))
            .build(),
          Product.builder()
            .name("Kit Bisutería Básico")
            .description(
              "Kit para elaboración de bisutería, incluye cuentas, hilos y herramientas"
            )
            .costPrice(new BigDecimal("18000"))
            .salePrice(new BigDecimal("30000"))
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

      List<Role> roles = roleRepository.findAll();
      Set<Role> adminRoles = new HashSet<>();
      Set<Role> employeeRoles = new HashSet<>();

      if (!roles.isEmpty()) {
        roles
          .stream()
          .filter(role -> role.getName().equals("ADMIN"))
          .findFirst()
          .ifPresent(adminRoles::add);

        roles
          .stream()
          .filter(role -> role.getName().equals("EMPLOYEE"))
          .findFirst()
          .ifPresent(employeeRoles::add);
      }

      User adminUser = User.builder()
        .name("Rosita González")
        .email("rosita@sigrap.com")
        .password(passwordEncoder.encode("Rosita123*"))
        .status(User.UserStatus.ACTIVE)
        .roles(adminRoles)
        .build();

      User employeeUser = User.builder()
        .name("Gladys Mendoza")
        .email("gladys@sigrap.com")
        .password(passwordEncoder.encode("Gladys123*"))
        .status(User.UserStatus.ACTIVE)
        .roles(employeeRoles)
        .build();

      userRepository.saveAll(List.of(adminUser, employeeUser));
      log.info("Users seeded successfully.");
    } else {
      log.info("Users already exist, skipping seeding.");
    }
  }

  /**
   * Seeds initial permissions into the database.
   * Creates a set of basic permissions for different resources and actions.
   * Only executes if the permissions table is empty.
   */
  private void seedPermissions() {
    if (permissionRepository.count() == 0) {
      log.info("Seeding permissions...");

      LocalDateTime now = LocalDateTime.now();

      permissionRepository.saveAll(
        List.of(
          Permission.builder()
            .name("USER_READ")
            .description("Permission to read user information")
            .resource("USER")
            .action("READ")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("USER_CREATE")
            .description("Permission to create new users")
            .resource("USER")
            .action("CREATE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("USER_UPDATE")
            .description("Permission to update user information")
            .resource("USER")
            .action("UPDATE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("USER_DELETE")
            .description("Permission to delete users")
            .resource("USER")
            .action("DELETE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("PRODUCT_READ")
            .description("Permission to read product information")
            .resource("PRODUCT")
            .action("READ")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("PRODUCT_CREATE")
            .description("Permission to create new products")
            .resource("PRODUCT")
            .action("CREATE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("PRODUCT_UPDATE")
            .description("Permission to update product information")
            .resource("PRODUCT")
            .action("UPDATE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("PRODUCT_DELETE")
            .description("Permission to delete products")
            .resource("PRODUCT")
            .action("DELETE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("CATEGORY_READ")
            .description("Permission to read category information")
            .resource("CATEGORY")
            .action("READ")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("CATEGORY_CREATE")
            .description("Permission to create new categories")
            .resource("CATEGORY")
            .action("CREATE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("CATEGORY_UPDATE")
            .description("Permission to update category information")
            .resource("CATEGORY")
            .action("UPDATE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("CATEGORY_DELETE")
            .description("Permission to delete categories")
            .resource("CATEGORY")
            .action("DELETE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("ROLE_READ")
            .description("Permission to read role information")
            .resource("ROLE")
            .action("READ")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("ROLE_CREATE")
            .description("Permission to create new roles")
            .resource("ROLE")
            .action("CREATE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("ROLE_UPDATE")
            .description("Permission to update role information")
            .resource("ROLE")
            .action("UPDATE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("ROLE_DELETE")
            .description("Permission to delete roles")
            .resource("ROLE")
            .action("DELETE")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("PERMISSION_READ")
            .description("Permission to read permission information")
            .resource("PERMISSION")
            .action("READ")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("PERMISSION_ASSIGN")
            .description("Permission to assign permissions to roles")
            .resource("PERMISSION")
            .action("ASSIGN")
            .createdAt(now)
            .updatedAt(now)
            .build(),
          Permission.builder()
            .name("AUDIT_READ")
            .description("Permission to read audit logs")
            .resource("AUDIT")
            .action("READ")
            .createdAt(now)
            .updatedAt(now)
            .build()
        )
      );

      log.info("Permissions seeded successfully.");
    } else {
      log.info("Permissions already exist, skipping seeding.");
    }
  }

  /**
   * Seeds initial roles into the database.
   * Creates standard roles with appropriate permissions.
   * Only executes if the roles table is empty.
   */
  private void seedRoles() {
    if (roleRepository.count() == 0) {
      log.info("Seeding roles...");

      LocalDateTime now = LocalDateTime.now();

      List<Permission> permissions = permissionRepository.findAll();
      if (permissions.isEmpty()) {
        log.warn("No permissions found for role seeding.");
        return;
      }

      Role adminRole = Role.builder()
        .name("Administrador")
        .description("Rol de Administrador con acceso total al sistema")
        .permissions(new HashSet<>(permissions))
        .createdAt(now)
        .updatedAt(now)
        .build();

      Set<Permission> employeePermissions = new HashSet<>();
      permissions
        .stream()
        .filter(
          p ->
            p.getAction().equals("READ") ||
            (p.getResource().equals("PRODUCT") &&
              !p.getAction().equals("DELETE"))
        )
        .forEach(employeePermissions::add);

      Role employeeRole = Role.builder()
        .name("Empleado")
        .description("Rol de Empleado con acceso limitado al sistema")
        .permissions(employeePermissions)
        .createdAt(now)
        .updatedAt(now)
        .build();

      roleRepository.saveAll(List.of(adminRole, employeeRole));
      log.info("Roles seeded successfully.");
    } else {
      log.info("Roles already exist, skipping seeding.");
    }
  }

  /**
   * Seeds initial notification preferences for users.
   * Creates default notification settings for each user in the system.
   * Only executes if there are users but no notification preferences.
   */
  private void seedUserNotificationPreferences() {
    if (
      userNotificationPreferenceRepository.count() == 0 &&
      userRepository.count() > 0
    ) {
      log.info("Seeding user notification preferences...");

      List<User> users = userRepository.findAll();
      List<UserNotificationPreference> preferences = new ArrayList<>();

      for (User user : users) {
        for (NotificationType type : NotificationType.values()) {
          preferences.add(
            UserNotificationPreference.builder()
              .user(user)
              .notificationType(type)
              .enabled(true)
              .emailEnabled(true)
              .pushEnabled(type == NotificationType.SECURITY)
              .build()
          );
        }
      }

      userNotificationPreferenceRepository.saveAll(preferences);
      log.info("User notification preferences seeded successfully.");
    } else {
      log.info(
        "User notification preferences already exist, skipping seeding."
      );
    }
  }

  private void seedEmployees() {
    if (employeeRepository.count() > 0) {
      log.info("Employees already seeded.");
      return;
    }

    log.info("Seeding employees...");

    List<Employee> employees = new ArrayList<>();

    User adminUser = userRepository
      .findByEmail("rosita@sigrap.com")
      .orElseThrow(() -> new RuntimeException("Admin user not found"));

    employees.add(
      Employee.builder()
        .user(adminUser)
        .firstName("Rosita")
        .lastName("González")
        .documentId("12345678")
        .phoneNumber("+573001234567")
        .email(adminUser.getEmail())
        .position("Gerente General")
        .department("Administración")
        .hireDate(LocalDateTime.now().minusYears(2))
        .status(Employee.EmployeeStatus.ACTIVE)
        .build()
    );

    User employeeUser = userRepository
      .findByEmail("gladys@sigrap.com")
      .orElseThrow(() -> new RuntimeException("Employee user not found"));

    employees.add(
      Employee.builder()
        .user(employeeUser)
        .firstName("Gladys")
        .lastName("Mendoza")
        .documentId("87654321")
        .phoneNumber("+573109876543")
        .email(employeeUser.getEmail())
        .position("Vendedor")
        .department("Ventas")
        .hireDate(LocalDateTime.now().minusMonths(6))
        .status(Employee.EmployeeStatus.ACTIVE)
        .build()
    );

    employeeRepository.saveAll(employees);
    log.info("Employees seeded successfully.");
  }

  private void seedSchedules() {
    if (scheduleRepository.count() > 0) {
      log.info("Schedules already seeded.");
      return;
    }

    log.info("Seeding schedules...");

    List<Schedule> schedules = new ArrayList<>();
    List<Employee> employees = employeeRepository.findAll();

    for (Employee employee : employees) {
      for (String day : List.of(
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY"
      )) {
        schedules.add(
          Schedule.builder()
            .employee(employee)
            .day(day)
            .startTime(LocalDateTime.now().withHour(8).withMinute(0))
            .endTime(LocalDateTime.now().withHour(17).withMinute(0))
            .isActive(true)
            .build()
        );
      }

      if ("Vendedor".equals(employee.getPosition())) {
        schedules.add(
          Schedule.builder()
            .employee(employee)
            .day("SATURDAY")
            .startTime(LocalDateTime.now().withHour(8).withMinute(0))
            .endTime(LocalDateTime.now().withHour(13).withMinute(0))
            .isActive(true)
            .build()
        );
      }
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
    List<Employee> employees = employeeRepository.findAll();
    LocalDateTime now = LocalDateTime.now();

    for (Employee employee : employees) {
      for (int i = 0; i < 7; i++) {
        LocalDateTime date = now.minusDays(i);

        if (
          date.getDayOfWeek().getValue() > 5 &&
          !"Vendedor".equals(employee.getPosition())
        ) {
          continue;
        }

        Attendance.AttendanceStatus status =
          Attendance.AttendanceStatus.PRESENT;
        String notes = "Asistencia regular";

        if (i == 2) {
          status = Attendance.AttendanceStatus.LATE;
          notes = "Llegó 15 minutos tarde debido al tráfico";
        } else if (i == 5) {
          status = Attendance.AttendanceStatus.ON_LEAVE;
          notes = "Permiso por asuntos personales";
        }

        attendanceRecords.add(
          Attendance.builder()
            .employee(employee)
            .date(date)
            .clockInTime(date.withHour(8).withMinute(0))
            .clockOutTime(date.withHour(17).withMinute(0))
            .status(status)
            .notes(notes)
            .build()
        );
      }
    }

    attendanceRepository.saveAll(attendanceRecords);
    log.info("Attendance records seeded successfully.");
  }

  private void seedEmployeePerformance() {
    if (employeePerformanceRepository.count() > 0) {
      log.info("Performance records already seeded.");
      return;
    }

    log.info("Seeding performance records...");

    List<EmployeePerformance> performanceRecords = new ArrayList<>();
    List<Employee> employees = employeeRepository.findAll();
    LocalDateTime now = LocalDateTime.now();

    for (Employee employee : employees) {
      for (int i = 0; i < 3; i++) {
        LocalDateTime periodStart = now.minusMonths(i).withDayOfMonth(1);
        LocalDateTime periodEnd = periodStart.plusMonths(1).minusDays(1);

        int salesCount = 0;
        BigDecimal salesTotal = BigDecimal.ZERO;
        int rating = 0;
        String notes = "";

        switch (employee.getPosition()) {
          case "Vendedor":
            salesCount = 80 + random.nextInt(40);
            salesTotal = BigDecimal.valueOf(
              salesCount * 100 * (1 + random.nextDouble())
            );
            rating = 85 + random.nextInt(15);
            notes = "Excelente desempeño en ventas. Buena atención al cliente.";
            break;
          case "Almacenero":
            salesCount = 150 + random.nextInt(50);
            salesTotal = BigDecimal.valueOf(salesCount * 50);
            rating = 80 + random.nextInt(15);
            notes =
              "Eficiente manejo de inventario. Mantiene el almacén organizado.";
            break;
          case "Gerente General":
            salesCount = 20 + random.nextInt(10);
            salesTotal = BigDecimal.valueOf(10000 + random.nextDouble() * 5000);
            rating = 90 + random.nextInt(10);
            notes =
              "Liderazgo efectivo. Cumplimiento de objetivos del departamento.";
            break;
        }

        performanceRecords.add(
          EmployeePerformance.builder()
            .employee(employee)
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .salesCount(salesCount)
            .salesTotal(salesTotal)
            .rating(rating)
            .notes(notes)
            .build()
        );
      }
    }

    employeePerformanceRepository.saveAll(performanceRecords);
    log.info("Performance records seeded successfully.");
  }

  private void seedActivityLogs() {
    if (activityLogRepository.count() > 0) {
      log.info("Activity logs already seeded.");
      return;
    }

    log.info("Seeding activity logs...");

    List<ActivityLog> activityLogs = new ArrayList<>();
    List<Employee> employees = employeeRepository.findAll();
    LocalDateTime now = LocalDateTime.now();

    for (Employee employee : employees) {
      activityLogs.add(
        ActivityLog.builder()
          .employee(employee)
          .timestamp(now.minusDays(1))
          .actionType(ActivityLog.ActionType.LOGIN)
          .description("Inicio de sesión exitoso")
          .moduleName("auth")
          .ipAddress("192.168.1.100")
          .build()
      );

      activityLogs.add(
        ActivityLog.builder()
          .employee(employee)
          .timestamp(now.minusDays(1).plusHours(9))
          .actionType(ActivityLog.ActionType.VIEW)
          .description("Consulta de inventario")
          .moduleName("inventory")
          .ipAddress("192.168.1.100")
          .build()
      );

      if ("Vendedor".equals(employee.getPosition())) {
        activityLogs.add(
          ActivityLog.builder()
            .employee(employee)
            .timestamp(now.minusDays(1).plusHours(10))
            .actionType(ActivityLog.ActionType.CREATE)
            .description("Registro de nueva venta")
            .moduleName("sales")
            .entityId("SALE-001")
            .ipAddress("192.168.1.100")
            .build()
        );
      }

      if ("Almacenero".equals(employee.getPosition())) {
        activityLogs.add(
          ActivityLog.builder()
            .employee(employee)
            .timestamp(now.minusDays(1).plusHours(11))
            .actionType(ActivityLog.ActionType.UPDATE)
            .description("Actualización de stock de producto")
            .moduleName("inventory")
            .entityId("PROD-001")
            .ipAddress("192.168.1.100")
            .build()
        );
      }

      if ("Gerente General".equals(employee.getPosition())) {
        activityLogs.add(
          ActivityLog.builder()
            .employee(employee)
            .timestamp(now.minusDays(1).plusHours(14))
            .actionType(ActivityLog.ActionType.EXPORT)
            .description("Generación de reporte de ventas")
            .moduleName("reports")
            .ipAddress("192.168.1.100")
            .build()
        );
      }

      activityLogs.add(
        ActivityLog.builder()
          .employee(employee)
          .timestamp(now.minusDays(1).plusHours(17))
          .actionType(ActivityLog.ActionType.LOGOUT)
          .description("Cierre de sesión")
          .moduleName("auth")
          .ipAddress("192.168.1.100")
          .build()
      );
    }

    activityLogRepository.saveAll(activityLogs);
    log.info("Activity logs seeded successfully.");
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
          .taxId("900123456-7")
          .contactPerson("Miguel Sánchez")
          .email("msanchez@officedepot.com.co")
          .phone("(601) 555-2700")
          .address("Carrera 15 # 80-25, Bogotá D.C.")
          .paymentMethod(PaymentMethod.BANK_TRANSFER)
          .status(SupplierStatus.ACTIVE)
          .website("https://www.officedepot.com.co")
          .paymentTerms("30 días")
          .notes(
            "Proveedor principal de artículos de oficina y escolares en Colombia"
          )
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Panamericana")
          .taxId("860000123-4")
          .contactPerson("Ana Campos")
          .email("acampos@panamericana.com.co")
          .phone("(601) 337-9000")
          .address("Avenida Chile # 72-41, Bogotá D.C.")
          .paymentMethod(PaymentMethod.CREDIT_CARD)
          .status(SupplierStatus.ACTIVE)
          .website("https://www.panamericana.com.co")
          .paymentTerms("15 días")
          .notes(
            "Distribuidor mayorista de útiles escolares y de oficina en Colombia"
          )
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Artesco Colombia")
          .taxId("900567890-1")
          .contactPerson("Roberto Gutiérrez")
          .email("rgutierrez@artesco.com.co")
          .phone("(604) 444-6000")
          .address("Calle 10 # 43E-115, Medellín, Antioquia")
          .paymentMethod(PaymentMethod.BANK_TRANSFER)
          .status(SupplierStatus.ACTIVE)
          .website("https://www.artesco.com.co")
          .paymentTerms("45 días")
          .notes(
            "Fabricante de útiles escolares de alta calidad con presencia en Colombia"
          )
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Scribe Colombia")
          .taxId("830000456-7")
          .contactPerson("María Elena Torres")
          .email("metorres@scribe.com.co")
          .phone("(602) 660-2500")
          .address("Carrera 1 # 23-89, Cali, Valle del Cauca")
          .paymentMethod(PaymentMethod.CHECK)
          .status(SupplierStatus.ACTIVE)
          .website("https://www.scribe.com.co")
          .paymentTerms("30 días")
          .notes("Especialista en materiales artísticos y cuadernos")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Faber-Castell Colombia")
          .taxId("800123789-0")
          .contactPerson("Jorge Reátegui")
          .email("jreategui@faber-castell.com.co")
          .phone("(601) 744-4800")
          .address("Autopista Norte Km 19, Chía, Cundinamarca")
          .paymentMethod(PaymentMethod.BANK_TRANSFER)
          .status(SupplierStatus.ACTIVE)
          .website("https://www.faber-castell.com.co")
          .paymentTerms("45 días")
          .notes(
            "Proveedor global de instrumentos de escritura y dibujo en Colombia"
          )
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Pegaucho")
          .taxId("890987654-3")
          .contactPerson("Lucía Pérez")
          .email("lperez@pegaucho.com.co")
          .phone("(601) 222-2300")
          .address("Calle 13 # 68-78, Bogotá D.C.")
          .paymentMethod(PaymentMethod.CASH)
          .status(SupplierStatus.ACTIVE)
          .website("https://www.pegaucho.com.co")
          .paymentTerms("Contado")
          .notes("Fabricante colombiano de pegamentos y adhesivos")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Norma Colombia")
          .taxId("800234567-8")
          .contactPerson("Daniel Quispe")
          .email("dquispe@norma.com.co")
          .phone("(601) 423-8000")
          .address("Calle 100 # 19A-50, Bogotá D.C.")
          .paymentMethod(PaymentMethod.BANK_TRANSFER)
          .status(SupplierStatus.ACTIVE)
          .website("https://www.norma.com.co")
          .paymentTerms("30 días")
          .notes("Especialista en cuadernos y productos de papel en Colombia")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("DistriOffice")
          .taxId("900876543-2")
          .contactPerson("Patricia Rojas")
          .email("projas@distrioffice.com.co")
          .phone("(604) 333-1800")
          .address("Carrera 48 # 10-45, Medellín, Antioquia")
          .paymentMethod(PaymentMethod.CHECK)
          .status(SupplierStatus.INACTIVE)
          .website("https://www.distrioffice.com.co")
          .paymentTerms("45 días")
          .notes(
            "Distribuidor de productos importados para oficina, actualmente inactivo"
          )
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Pilot Colombia")
          .taxId("830765432-1")
          .contactPerson("Carlos Mendoza")
          .email("cmendoza@pilotcolombia.com")
          .phone("(601) 617-5200")
          .address("Carrera 7 # 71-21, Bogotá D.C.")
          .paymentMethod(PaymentMethod.BANK_TRANSFER)
          .status(SupplierStatus.ACTIVE)
          .website("https://www.pilotpen.com.co")
          .paymentTerms("30 días")
          .notes("Importador oficial de productos Pilot en Colombia")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Papeles Nacionales S.A.S.")
          .taxId("860987123-0")
          .contactPerson("Fernando Torres")
          .email("ftorres@papelesnacionales.com.co")
          .phone("(601) 742-9000")
          .address("Zona Franca de Bogotá, Bodega 12, Bogotá D.C.")
          .paymentMethod(PaymentMethod.BANK_TRANSFER)
          .status(SupplierStatus.PROBATION)
          .website("https://www.papelesnacionales.com.co")
          .paymentTerms("60 días")
          .notes(
            "Fabricante de productos de papel y cuadernos, en periodo de prueba"
          )
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

    Supplier panamericana = findSupplierByName(suppliers, "Panamericana");
    Supplier faberCastell = findSupplierByName(
      suppliers,
      "Faber-Castell Colombia"
    );
    Supplier artesco = findSupplierByName(suppliers, "Artesco Colombia");
    Supplier officedepot = findSupplierByName(suppliers, "Office Depot");
    Supplier norma = findSupplierByName(suppliers, "Norma Colombia");

    Product cuadernoUniversitario = findProductByName(
      products,
      "Cuaderno Universitario Norma"
    );
    Product lapizMirado = findProductByName(products, "Lápiz Mirado HB");
    Product cajaColores = findProductByName(
      products,
      "Caja de Colores Faber-Castell x12"
    );
    Product kitGeometrico = findProductByName(
      products,
      "Kit Geométrico Faber-Castell"
    );
    Product borrador = findProductByName(products, "Borrador Nata Pelikan");
    Product resmaA4 = findProductByName(products, "Resma Papel Bond A4");
    Product cartulinaBlanca = findProductByName(
      products,
      "Cartulina Blanca Pliego"
    );
    Product blockIris = findProductByName(products, "Block Iris Carta");
    Product marcadoresPermanentes = findProductByName(
      products,
      "Marcadores Permanentes x4"
    );
    Product cuadernoArgollado = findProductByName(
      products,
      "Cuaderno Argollado 5 Materias"
    );

    List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    PurchaseOrder orden1 = PurchaseOrder.builder()
      .orderNumber("OC-2025-001")
      .supplier(panamericana)
      .orderDate(LocalDate.now().minusDays(45))
      .expectedDeliveryDate(LocalDate.now().minusDays(35))
      .actualDeliveryDate(LocalDate.now().minusDays(34))
      .status(PurchaseOrder.Status.DELIVERED)
      .notes(
        "Pedido para abastecer inventario de útiles escolares por temporada de regreso a clases"
      )
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item11 = PurchaseOrderItem.builder()
      .product(cuadernoUniversitario)
      .quantity(100)
      .unitPrice(new BigDecimal("4000"))
      .receivedQuantity(100)
      .notes("Cuadernos para stock inicial de temporada escolar")
      .build();

    PurchaseOrderItem item12 = PurchaseOrderItem.builder()
      .product(lapizMirado)
      .quantity(200)
      .unitPrice(new BigDecimal("500"))
      .receivedQuantity(200)
      .notes("Lápices HB para stock inicial de temporada escolar")
      .build();

    PurchaseOrderItem item13 = PurchaseOrderItem.builder()
      .product(borrador)
      .quantity(150)
      .unitPrice(new BigDecimal("800"))
      .receivedQuantity(150)
      .notes("Borradores para stock inicial de temporada escolar")
      .build();

    item11.setPurchaseOrder(orden1);
    item12.setPurchaseOrder(orden1);
    item13.setPurchaseOrder(orden1);
    orden1.getItems().add(item11);
    orden1.getItems().add(item12);
    orden1.getItems().add(item13);

    BigDecimal total1 = calculateOrderTotal(orden1);
    orden1.setTotalAmount(total1);
    purchaseOrders.add(orden1);

    PurchaseOrder orden2 = PurchaseOrder.builder()
      .orderNumber("OC-2025-002")
      .supplier(faberCastell)
      .orderDate(LocalDate.now().minusDays(30))
      .expectedDeliveryDate(LocalDate.now().minusDays(20))
      .actualDeliveryDate(LocalDate.now().minusDays(22))
      .status(PurchaseOrder.Status.DELIVERED)
      .notes("Pedido de materiales artísticos para taller de arte")
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item21 = PurchaseOrderItem.builder()
      .product(cajaColores)
      .quantity(50)
      .unitPrice(new BigDecimal("5500"))
      .receivedQuantity(50)
      .notes("Cajas de colores para taller de arte")
      .build();

    PurchaseOrderItem item22 = PurchaseOrderItem.builder()
      .product(kitGeometrico)
      .quantity(30)
      .unitPrice(new BigDecimal("7500"))
      .receivedQuantity(30)
      .notes("Kits geométricos para estudiantes de secundaria")
      .build();

    item21.setPurchaseOrder(orden2);
    item22.setPurchaseOrder(orden2);
    orden2.getItems().add(item21);
    orden2.getItems().add(item22);

    BigDecimal total2 = calculateOrderTotal(orden2);
    orden2.setTotalAmount(total2);
    purchaseOrders.add(orden2);

    PurchaseOrder orden3 = PurchaseOrder.builder()
      .orderNumber("OC-2025-003")
      .supplier(officedepot)
      .orderDate(LocalDate.now().minusDays(15))
      .expectedDeliveryDate(LocalDate.now().minusDays(7))
      .actualDeliveryDate(LocalDate.now().minusDays(8))
      .status(PurchaseOrder.Status.DELIVERED)
      .notes("Reposición de stock de papel y cartulina")
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item31 = PurchaseOrderItem.builder()
      .product(resmaA4)
      .quantity(80)
      .unitPrice(new BigDecimal("12000"))
      .receivedQuantity(80)
      .notes("Resmas de papel para fotocopias e impresiones")
      .build();

    PurchaseOrderItem item32 = PurchaseOrderItem.builder()
      .product(cartulinaBlanca)
      .quantity(120)
      .unitPrice(new BigDecimal("700"))
      .receivedQuantity(120)
      .notes("Cartulinas para proyectos escolares")
      .build();

    PurchaseOrderItem item33 = PurchaseOrderItem.builder()
      .product(blockIris)
      .quantity(60)
      .unitPrice(new BigDecimal("3500"))
      .receivedQuantity(60)
      .notes("Blocks de papel iris para manualidades")
      .build();

    item31.setPurchaseOrder(orden3);
    item32.setPurchaseOrder(orden3);
    item33.setPurchaseOrder(orden3);
    orden3.getItems().add(item31);
    orden3.getItems().add(item32);
    orden3.getItems().add(item33);

    BigDecimal total3 = calculateOrderTotal(orden3);
    orden3.setTotalAmount(total3);
    purchaseOrders.add(orden3);

    PurchaseOrder orden4 = PurchaseOrder.builder()
      .orderNumber("OC-2025-004")
      .supplier(artesco)
      .orderDate(LocalDate.now().minusDays(5))
      .expectedDeliveryDate(LocalDate.now().plusDays(3))
      .status(PurchaseOrder.Status.CONFIRMED)
      .notes("Pedido urgente de marcadores para evento escolar")
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item41 = PurchaseOrderItem.builder()
      .product(marcadoresPermanentes)
      .quantity(40)
      .unitPrice(new BigDecimal("5000"))
      .receivedQuantity(0)
      .notes("Marcadores para taller de cartelería")
      .build();

    item41.setPurchaseOrder(orden4);
    orden4.getItems().add(item41);

    BigDecimal total4 = calculateOrderTotal(orden4);
    orden4.setTotalAmount(total4);
    purchaseOrders.add(orden4);

    PurchaseOrder orden5 = PurchaseOrder.builder()
      .orderNumber("OC-2025-005")
      .supplier(norma)
      .orderDate(LocalDate.now().minusDays(2))
      .expectedDeliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrder.Status.SUBMITTED)
      .notes("Pedido para reponer stock de cuadernos")
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item51 = PurchaseOrderItem.builder()
      .product(cuadernoUniversitario)
      .quantity(50)
      .unitPrice(new BigDecimal("4000"))
      .receivedQuantity(0)
      .notes("Reposición de stock de cuadernos universitarios")
      .build();

    PurchaseOrderItem item52 = PurchaseOrderItem.builder()
      .product(cuadernoArgollado)
      .quantity(35)
      .unitPrice(new BigDecimal("9000"))
      .receivedQuantity(0)
      .notes("Reposición de stock de cuadernos argollados")
      .build();

    item51.setPurchaseOrder(orden5);
    item52.setPurchaseOrder(orden5);
    orden5.getItems().add(item51);
    orden5.getItems().add(item52);

    BigDecimal total5 = calculateOrderTotal(orden5);
    orden5.setTotalAmount(total5);
    purchaseOrders.add(orden5);

    PurchaseOrder orden6 = PurchaseOrder.builder()
      .orderNumber("OC-2025-006")
      .supplier(panamericana)
      .orderDate(LocalDate.now())
      .expectedDeliveryDate(LocalDate.now().plusDays(15))
      .status(PurchaseOrder.Status.DRAFT)
      .notes("Borrador de pedido para próximo mes")
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item61 = PurchaseOrderItem.builder()
      .product(lapizMirado)
      .quantity(150)
      .unitPrice(new BigDecimal("500"))
      .receivedQuantity(0)
      .notes("Stock preventivo para próximo trimestre")
      .build();

    PurchaseOrderItem item62 = PurchaseOrderItem.builder()
      .product(borrador)
      .quantity(100)
      .unitPrice(new BigDecimal("800"))
      .receivedQuantity(0)
      .notes("Stock preventivo para próximo trimestre")
      .build();

    item61.setPurchaseOrder(orden6);
    item62.setPurchaseOrder(orden6);
    orden6.getItems().add(item61);
    orden6.getItems().add(item62);

    BigDecimal total6 = calculateOrderTotal(orden6);
    orden6.setTotalAmount(total6);
    purchaseOrders.add(orden6);

    purchaseOrderRepository.saveAll(purchaseOrders);
    log.info("{} purchase orders seeded successfully.", purchaseOrders.size());
  }

  private void seedPurchaseOrderTrackingEvents() {
    if (purchaseOrderTrackingEventRepository.count() == 0) {
      log.info("Seeding purchase order tracking events...");
      List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
      List<PurchaseOrderTrackingEvent> trackingEvents = new ArrayList<>();

      if (purchaseOrders.isEmpty()) {
        log.warn(
          "No purchase orders found to seed tracking events. Skipping this step."
        );
        return;
      }

      for (PurchaseOrder order : purchaseOrders) {
        LocalDateTime eventDate = order
          .getOrderDate()
          .atStartOfDay()
          .plusHours(random.nextInt(24));
        String baseLocation = "Bogotá DC";

        trackingEvents.add(
          PurchaseOrderTrackingEvent.builder()
            .purchaseOrder(order)
            .eventTimestamp(eventDate)
            .status("ORDEN CREADA")
            .location(baseLocation)
            .notes("Pedido generado en el sistema.")
            .build()
        );

        if (
          order.getStatus() == PurchaseOrder.Status.CONFIRMED ||
          order.getStatus() == PurchaseOrder.Status.SHIPPED ||
          order.getStatus() == PurchaseOrder.Status.DELIVERED ||
          order.getStatus() == PurchaseOrder.Status.DELIVERED
        ) {
          eventDate = eventDate
            .plusDays(random.nextInt(1) + 1)
            .plusHours(random.nextInt(12));
          trackingEvents.add(
            PurchaseOrderTrackingEvent.builder()
              .purchaseOrder(order)
              .eventTimestamp(eventDate)
              .status("ORDEN CONFIRMADA")
              .location(order.getSupplier().getAddress())
              .notes("Proveedor ha confirmado el pedido.")
              .build()
          );
        }

        if (
          order.getStatus() == PurchaseOrder.Status.SHIPPED ||
          order.getStatus() == PurchaseOrder.Status.DELIVERED ||
          order.getStatus() == PurchaseOrder.Status.DELIVERED
        ) {
          eventDate = eventDate
            .plusDays(random.nextInt(2) + 1)
            .plusHours(random.nextInt(12));
          trackingEvents.add(
            PurchaseOrderTrackingEvent.builder()
              .purchaseOrder(order)
              .eventTimestamp(eventDate)
              .status("ORDEN ENVIADA")
              .location(
                "Centro de Distribución - " + order.getSupplier().getAddress()
              )
              .notes(
                "El pedido ha sido despachado por el proveedor. Transportadora: Servientrega, Guía: " +
                "GUIA" +
                String.format("%05d", order.getId()) +
                random.nextInt(100)
              )
              .build()
          );
        }

        if (
          order.getStatus() == PurchaseOrder.Status.DELIVERED ||
          order.getStatus() == PurchaseOrder.Status.DELIVERED
        ) {
          eventDate = eventDate
            .plusDays(random.nextInt(3) + 1)
            .plusHours(random.nextInt(12));
          trackingEvents.add(
            PurchaseOrderTrackingEvent.builder()
              .purchaseOrder(order)
              .eventTimestamp(eventDate)
              .status("EN TRÁNSITO")
              .location(
                "Punto de control - " + order.getSupplier().getAddress()
              )
              .notes("El pedido está en camino a la papelería.")
              .build()
          );

          eventDate = eventDate
            .plusDays(random.nextInt(2) + 1)
            .plusHours(random.nextInt(12));
          trackingEvents.add(
            PurchaseOrderTrackingEvent.builder()
              .purchaseOrder(order)
              .eventTimestamp(eventDate)
              .status("ENTREGADO")
              .location("Papelería Rosita - " + baseLocation)
              .notes("El pedido ha sido recibido en la papelería.")
              .build()
          );
        }

        if (order.getStatus() == PurchaseOrder.Status.DELIVERED) {
          eventDate = eventDate
            .plusDays(random.nextInt(1) + 1)
            .plusHours(random.nextInt(12));
          trackingEvents.add(
            PurchaseOrderTrackingEvent.builder()
              .purchaseOrder(order)
              .eventTimestamp(eventDate)
              .status("ORDEN COMPLETADA")
              .location("Papelería Rosita - " + baseLocation)
              .notes("El pedido ha sido verificado y cerrado.")
              .build()
          );
        }

        if (order.getStatus() == PurchaseOrder.Status.CANCELLED) {
          eventDate = eventDate
            .plusDays(random.nextInt(1) + 1)
            .plusHours(random.nextInt(12));
          trackingEvents.add(
            PurchaseOrderTrackingEvent.builder()
              .purchaseOrder(order)
              .eventTimestamp(eventDate)
              .status("ORDEN CANCELADA")
              .location(baseLocation)
              .notes("El pedido fue cancelado por el usuario.")
              .build()
          );
        }
      }
      purchaseOrderTrackingEventRepository.saveAll(trackingEvents);
      log.info(
        "Seeded {} purchase order tracking events.",
        trackingEvents.size()
      );
    }
  }

  private void seedPayments() {
    if (paymentRepository.count() == 0) {
      log.info("Seeding payments...");
      List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
      List<Payment> payments = new ArrayList<>();

      if (purchaseOrders.isEmpty()) {
        log.warn(
          "No purchase orders found to seed payments. Skipping this step."
        );
        return;
      }

      int paymentCounter = 0;
      PaymentMethod[] paymentMethods = {
        PaymentMethod.BANK_TRANSFER,
        PaymentMethod.CREDIT_CARD,
        PaymentMethod.NEQUI,
        PaymentMethod.DAVIPLATA,
        PaymentMethod.CASH,
      };

      for (PurchaseOrder order : purchaseOrders) {
        if (
          order.getStatus() == PurchaseOrder.Status.CONFIRMED ||
          order.getStatus() == PurchaseOrder.Status.SHIPPED ||
          order.getStatus() == PurchaseOrder.Status.DELIVERED ||
          order.getStatus() == PurchaseOrder.Status.DELIVERED
        ) {
          Payment.PaymentBuilder paymentBuilder = Payment.builder()
            .purchaseOrder(order)
            .supplier(order.getSupplier())
            .amount(order.getTotalAmount())
            .paymentMethod(
              paymentMethods[paymentCounter % paymentMethods.length]
            )
            .transactionId(
              "TRN-" +
              LocalDate.now().getYear() +
              String.format("%02d", LocalDate.now().getMonthValue()) +
              "-" +
              String.format("%05d", order.getId()) +
              random.nextInt(100)
            );

          LocalDate paymentDate = order.getOrderDate();
          LocalDate dueDate = order.getOrderDate();
          String notes = "";
          String invoiceNumber =
            "INV-" +
            order.getOrderNumber().replace("OC-", "") +
            "-" +
            String.format("%02d", paymentCounter + 1);

          switch (order.getStatus()) {
            case CONFIRMED:
              paymentBuilder.status(PaymentStatus.PENDING);
              paymentDate = order
                .getOrderDate()
                .plusDays(random.nextInt(2) + 1);
              dueDate = paymentDate.plusDays(30);
              notes =
                "Pago programado para el pedido " +
                order.getOrderNumber() +
                ". Factura: " +
                invoiceNumber;
              break;
            case SHIPPED:
              paymentBuilder.status(PaymentStatus.PROCESSING);
              paymentDate = order.getExpectedDeliveryDate() != null
                ? order
                  .getExpectedDeliveryDate()
                  .minusDays(random.nextInt(3) + 1)
                : order.getOrderDate().plusDays(random.nextInt(5) + 2);
              dueDate = paymentDate.plusDays(15);
              notes =
                "Pago en proceso para el pedido " +
                order.getOrderNumber() +
                ". Factura: " +
                invoiceNumber;
              break;
            case DELIVERED:
              paymentBuilder.status(PaymentStatus.COMPLETED);
              paymentDate = order.getExpectedDeliveryDate() != null
                ? order.getExpectedDeliveryDate().plusDays(random.nextInt(2))
                : order.getOrderDate().plusDays(random.nextInt(7) + 3);
              if (order.getShipDate() != null) {
                paymentDate = order
                  .getShipDate()
                  .plusDays(random.nextInt(2) + 1);
              }
              dueDate = paymentDate.plusDays(7);

              notes =
                "Pago completado para el pedido " +
                order.getOrderNumber() +
                ". Factura: " +
                invoiceNumber;

              if (
                paymentCounter % 3 == 0 &&
                order.getExpectedDeliveryDate() != null
              ) {
                paymentDate = order.getExpectedDeliveryDate().minusDays(1);
              } else if (
                paymentCounter % 4 == 0 &&
                order.getExpectedDeliveryDate() != null
              ) {
                paymentDate = order.getExpectedDeliveryDate().plusDays(1);
              }

              break;
            default:
              continue;
          }

          if (paymentDate.isBefore(order.getOrderDate())) {
            paymentDate = order.getOrderDate();
          }

          paymentBuilder.paymentDate(paymentDate).notes(notes);
          paymentBuilder.invoiceNumber(invoiceNumber);
          paymentBuilder.dueDate(dueDate);
          payments.add(paymentBuilder.build());
          paymentCounter++;
        }
      }
      paymentRepository.saveAll(payments);
      log.info("Seeded {} payments.", payments.size());
    }
  }

  /**
   * Find a supplier by name in a list of suppliers.
   *
   * @param suppliers List of suppliers to search in
   * @param name Name of the supplier to find
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
   * @param name Name of the product to find
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
}
