package com.sigrap.config;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
   * Executes the data seeding process on application startup.
   * Ensures all required initial data is present in the database.
   *
   * @param args Command line arguments (not used)
   * @throws Exception if any seeding operation fails
   */
  @Override
  public void run(String... args) throws Exception {
    seedCategories();
    seedProducts();
    seedUsers();
  }

  /**
   * Seeds initial product categories into the database.
   * Creates a comprehensive set of categories covering different types of stationery items.
   * Only executes if the categories table is empty.
   */
  private void seedCategories() {
    if (categoryRepository.count() == 0) {
      log.info("Seeding categories...");

      Category c1 = Category.builder()
        .name("Útiles Escolares")
        .description(
          "Artículos básicos para estudiantes de primaria y secundaria."
        )
        .build();

      Category c2 = Category.builder()
        .name("Artículos de Oficina")
        .description(
          "Suministros profesionales para el trabajo administrativo y de oficina."
        )
        .build();

      Category c3 = Category.builder()
        .name("Papelería General")
        .description(
          "Papeles, sobres, cuadernos y otros artículos de uso cotidiano."
        )
        .build();

      Category c4 = Category.builder()
        .name("Regalos y Detalles")
        .description(
          "Artículos decorativos, tarjetas y empaques para obsequios."
        )
        .build();

      Category c5 = Category.builder()
        .name("Tecnología Básica")
        .description(
          "Accesorios de computación, almacenamiento y dispositivos sencillos."
        )
        .build();

      Category c6 = Category.builder()
        .name("Arte y Dibujo")
        .description(
          "Materiales para expresión artística, dibujo técnico y manualidades creativas."
        )
        .build();

      Category c7 = Category.builder()
        .name("Escritura")
        .description(
          "Bolígrafos, lápices, marcadores y otros instrumentos de escritura."
        )
        .build();

      Category c8 = Category.builder()
        .name("Cuadernos y Libretas")
        .description(
          "Diversos formatos de cuadernos para diferentes usos escolares y profesionales."
        )
        .build();

      Category c9 = Category.builder()
        .name("Organización y Archivo")
        .description(
          "Productos para clasificar, almacenar y organizar documentos."
        )
        .build();

      Category c10 = Category.builder()
        .name("Mochilas y Bolsos")
        .description(
          "Soluciones para transportar materiales escolares y de oficina."
        )
        .build();

      Category c11 = Category.builder()
        .name("Material Didáctico")
        .description(
          "Recursos educativos para facilitar el aprendizaje en diferentes áreas."
        )
        .build();

      Category c12 = Category.builder()
        .name("Manualidades")
        .description(
          "Materiales diversos para proyectos creativos y decorativos."
        )
        .build();

      categoryRepository.saveAll(
        List.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12)
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

      Product p1 = Product.builder()
        .name("Cuaderno Universitario Norma")
        .description(
          "Cuaderno universitario de 100 hojas, cuadriculado, pasta dura"
        )
        .costPrice(new BigDecimal("4000"))
        .salePrice(new BigDecimal("7000"))
        .category(categories.get(0))
        .build();

      Product p2 = Product.builder()
        .name("Lápiz Mirado HB")
        .description("Lápiz grafito HB, cuerpo hexagonal con goma incluida")
        .costPrice(new BigDecimal("500"))
        .salePrice(new BigDecimal("1000"))
        .category(categories.get(0))
        .build();

      Product p3 = Product.builder()
        .name("Caja de Colores Faber-Castell x12")
        .description(
          "Caja de 12 lápices de colores, punta resistente, colores vivos"
        )
        .costPrice(new BigDecimal("5500"))
        .salePrice(new BigDecimal("9500"))
        .category(categories.get(0))
        .build();

      Product p4 = Product.builder()
        .name("Kit Geométrico Faber-Castell")
        .description(
          "Kit de regla 30cm, escuadras, transportador y compás de precisión"
        )
        .costPrice(new BigDecimal("7500"))
        .salePrice(new BigDecimal("12000"))
        .category(categories.get(0))
        .build();

      Product p5 = Product.builder()
        .name("Borrador Nata Pelikan")
        .description(
          "Borrador de nata, suave, no mancha el papel, alta durabilidad"
        )
        .costPrice(new BigDecimal("800"))
        .salePrice(new BigDecimal("1500"))
        .category(categories.get(0))
        .build();

      Product p6 = Product.builder()
        .name("Grapadora Metálica Rank")
        .description(
          "Grapadora metálica de escritorio, capacidad 20 hojas, color negro"
        )
        .costPrice(new BigDecimal("8000"))
        .salePrice(new BigDecimal("15000"))
        .category(categories.get(1))
        .build();

      Product p7 = Product.builder()
        .name("Perforadora de Papel Rank")
        .description(
          "Perforadora metálica de 2 huecos, capacidad 20 hojas, color negro"
        )
        .costPrice(new BigDecimal("10000"))
        .salePrice(new BigDecimal("18000"))
        .category(categories.get(1))
        .build();

      Product p8 = Product.builder()
        .name("Caja de Clips Estándar x100")
        .description(
          "Caja con 100 clips metálicos estándar, 33mm, acabado plateado"
        )
        .costPrice(new BigDecimal("1500"))
        .salePrice(new BigDecimal("3000"))
        .category(categories.get(1))
        .build();

      Product p9 = Product.builder()
        .name("Set de Bandejas Organizadoras x3")
        .description(
          "Set de 3 bandejas plásticas apilables para documentos, color negro"
        )
        .costPrice(new BigDecimal("12000"))
        .salePrice(new BigDecimal("22000"))
        .category(categories.get(1))
        .build();

      Product p10 = Product.builder()
        .name("Dispensador de Cinta Adhesiva")
        .description(
          "Dispensador de cinta adhesiva de escritorio, base pesada antideslizante"
        )
        .costPrice(new BigDecimal("3500"))
        .salePrice(new BigDecimal("6500"))
        .category(categories.get(1))
        .build();

      Product p11 = Product.builder()
        .name("Resma Papel Bond A4")
        .description(
          "Resma de papel bond A4, 75g, 500 hojas, blanco, multifuncional"
        )
        .costPrice(new BigDecimal("12000"))
        .salePrice(new BigDecimal("20000"))
        .category(categories.get(2))
        .build();

      Product p12 = Product.builder()
        .name("Sobre Manila Carta x50")
        .description("Paquete de 50 sobres manila tamaño carta, color kraft")
        .costPrice(new BigDecimal("6000"))
        .salePrice(new BigDecimal("11000"))
        .category(categories.get(2))
        .build();

      Product p13 = Product.builder()
        .name("Cartulina Blanca Pliego")
        .description("Pliego de cartulina blanca, 70x100cm, 180g, acabado mate")
        .costPrice(new BigDecimal("700"))
        .salePrice(new BigDecimal("1300"))
        .category(categories.get(2))
        .build();

      Product p14 = Product.builder()
        .name("Block Iris Carta")
        .description(
          "Block de papel iris tamaño carta, 20 hojas, colores surtidos"
        )
        .costPrice(new BigDecimal("3500"))
        .salePrice(new BigDecimal("6000"))
        .category(categories.get(2))
        .build();

      Product p15 = Product.builder()
        .name("Papel Adhesivo Carta x20")
        .description(
          "Paquete de 20 hojas de papel adhesivo blanco, tamaño carta"
        )
        .costPrice(new BigDecimal("4000"))
        .salePrice(new BigDecimal("7500"))
        .category(categories.get(2))
        .build();

      Product p16 = Product.builder()
        .name("Tarjeta Felicitación Surtida")
        .description(
          "Tarjeta de felicitación con sobre, diseños variados para toda ocasión"
        )
        .costPrice(new BigDecimal("1500"))
        .salePrice(new BigDecimal("3500"))
        .category(categories.get(3))
        .build();

      Product p17 = Product.builder()
        .name("Papel de Regalo Surtido")
        .description("Pliego de papel de regalo, diseños variados, 70x100cm")
        .costPrice(new BigDecimal("1000"))
        .salePrice(new BigDecimal("2000"))
        .category(categories.get(3))
        .build();

      Product p18 = Product.builder()
        .name("Muñeco Anime Pequeño")
        .description(
          "Figura decorativa de personaje anime, 10cm de altura, varios modelos"
        )
        .costPrice(new BigDecimal("6000"))
        .salePrice(new BigDecimal("12000"))
        .category(categories.get(3))
        .build();

      Product p19 = Product.builder()
        .name("Caja de Regalo Decorada")
        .description(
          "Caja de regalo decorativa, tamaño mediano, con moño incluido"
        )
        .costPrice(new BigDecimal("2500"))
        .salePrice(new BigDecimal("5500"))
        .category(categories.get(3))
        .build();

      Product p20 = Product.builder()
        .name("Set Tarjetas Motivacionales x10")
        .description(
          "Conjunto de 10 tarjetas pequeñas con mensajes motivacionales"
        )
        .costPrice(new BigDecimal("3000"))
        .salePrice(new BigDecimal("6000"))
        .category(categories.get(3))
        .build();

      Product p21 = Product.builder()
        .name("Memoria USB 16GB")
        .description(
          "Memoria USB 16GB, conexión 2.0, carcasa plástica resistente"
        )
        .costPrice(new BigDecimal("15000"))
        .salePrice(new BigDecimal("25000"))
        .category(categories.get(4))
        .build();

      Product p22 = Product.builder()
        .name("Mouse Óptico USB")
        .description(
          "Mouse óptico USB con cable, resolución 1000 DPI, diseño ergonómico"
        )
        .costPrice(new BigDecimal("12000"))
        .salePrice(new BigDecimal("20000"))
        .category(categories.get(4))
        .build();

      Product p23 = Product.builder()
        .name("Funda para Laptop 14\"")
        .description(
          "Funda protectora para laptop de 14 pulgadas, acolchada, impermeable"
        )
        .costPrice(new BigDecimal("18000"))
        .salePrice(new BigDecimal("30000"))
        .category(categories.get(4))
        .build();

      Product p24 = Product.builder()
        .name("Audífonos con Cable")
        .description(
          "Audífonos con cable, conector 3.5mm, control de volumen integrado"
        )
        .costPrice(new BigDecimal("8000"))
        .salePrice(new BigDecimal("15000"))
        .category(categories.get(4))
        .build();

      Product p25 = Product.builder()
        .name("Adaptador HDMI a VGA")
        .description(
          "Adaptador conversor de HDMI a VGA, compatible con PC y dispositivos móviles"
        )
        .costPrice(new BigDecimal("10000"))
        .salePrice(new BigDecimal("18000"))
        .category(categories.get(4))
        .build();

      Product p26 = Product.builder()
        .name("Set Acuarelas x12")
        .description(
          "Set de 12 pastillas de acuarela con pincel incluido, colores surtidos"
        )
        .costPrice(new BigDecimal("7000"))
        .salePrice(new BigDecimal("14000"))
        .category(categories.get(5))
        .build();

      Product p27 = Product.builder()
        .name("Block Papel Acuarela A4")
        .description(
          "Block de papel para acuarela, 10 hojas, 300g, tamaño A4, grano fino"
        )
        .costPrice(new BigDecimal("6000"))
        .salePrice(new BigDecimal("12000"))
        .category(categories.get(5))
        .build();

      Product p28 = Product.builder()
        .name("Set Pinceles Artísticos x5")
        .description(
          "Set de 5 pinceles de diferentes tamaños para técnicas húmedas"
        )
        .costPrice(new BigDecimal("5000"))
        .salePrice(new BigDecimal("10000"))
        .category(categories.get(5))
        .build();

      Product p29 = Product.builder()
        .name("Lienzo para Pintura 30x40cm")
        .description(
          "Lienzo de algodón montado en bastidor de madera, 30x40cm, imprimado"
        )
        .costPrice(new BigDecimal("9000"))
        .salePrice(new BigDecimal("16000"))
        .category(categories.get(5))
        .build();

      Product p30 = Product.builder()
        .name("Lápices de Dibujo Profesionales x6")
        .description(
          "Set de 6 lápices de grafito para dibujo artístico, diferentes durezas"
        )
        .costPrice(new BigDecimal("8000"))
        .salePrice(new BigDecimal("15000"))
        .category(categories.get(5))
        .build();

      Product p31 = Product.builder()
        .name("Bolígrafo Kilométrico 100 x12")
        .description(
          "Caja de 12 bolígrafos Kilométrico 100, punta media, color azul"
        )
        .costPrice(new BigDecimal("6000"))
        .salePrice(new BigDecimal("10000"))
        .category(categories.get(6))
        .build();

      Product p32 = Product.builder()
        .name("Pluma Estilográfica Básica")
        .description(
          "Pluma estilográfica con cartucho recargable, cuerpo metálico, punta fina"
        )
        .costPrice(new BigDecimal("12000"))
        .salePrice(new BigDecimal("22000"))
        .category(categories.get(6))
        .build();

      Product p33 = Product.builder()
        .name("Marcadores Permanentes x4")
        .description(
          "Set de 4 marcadores permanentes, colores básicos, punta biselada"
        )
        .costPrice(new BigDecimal("5000"))
        .salePrice(new BigDecimal("9000"))
        .category(categories.get(6))
        .build();

      Product p34 = Product.builder()
        .name("Resaltadores Neón x5")
        .description(
          "Set de 5 resaltadores en colores neón, punta biselada, tinta fluorescente"
        )
        .costPrice(new BigDecimal("6000"))
        .salePrice(new BigDecimal("11000"))
        .category(categories.get(6))
        .build();

      Product p35 = Product.builder()
        .name("Lápiz Corrector Líquido")
        .description(
          "Lápiz corrector líquido de secado rápido, punta metálica de precisión"
        )
        .costPrice(new BigDecimal("2500"))
        .salePrice(new BigDecimal("4500"))
        .category(categories.get(6))
        .build();

      Product p36 = Product.builder()
        .name("Cuaderno Argollado 5 Materias")
        .description(
          "Cuaderno argollado de 5 materias, 200 hojas, separadores de colores"
        )
        .costPrice(new BigDecimal("9000"))
        .salePrice(new BigDecimal("16000"))
        .category(categories.get(7))
        .build();

      Product p37 = Product.builder()
        .name("Libreta de Bolsillo Cuadriculada")
        .description(
          "Libreta de bolsillo, 80 hojas cuadriculadas, tapa dura, 9x14cm"
        )
        .costPrice(new BigDecimal("2500"))
        .salePrice(new BigDecimal("5000"))
        .category(categories.get(7))
        .build();

      Product p38 = Product.builder()
        .name("Agenda Anual Ejecutiva")
        .description(
          "Agenda anual ejecutiva, una página por día, tapa dura, cinta separadora"
        )
        .costPrice(new BigDecimal("15000"))
        .salePrice(new BigDecimal("25000"))
        .category(categories.get(7))
        .build();

      Product p39 = Product.builder()
        .name("Block de Notas Adhesivas Neón")
        .description(
          "Block de 5 colores neón de notas adhesivas, 100 hojas cada color"
        )
        .costPrice(new BigDecimal("3000"))
        .salePrice(new BigDecimal("6000"))
        .category(categories.get(7))
        .build();

      Product p40 = Product.builder()
        .name("Diario Personal con Candado")
        .description(
          "Diario personal con candado, tapa acolchada, 200 páginas, 15x21cm"
        )
        .costPrice(new BigDecimal("12000"))
        .salePrice(new BigDecimal("20000"))
        .category(categories.get(7))
        .build();

      Product p41 = Product.builder()
        .name("Archivador AZ Carta")
        .description(
          "Archivador AZ tamaño carta, lomo ancho, con palanca y gancho"
        )
        .costPrice(new BigDecimal("8000"))
        .salePrice(new BigDecimal("15000"))
        .category(categories.get(8))
        .build();

      Product p42 = Product.builder()
        .name("Carpeta Plástica con Gancho")
        .description(
          "Carpeta plástica con gancho legajador, colores surtidos, tamaño carta"
        )
        .costPrice(new BigDecimal("2000"))
        .salePrice(new BigDecimal("4000"))
        .category(categories.get(8))
        .build();

      Product p43 = Product.builder()
        .name("Separadores Plásticos x5")
        .description(
          "Juego de 5 separadores plásticos, colores surtidos, tamaño carta"
        )
        .costPrice(new BigDecimal("1500"))
        .salePrice(new BigDecimal("3000"))
        .category(categories.get(8))
        .build();

      Product p44 = Product.builder()
        .name("Caja Organizadora Plástica")
        .description(
          "Caja organizadora plástica con tapa, capacidad 5 litros, transparente"
        )
        .costPrice(new BigDecimal("7000"))
        .salePrice(new BigDecimal("12000"))
        .category(categories.get(8))
        .build();

      Product p45 = Product.builder()
        .name("Mochila Escolar Básica")
        .description(
          "Mochila escolar con 2 compartimientos, bolsillo lateral, acolchada"
        )
        .costPrice(new BigDecimal("25000"))
        .salePrice(new BigDecimal("45000"))
        .category(categories.get(9))
        .build();

      Product p46 = Product.builder()
        .name("Cartuchera de Tela Simple")
        .description(
          "Cartuchera de tela con cierre, capacidad para 10 lápices, diseños surtidos"
        )
        .costPrice(new BigDecimal("4000"))
        .salePrice(new BigDecimal("8000"))
        .category(categories.get(9))
        .build();

      Product p47 = Product.builder()
        .name("Ábaco Infantil de Colores")
        .description(
          "Ábaco de madera con cuentas de colores, 10 filas, 10 cuentas por fila"
        )
        .costPrice(new BigDecimal("15000"))
        .salePrice(new BigDecimal("25000"))
        .category(categories.get(10))
        .build();

      Product p48 = Product.builder()
        .name("Mapamundi Didáctico")
        .description(
          "Mapamundi escolar didáctico plastificado, 50x70cm, doble cara"
        )
        .costPrice(new BigDecimal("12000"))
        .salePrice(new BigDecimal("20000"))
        .category(categories.get(10))
        .build();

      Product p49 = Product.builder()
        .name("Set Arcilla para Modelar")
        .description(
          "Set de arcilla para modelar, 4 colores básicos, no tóxica, 500g"
        )
        .costPrice(new BigDecimal("8000"))
        .salePrice(new BigDecimal("14000"))
        .category(categories.get(11))
        .build();

      Product p50 = Product.builder()
        .name("Kit Bisutería Básico")
        .description(
          "Kit para elaboración de bisutería, incluye cuentas, hilos y herramientas"
        )
        .costPrice(new BigDecimal("18000"))
        .salePrice(new BigDecimal("30000"))
        .category(categories.get(11))
        .build();

      productRepository.saveAll(
        List.of(
          p1,
          p2,
          p3,
          p4,
          p5,
          p6,
          p7,
          p8,
          p9,
          p10,
          p11,
          p12,
          p13,
          p14,
          p15,
          p16,
          p17,
          p18,
          p19,
          p20,
          p21,
          p22,
          p23,
          p24,
          p25,
          p26,
          p27,
          p28,
          p29,
          p30,
          p31,
          p32,
          p33,
          p34,
          p35,
          p36,
          p37,
          p38,
          p39,
          p40,
          p41,
          p42,
          p43,
          p44,
          p45,
          p46,
          p47,
          p48,
          p49,
          p50
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
        .build();

      User employeeUser = User.builder()
        .name("Gladys Mendoza")
        .email("gladys@sigrap.com")
        .password(passwordEncoder.encode("Gladys123*"))
        .build();

      userRepository.saveAll(List.of(adminUser, employeeUser));
      log.info("Users seeded successfully.");
    } else {
      log.info("Users already exist, skipping seeding.");
    }
  }
}
