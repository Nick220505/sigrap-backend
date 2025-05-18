package com.sigrap.config;

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
   * Used to check if attendance records exist and to save new attendance records during seeding.
   */
  private final AttendanceRepository attendanceRepository;

  /**
   * Repository for supplier database operations.
   * Used to check if suppliers exist and to save new suppliers during seeding.
   */
  private final SupplierRepository supplierRepository;

  /**
   * Repository for purchase order database operations.
   * Used to check if purchase orders exist and to save new purchase orders during seeding.
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
    log.info("Data seeding completed.");
  }

  /**
   * Seeds initial user accounts into the database.
   * Creates default administrative and employee accounts with secure passwords.
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
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Lápiz Mirado HB")
            .description("Lápiz grafito HB, cuerpo hexagonal con goma incluida")
            .costPrice(new BigDecimal("500"))
            .salePrice(new BigDecimal("1000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Caja de Colores Faber-Castell x12")
            .description(
              "Caja de 12 lápices de colores, punta resistente, colores vivos"
            )
            .costPrice(new BigDecimal("5500"))
            .salePrice(new BigDecimal("9500"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Kit Geométrico Faber-Castell")
            .description(
              "Kit de regla 30cm, escuadras, transportador y compás de precisión"
            )
            .costPrice(new BigDecimal("7500"))
            .salePrice(new BigDecimal("12000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Borrador Nata Pelikan")
            .description(
              "Borrador de nata, suave, no mancha el papel, alta durabilidad"
            )
            .costPrice(new BigDecimal("800"))
            .salePrice(new BigDecimal("1500"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(0))
            .build(),
          Product.builder()
            .name("Grapadora Metálica Rank")
            .description(
              "Grapadora metálica de escritorio, capacidad 20 hojas, color negro"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("15000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Perforadora de Papel Rank")
            .description(
              "Perforadora metálica de 2 huecos, capacidad 20 hojas, color negro"
            )
            .costPrice(new BigDecimal("10000"))
            .salePrice(new BigDecimal("18000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Caja de Clips Estándar x100")
            .description(
              "Caja con 100 clips metálicos estándar, 33mm, acabado plateado"
            )
            .costPrice(new BigDecimal("1500"))
            .salePrice(new BigDecimal("3000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Set de Bandejas Organizadoras x3")
            .description(
              "Set de 3 bandejas plásticas apilables para documentos, color negro"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("22000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Dispensador de Cinta Adhesiva")
            .description(
              "Dispensador de cinta adhesiva de escritorio, base pesada antideslizante"
            )
            .costPrice(new BigDecimal("3500"))
            .salePrice(new BigDecimal("6500"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(1))
            .build(),
          Product.builder()
            .name("Resma Papel Bond A4")
            .description(
              "Resma de papel bond A4, 75g, 500 hojas, blanco, multifuncional"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("20000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Sobre Manila Carta x50")
            .description(
              "Paquete de 50 sobres manila tamaño carta, color kraft"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("11000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Cartulina Blanca Pliego")
            .description(
              "Pliego de cartulina blanca, 70x100cm, 180g, acabado mate"
            )
            .costPrice(new BigDecimal("700"))
            .salePrice(new BigDecimal("1300"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Block Iris Carta")
            .description(
              "Block de papel iris tamaño carta, 20 hojas, colores surtidos"
            )
            .costPrice(new BigDecimal("3500"))
            .salePrice(new BigDecimal("6000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Papel Adhesivo Carta x20")
            .description(
              "Paquete de 20 hojas de papel adhesivo blanco, tamaño carta"
            )
            .costPrice(new BigDecimal("4000"))
            .salePrice(new BigDecimal("7500"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(2))
            .build(),
          Product.builder()
            .name("Tarjeta Felicitación Surtida")
            .description(
              "Tarjeta de felicitación con sobre, diseños variados para toda ocasión"
            )
            .costPrice(new BigDecimal("1500"))
            .salePrice(new BigDecimal("3500"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Papel de Regalo Surtido")
            .description(
              "Pliego de papel de regalo, diseños variados, 70x100cm"
            )
            .costPrice(new BigDecimal("1000"))
            .salePrice(new BigDecimal("2000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Muñeco Anime Pequeño")
            .description(
              "Figura decorativa de personaje anime, 10cm de altura, varios modelos"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("12000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Caja de Regalo Decorada")
            .description(
              "Caja de regalo decorativa, tamaño mediano, con moño incluido"
            )
            .costPrice(new BigDecimal("2500"))
            .salePrice(new BigDecimal("5500"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Set Tarjetas Motivacionales x10")
            .description(
              "Conjunto de 10 tarjetas pequeñas con mensajes motivacionales"
            )
            .costPrice(new BigDecimal("3000"))
            .salePrice(new BigDecimal("6000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(3))
            .build(),
          Product.builder()
            .name("Memoria USB 16GB")
            .description(
              "Memoria USB 16GB, conexión 2.0, carcasa plástica resistente"
            )
            .costPrice(new BigDecimal("15000"))
            .salePrice(new BigDecimal("25000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Mouse Óptico USB")
            .description(
              "Mouse óptico USB con cable, resolución 1000 DPI, diseño ergonómico"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("20000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Funda para Laptop 14\"")
            .description(
              "Funda protectora para laptop de 14 pulgadas, acolchada, impermeable"
            )
            .costPrice(new BigDecimal("18000"))
            .salePrice(new BigDecimal("30000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Audífonos con Cable")
            .description(
              "Audífonos con cable, conector 3.5mm, control de volumen integrado"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("15000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Adaptador HDMI a VGA")
            .description(
              "Adaptador conversor de HDMI a VGA, compatible con PC y dispositivos móviles"
            )
            .costPrice(new BigDecimal("10000"))
            .salePrice(new BigDecimal("18000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(4))
            .build(),
          Product.builder()
            .name("Set Acuarelas x12")
            .description(
              "Set de 12 pastillas de acuarela con pincel incluido, colores surtidos"
            )
            .costPrice(new BigDecimal("7000"))
            .salePrice(new BigDecimal("14000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Block Papel Acuarela A4")
            .description(
              "Block de papel para acuarela, 10 hojas, 300g, tamaño A4, grano fino"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("12000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Set Pinceles Artísticos x5")
            .description(
              "Set de 5 pinceles de diferentes tamaños para técnicas húmedas"
            )
            .costPrice(new BigDecimal("5000"))
            .salePrice(new BigDecimal("10000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Lienzo para Pintura 30x40cm")
            .description(
              "Lienzo de algodón montado en bastidor de madera, 30x40cm, imprimado"
            )
            .costPrice(new BigDecimal("9000"))
            .salePrice(new BigDecimal("16000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Lápices de Dibujo Profesionales x6")
            .description(
              "Set de 6 lápices de grafito para dibujo artístico, diferentes durezas"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("15000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(5))
            .build(),
          Product.builder()
            .name("Bolígrafo Kilométrico 100 x12")
            .description(
              "Caja de 12 bolígrafos Kilométrico 100, punta media, color azul"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("10000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Pluma Estilográfica Básica")
            .description(
              "Pluma estilográfica con cartucho recargable, cuerpo metálico, punta fina"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("22000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Marcadores Permanentes x4")
            .description(
              "Set de 4 marcadores permanentes, colores básicos, punta biselada"
            )
            .costPrice(new BigDecimal("5000"))
            .salePrice(new BigDecimal("9000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Resaltadores Neón x5")
            .description(
              "Set de 5 resaltadores en colores neón, punta biselada, tinta fluorescente"
            )
            .costPrice(new BigDecimal("6000"))
            .salePrice(new BigDecimal("11000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Lápiz Corrector Líquido")
            .description(
              "Lápiz corrector líquido de secado rápido, punta metálica de precisión"
            )
            .costPrice(new BigDecimal("2500"))
            .salePrice(new BigDecimal("4500"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(6))
            .build(),
          Product.builder()
            .name("Cuaderno Argollado 5 Materias")
            .description(
              "Cuaderno argollado de 5 materias, 200 hojas, separadores de colores"
            )
            .costPrice(new BigDecimal("9000"))
            .salePrice(new BigDecimal("16000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Libreta de Bolsillo Cuadriculada")
            .description(
              "Libreta de bolsillo, 80 hojas cuadriculadas, tapa dura, 9x14cm"
            )
            .costPrice(new BigDecimal("2500"))
            .salePrice(new BigDecimal("5000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Agenda Anual Ejecutiva")
            .description(
              "Agenda anual ejecutiva, una página por día, tapa dura, cinta separadora"
            )
            .costPrice(new BigDecimal("15000"))
            .salePrice(new BigDecimal("25000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Block de Notas Adhesivas Neón")
            .description(
              "Block de 5 colores neón de notas adhesivas, 100 hojas cada color"
            )
            .costPrice(new BigDecimal("3000"))
            .salePrice(new BigDecimal("6000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Diario Personal con Candado")
            .description(
              "Diario personal con candado, tapa acolchada, 200 páginas, 15x21cm"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("20000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(7))
            .build(),
          Product.builder()
            .name("Archivador AZ Carta")
            .description(
              "Archivador AZ tamaño carta, lomo ancho, con palanca y gancho"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("15000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Carpeta Plástica con Gancho")
            .description(
              "Carpeta plástica con gancho legajador, colores surtidos, tamaño carta"
            )
            .costPrice(new BigDecimal("2000"))
            .salePrice(new BigDecimal("4000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Separadores Plásticos x5")
            .description(
              "Juego de 5 separadores plásticos, colores surtidos, tamaño carta"
            )
            .costPrice(new BigDecimal("1500"))
            .salePrice(new BigDecimal("3000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Caja Organizadora Plástica")
            .description(
              "Caja organizadora plástica con tapa, capacidad 5 litros, transparente"
            )
            .costPrice(new BigDecimal("7000"))
            .salePrice(new BigDecimal("12000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(8))
            .build(),
          Product.builder()
            .name("Mochila Escolar Básica")
            .description(
              "Mochila escolar con 2 compartimientos, bolsillo lateral, acolchada"
            )
            .costPrice(new BigDecimal("25000"))
            .salePrice(new BigDecimal("45000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(9))
            .build(),
          Product.builder()
            .name("Cartuchera de Tela Simple")
            .description(
              "Cartuchera de tela con cierre, capacidad para 10 lápices, diseños surtidos"
            )
            .costPrice(new BigDecimal("4000"))
            .salePrice(new BigDecimal("8000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(9))
            .build(),
          Product.builder()
            .name("Ábaco Infantil de Colores")
            .description(
              "Ábaco de madera con cuentas de colores, 10 filas, 10 cuentas por fila"
            )
            .costPrice(new BigDecimal("15000"))
            .salePrice(new BigDecimal("25000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(10))
            .build(),
          Product.builder()
            .name("Mapamundi Didáctico")
            .description(
              "Mapamundi escolar didáctico plastificado, 50x70cm, doble cara"
            )
            .costPrice(new BigDecimal("12000"))
            .salePrice(new BigDecimal("20000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(10))
            .build(),
          Product.builder()
            .name("Set Arcilla para Modelar")
            .description(
              "Set de arcilla para modelar, 4 colores básicos, no tóxica, 500g"
            )
            .costPrice(new BigDecimal("8000"))
            .salePrice(new BigDecimal("14000"))
            .stock(random.nextInt(131) + 20)
            .minimumStockThreshold(random.nextInt(16) + 5)
            .category(categories.get(11))
            .build(),
          Product.builder()
            .name("Kit Bisutería Básico")
            .description(
              "Kit para elaboración de bisutería, incluye cuentas, hilos y herramientas"
            )
            .costPrice(new BigDecimal("18000"))
            .salePrice(new BigDecimal("30000"))
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
        .name("Rosita González")
        .email("rosita@sigrap.com")
        .password(passwordEncoder.encode("Rosita123*"))
        .role(UserRole.ADMINISTRATOR)
        .documentId("12345678")
        .phone("+573001234567")
        .build();

      User employeeUser = User.builder()
        .name("Gladys Mendoza")
        .email("gladys@sigrap.com")
        .password(passwordEncoder.encode("Gladys123*"))
        .role(UserRole.EMPLOYEE)
        .documentId("87654321")
        .phone("+573109876543")
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

    for (User user : users) {
      if (!"gladys@sigrap.com".equals(user.getEmail())) {
        continue;
      }

      for (String day : List.of(
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY"
      )) {
        schedules.add(
          Schedule.builder()
            .user(user)
            .day(day)
            .startTime(defaultStartTime)
            .endTime(defaultEndTime)
            .isActive(true)
            .build()
        );
      }

      if ("gladys@sigrap.com".equals(user.getEmail())) {
        schedules.add(
          Schedule.builder()
            .user(user)
            .day("SATURDAY")
            .startTime(defaultStartTime)
            .endTime(saturdayEndTime)
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
    List<User> users = userRepository.findAll();
    LocalDateTime now = LocalDateTime.now();

    for (User user : users) {
      if (!"gladys@sigrap.com".equals(user.getEmail())) {
        continue;
      }

      for (int i = 0; i < 7; i++) {
        LocalDateTime date = now.minusDays(i);

        if (
          date.getDayOfWeek().getValue() > 5 &&
          !"gladys@sigrap.com".equals(user.getEmail())
        ) {
          continue;
        }

        AttendanceStatus status = AttendanceStatus.PRESENT;
        String notes = "Asistencia regular";

        if (i == 2) {
          status = AttendanceStatus.LATE;
          notes = "Llegó 15 minutos tarde debido al tráfico";
        } else if (i == 5) {
          status = AttendanceStatus.ON_LEAVE;
          notes = "Permiso por asuntos personales";
        }

        attendanceRecords.add(
          Attendance.builder()
            .user(user)
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
          .contactPerson("Miguel Sánchez")
          .email("msanchez@officedepot.com.co")
          .phone("(601) 555-2700")
          .address("Carrera 15 # 80-25, Bogotá D.C.")
          .website("https://www.officedepot.com.co")
          .paymentTerms("30 días")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Panamericana")
          .contactPerson("Ana Campos")
          .email("acampos@panamericana.com.co")
          .phone("(601) 337-9000")
          .address("Avenida Chile # 72-41, Bogotá D.C.")
          .website("https://www.panamericana.com.co")
          .paymentTerms("15 días")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Artesco Colombia")
          .contactPerson("Roberto Gutiérrez")
          .email("rgutierrez@artesco.com.co")
          .phone("(604) 444-6000")
          .address("Calle 10 # 43E-115, Medellín, Antioquia")
          .website("https://www.artesco.com.co")
          .paymentTerms("45 días")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Scribe Colombia")
          .contactPerson("María Elena Torres")
          .email("metorres@scribe.com.co")
          .phone("(602) 660-2500")
          .address("Carrera 1 # 23-89, Cali, Valle del Cauca")
          .website("https://www.scribe.com.co")
          .paymentTerms("30 días")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Faber-Castell Colombia")
          .contactPerson("Jorge Reátegui")
          .email("jreategui@faber-castell.com.co")
          .phone("(601) 744-4800")
          .address("Autopista Norte Km 19, Chía, Cundinamarca")
          .website("https://www.faber-castell.com.co")
          .paymentTerms("45 días")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Pegaucho")
          .contactPerson("Lucía Pérez")
          .email("lperez@pegaucho.com.co")
          .phone("(601) 222-2300")
          .address("Calle 13 # 68-78, Bogotá D.C.")
          .website("https://www.pegaucho.com.co")
          .paymentTerms("Contado")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Norma Colombia")
          .contactPerson("Daniel Quispe")
          .email("dquispe@norma.com.co")
          .phone("(601) 423-8000")
          .address("Calle 100 # 19A-50, Bogotá D.C.")
          .website("https://www.norma.com.co")
          .paymentTerms("30 días")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("DistriOffice")
          .contactPerson("Patricia Rojas")
          .email("projas@distrioffice.com.co")
          .phone("(604) 333-1800")
          .address("Carrera 48 # 10-45, Medellín, Antioquia")
          .website("https://www.distrioffice.com.co")
          .paymentTerms("45 días")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Pilot Colombia")
          .contactPerson("Carlos Mendoza")
          .email("cmendoza@pilotcolombia.com")
          .phone("(601) 617-5200")
          .address("Carrera 7 # 71-21, Bogotá D.C.")
          .website("https://www.pilotpen.com.co")
          .paymentTerms("30 días")
          .build()
      );

      suppliers.add(
        Supplier.builder()
          .name("Papeles Nacionales S.A.S.")
          .contactPerson("Fernando Torres")
          .email("ftorres@papelesnacionales.com.co")
          .phone("(601) 742-9000")
          .address("Zona Franca de Bogotá, Bodega 12, Bogotá D.C.")
          .website("https://www.papelesnacionales.com.co")
          .paymentTerms("60 días")
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
      .supplier(panamericana)
      .orderDate(LocalDate.now().minusDays(45))
      .expectedDeliveryDate(LocalDate.now().minusDays(35))
      .actualDeliveryDate(LocalDate.now().minusDays(34))
      .status(PurchaseOrderStatus.DELIVERED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item11 = PurchaseOrderItem.builder()
      .product(cuadernoUniversitario)
      .quantity(100)
      .unitPrice(new BigDecimal("4000"))
      .receivedQuantity(100)
      .build();

    PurchaseOrderItem item12 = PurchaseOrderItem.builder()
      .product(lapizMirado)
      .quantity(200)
      .unitPrice(new BigDecimal("500"))
      .receivedQuantity(200)
      .build();

    PurchaseOrderItem item13 = PurchaseOrderItem.builder()
      .product(borrador)
      .quantity(150)
      .unitPrice(new BigDecimal("800"))
      .receivedQuantity(150)
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
      .supplier(faberCastell)
      .orderDate(LocalDate.now().minusDays(30))
      .expectedDeliveryDate(LocalDate.now().minusDays(20))
      .actualDeliveryDate(LocalDate.now().minusDays(22))
      .status(PurchaseOrderStatus.DELIVERED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item21 = PurchaseOrderItem.builder()
      .product(cajaColores)
      .quantity(50)
      .unitPrice(new BigDecimal("5500"))
      .receivedQuantity(50)
      .build();

    PurchaseOrderItem item22 = PurchaseOrderItem.builder()
      .product(kitGeometrico)
      .quantity(30)
      .unitPrice(new BigDecimal("7500"))
      .receivedQuantity(30)
      .build();

    item21.setPurchaseOrder(orden2);
    item22.setPurchaseOrder(orden2);
    orden2.getItems().add(item21);
    orden2.getItems().add(item22);

    BigDecimal total2 = calculateOrderTotal(orden2);
    orden2.setTotalAmount(total2);
    purchaseOrders.add(orden2);

    PurchaseOrder orden3 = PurchaseOrder.builder()
      .supplier(officedepot)
      .orderDate(LocalDate.now().minusDays(15))
      .expectedDeliveryDate(LocalDate.now().minusDays(7))
      .actualDeliveryDate(LocalDate.now().minusDays(8))
      .status(PurchaseOrderStatus.DELIVERED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item31 = PurchaseOrderItem.builder()
      .product(resmaA4)
      .quantity(80)
      .unitPrice(new BigDecimal("12000"))
      .receivedQuantity(80)
      .build();

    PurchaseOrderItem item32 = PurchaseOrderItem.builder()
      .product(cartulinaBlanca)
      .quantity(120)
      .unitPrice(new BigDecimal("700"))
      .receivedQuantity(120)
      .build();

    PurchaseOrderItem item33 = PurchaseOrderItem.builder()
      .product(blockIris)
      .quantity(60)
      .unitPrice(new BigDecimal("3500"))
      .receivedQuantity(60)
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
      .supplier(artesco)
      .orderDate(LocalDate.now().minusDays(5))
      .expectedDeliveryDate(LocalDate.now().plusDays(3))
      .status(PurchaseOrderStatus.CONFIRMED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item41 = PurchaseOrderItem.builder()
      .product(marcadoresPermanentes)
      .quantity(40)
      .unitPrice(new BigDecimal("5000"))
      .receivedQuantity(0)
      .build();

    item41.setPurchaseOrder(orden4);
    orden4.getItems().add(item41);

    BigDecimal total4 = calculateOrderTotal(orden4);
    orden4.setTotalAmount(total4);
    purchaseOrders.add(orden4);

    PurchaseOrder orden5 = PurchaseOrder.builder()
      .supplier(norma)
      .orderDate(LocalDate.now().minusDays(2))
      .expectedDeliveryDate(LocalDate.now().plusDays(10))
      .status(PurchaseOrderStatus.SUBMITTED)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item51 = PurchaseOrderItem.builder()
      .product(cuadernoUniversitario)
      .quantity(50)
      .unitPrice(new BigDecimal("4000"))
      .receivedQuantity(0)
      .build();

    PurchaseOrderItem item52 = PurchaseOrderItem.builder()
      .product(cuadernoArgollado)
      .quantity(35)
      .unitPrice(new BigDecimal("9000"))
      .receivedQuantity(0)
      .build();

    item51.setPurchaseOrder(orden5);
    item52.setPurchaseOrder(orden5);
    orden5.getItems().add(item51);
    orden5.getItems().add(item52);

    BigDecimal total5 = calculateOrderTotal(orden5);
    orden5.setTotalAmount(total5);
    purchaseOrders.add(orden5);

    PurchaseOrder orden6 = PurchaseOrder.builder()
      .supplier(panamericana)
      .orderDate(LocalDate.now())
      .expectedDeliveryDate(LocalDate.now().plusDays(15))
      .status(PurchaseOrderStatus.DRAFT)
      .items(new ArrayList<>())
      .totalAmount(BigDecimal.ZERO)
      .build();

    PurchaseOrderItem item61 = PurchaseOrderItem.builder()
      .product(lapizMirado)
      .quantity(150)
      .unitPrice(new BigDecimal("500"))
      .receivedQuantity(0)
      .build();

    PurchaseOrderItem item62 = PurchaseOrderItem.builder()
      .product(borrador)
      .quantity(100)
      .unitPrice(new BigDecimal("800"))
      .receivedQuantity(0)
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
            .fullName("Juan Rodríguez")
            .documentId("1098765432")
            .email("juan.rodriguez@gmail.com")
            .phoneNumber("310-555-1234")
            .address(
              "Calle 45 Sur # 23-18, Barrio Kennedy Central, Bogotá D.C."
            )
            .build(),
          Customer.builder()
            .fullName("María González")
            .documentId("1076543210")
            .email("maria.gonzalez@hotmail.com")
            .phoneNumber("315-555-6789")
            .address(
              "Carrera 72 Sur # 56-24, Apto 402, Barrio Ciudad Kennedy Sur, Bogotá D.C."
            )
            .build(),
          Customer.builder()
            .fullName("Carlos Pérez")
            .documentId("1053789456")
            .email("carlos.perez@outlook.com")
            .phoneNumber("300-555-4321")
            .address("Diagonal 38 Sur # 34-71, Barrio Timiza, Bogotá D.C.")
            .build(),
          Customer.builder()
            .fullName("Ana Martínez")
            .documentId("1087654321")
            .email("ana.martinez@gmail.com")
            .phoneNumber("320-555-8765")
            .address(
              "Transversal R1 # 67B-21 Sur, Barrio Bosa Nova, Bogotá D.C."
            )
            .build(),
          Customer.builder()
            .fullName("Pedro Sánchez")
            .documentId("1012345678")
            .email("pedro.sanchez@yahoo.com")
            .phoneNumber("313-555-2345")
            .address("Calle 69A Sur # 92-41, Barrio El Perdomo, Bogotá D.C.")
            .build(),
          Customer.builder()
            .fullName("Laura Díaz")
            .documentId("1034567890")
            .email("laura.diaz@hotmail.com")
            .phoneNumber("318-555-7890")
            .address(
              "Carrera 18M # 69J-06 Sur, Barrio Villa de los Alpes Sur, Bogotá D.C."
            )
            .build(),
          Customer.builder()
            .fullName("Andrés López")
            .documentId("1045678923")
            .email("andres.lopez@gmail.com")
            .phoneNumber("301-555-5432")
            .address(
              "Diagonal 62 Sur # 20F-21, Barrio San Francisco, Bogotá D.C."
            )
            .build(),
          Customer.builder()
            .fullName("Sofía Ramírez")
            .documentId("1078912345")
            .email("sofia.ramirez@outlook.com")
            .phoneNumber("312-555-9876")
            .address("Calle 43 Sur # 24-27, Barrio Venecia, Bogotá D.C.")
            .build(),
          Customer.builder()
            .fullName("Javier Torres")
            .documentId("1089012345")
            .email("javier.torres@yahoo.com")
            .phoneNumber("316-555-3456")
            .address("Carrera 80 # 68-23 Sur, Barrio Roma, Bogotá D.C.")
            .build(),
          Customer.builder()
            .fullName("Daniela Herrera")
            .documentId("1023456789")
            .email("daniela.herrera@gmail.com")
            .phoneNumber("314-555-8901")
            .address(
              "Transversal 74F # 40B-54 Sur, Barrio Ciudad Kennedy Oriental, Bogotá D.C."
            )
            .build(),
          Customer.builder()
            .fullName("Sebastián Castro")
            .documentId("1067890123")
            .email("sebastian.castro@hotmail.com")
            .phoneNumber("305-555-6543")
            .address("Calle 68 Sur # 47A-15, Barrio Atlanta, Bogotá D.C.")
            .build(),
          Customer.builder()
            .fullName("Valentina Ortiz")
            .documentId("1056789012")
            .email("valentina.ortiz@outlook.com")
            .phoneNumber("319-555-0987")
            .address("Carrera 100 # 52-41 Sur, Barrio Corabastos, Bogotá D.C.")
            .build(),
          Customer.builder()
            .fullName("Camilo Reyes")
            .documentId("1032109876")
            .email("camilo.reyes@yahoo.com")
            .phoneNumber("311-555-4567")
            .address(
              "Diagonal 82 Sur # 6-22 Este, Barrio La Aurora, Bogotá D.C."
            )
            .build(),
          Customer.builder()
            .fullName("Isabella Vargas")
            .documentId("1054321098")
            .email("isabella.vargas@gmail.com")
            .phoneNumber("317-555-9012")
            .address(
              "Transversal 70D # 68-12 Sur, Barrio Britalia, Bogotá D.C."
            )
            .build(),
          Customer.builder()
            .fullName("Santiago Morales")
            .documentId("1065432109")
            .email("santiago.morales@hotmail.com")
            .phoneNumber("304-555-7654")
            .address("Calle 73 Sur # 87M-15, Barrio Dindalito, Bogotá D.C.")
            .build()
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
      "Producto defectuoso",
      "No era la talla correcta",
      "Cliente cambió de opinión",
      "Artículo dañado en el envío",
      "No cumple con las expectativas",
    };
    return reasons[random.nextInt(reasons.length)];
  }
}
