package com.sigrap.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.data-seeder.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    seedCategories();
    seedProducts();
    seedUsers();
  }

  private void seedCategories() {
    if (categoryRepository.count() == 0) {
      log.info("Seeding categories...");

      Category c1 = new Category();
      c1.setName("Útiles Escolares");
      c1.setDescription("Artículos básicos para estudiantes de primaria y secundaria.");

      Category c2 = new Category();
      c2.setName("Artículos de Oficina");
      c2.setDescription("Suministros profesionales para el trabajo administrativo y de oficina.");

      Category c3 = new Category();
      c3.setName("Papelería General");
      c3.setDescription("Papeles, sobres, cuadernos y otros artículos de uso cotidiano.");

      Category c4 = new Category();
      c4.setName("Regalos y Detalles");
      c4.setDescription("Artículos decorativos, tarjetas y empaques para obsequios.");

      Category c5 = new Category();
      c5.setName("Tecnología Básica");
      c5.setDescription("Accesorios de computación, almacenamiento y dispositivos sencillos.");

      Category c6 = new Category();
      c6.setName("Arte y Dibujo");
      c6.setDescription("Materiales para expresión artística, dibujo técnico y manualidades creativas.");

      Category c7 = new Category();
      c7.setName("Escritura");
      c7.setDescription("Bolígrafos, lápices, marcadores y otros instrumentos de escritura.");

      Category c8 = new Category();
      c8.setName("Cuadernos y Libretas");
      c8.setDescription("Diversos formatos de cuadernos para diferentes usos escolares y profesionales.");

      Category c9 = new Category();
      c9.setName("Organización y Archivo");
      c9.setDescription("Productos para clasificar, almacenar y organizar documentos.");

      Category c10 = new Category();
      c10.setName("Mochilas y Bolsos");
      c10.setDescription("Soluciones para transportar materiales escolares y de oficina.");

      Category c11 = new Category();
      c11.setName("Material Didáctico");
      c11.setDescription("Recursos educativos para facilitar el aprendizaje en diferentes áreas.");

      Category c12 = new Category();
      c12.setName("Manualidades");
      c12.setDescription("Materiales diversos para proyectos creativos y decorativos.");

      categoryRepository.saveAll(List.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12));
      log.info("Categories seeded successfully.");
    } else {
      log.info("Categories already exist, skipping seeding.");
    }
  }

  private void seedProducts() {
    if (productRepository.count() == 0) {
      log.info("Seeding products...");

      List<Category> categories = categoryRepository.findAll();
      if (categories.isEmpty()) {
        log.warn("No categories found for product seeding.");
        return;
      }

      Product p1 = new Product();
      p1.setName("Cuaderno Universitario Norma");
      p1.setDescription("Cuaderno universitario de 100 hojas, cuadriculado, pasta dura");
      p1.setCostPrice(new BigDecimal("4000"));
      p1.setSalePrice(new BigDecimal("7000"));
      p1.setCategory(categories.get(0));

      Product p2 = new Product();
      p2.setName("Lápiz Mirado HB");
      p2.setDescription("Lápiz grafito HB, cuerpo hexagonal con goma incluida");
      p2.setCostPrice(new BigDecimal("500"));
      p2.setSalePrice(new BigDecimal("1000"));
      p2.setCategory(categories.get(0));

      Product p3 = new Product();
      p3.setName("Caja de Colores Faber-Castell x12");
      p3.setDescription("Caja de 12 lápices de colores, punta resistente, colores vivos");
      p3.setCostPrice(new BigDecimal("5500"));
      p3.setSalePrice(new BigDecimal("9500"));
      p3.setCategory(categories.get(0));

      Product p4 = new Product();
      p4.setName("Kit Geométrico Faber-Castell");
      p4.setDescription("Kit de regla 30cm, escuadras, transportador y compás de precisión");
      p4.setCostPrice(new BigDecimal("7500"));
      p4.setSalePrice(new BigDecimal("12000"));
      p4.setCategory(categories.get(0));

      Product p5 = new Product();
      p5.setName("Borrador Nata Pelikan");
      p5.setDescription("Borrador de nata, suave, no mancha el papel, alta durabilidad");
      p5.setCostPrice(new BigDecimal("800"));
      p5.setSalePrice(new BigDecimal("1500"));
      p5.setCategory(categories.get(0));

      Product p6 = new Product();
      p6.setName("Grapadora Metálica Rank");
      p6.setDescription("Grapadora metálica de escritorio, capacidad 20 hojas, color negro");
      p6.setCostPrice(new BigDecimal("8000"));
      p6.setSalePrice(new BigDecimal("15000"));
      p6.setCategory(categories.get(1));

      Product p7 = new Product();
      p7.setName("Perforadora de Papel Rank");
      p7.setDescription("Perforadora metálica de 2 huecos, capacidad 20 hojas, color negro");
      p7.setCostPrice(new BigDecimal("10000"));
      p7.setSalePrice(new BigDecimal("18000"));
      p7.setCategory(categories.get(1));

      Product p8 = new Product();
      p8.setName("Caja de Clips Estándar x100");
      p8.setDescription("Caja con 100 clips metálicos estándar, 33mm, acabado plateado");
      p8.setCostPrice(new BigDecimal("1500"));
      p8.setSalePrice(new BigDecimal("3000"));
      p8.setCategory(categories.get(1));

      Product p9 = new Product();
      p9.setName("Set de Bandejas Organizadoras x3");
      p9.setDescription("Set de 3 bandejas plásticas apilables para documentos, color negro");
      p9.setCostPrice(new BigDecimal("12000"));
      p9.setSalePrice(new BigDecimal("22000"));
      p9.setCategory(categories.get(1));

      Product p10 = new Product();
      p10.setName("Dispensador de Cinta Adhesiva");
      p10.setDescription("Dispensador de cinta adhesiva de escritorio, base pesada antideslizante");
      p10.setCostPrice(new BigDecimal("3500"));
      p10.setSalePrice(new BigDecimal("6500"));
      p10.setCategory(categories.get(1));

      Product p11 = new Product();
      p11.setName("Resma Papel Bond A4");
      p11.setDescription("Resma de papel bond A4, 75g, 500 hojas, blanco, multifuncional");
      p11.setCostPrice(new BigDecimal("12000"));
      p11.setSalePrice(new BigDecimal("20000"));
      p11.setCategory(categories.get(2));

      Product p12 = new Product();
      p12.setName("Sobre Manila Carta x50");
      p12.setDescription("Paquete de 50 sobres manila tamaño carta, color kraft");
      p12.setCostPrice(new BigDecimal("6000"));
      p12.setSalePrice(new BigDecimal("11000"));
      p12.setCategory(categories.get(2));

      Product p13 = new Product();
      p13.setName("Cartulina Blanca Pliego");
      p13.setDescription("Pliego de cartulina blanca, 70x100cm, 180g, acabado mate");
      p13.setCostPrice(new BigDecimal("700"));
      p13.setSalePrice(new BigDecimal("1300"));
      p13.setCategory(categories.get(2));

      Product p14 = new Product();
      p14.setName("Block Iris Carta");
      p14.setDescription("Block de papel iris tamaño carta, 20 hojas, colores surtidos");
      p14.setCostPrice(new BigDecimal("3500"));
      p14.setSalePrice(new BigDecimal("6000"));
      p14.setCategory(categories.get(2));

      Product p15 = new Product();
      p15.setName("Papel Adhesivo Carta x20");
      p15.setDescription("Paquete de 20 hojas de papel adhesivo blanco, tamaño carta");
      p15.setCostPrice(new BigDecimal("4000"));
      p15.setSalePrice(new BigDecimal("7500"));
      p15.setCategory(categories.get(2));

      Product p16 = new Product();
      p16.setName("Tarjeta Felicitación Surtida");
      p16.setDescription("Tarjeta de felicitación con sobre, diseños variados para toda ocasión");
      p16.setCostPrice(new BigDecimal("1500"));
      p16.setSalePrice(new BigDecimal("3500"));
      p16.setCategory(categories.get(3));

      Product p17 = new Product();
      p17.setName("Papel de Regalo Surtido");
      p17.setDescription("Pliego de papel de regalo, diseños variados, 70x100cm");
      p17.setCostPrice(new BigDecimal("1000"));
      p17.setSalePrice(new BigDecimal("2000"));
      p17.setCategory(categories.get(3));

      Product p18 = new Product();
      p18.setName("Muñeco Anime Pequeño");
      p18.setDescription("Figura decorativa de personaje anime, 10cm de altura, varios modelos");
      p18.setCostPrice(new BigDecimal("6000"));
      p18.setSalePrice(new BigDecimal("12000"));
      p18.setCategory(categories.get(3));

      Product p19 = new Product();
      p19.setName("Caja de Regalo Decorada");
      p19.setDescription("Caja de regalo decorativa, tamaño mediano, con moño incluido");
      p19.setCostPrice(new BigDecimal("2500"));
      p19.setSalePrice(new BigDecimal("5500"));
      p19.setCategory(categories.get(3));

      Product p20 = new Product();
      p20.setName("Set Tarjetas Motivacionales x10");
      p20.setDescription("Conjunto de 10 tarjetas pequeñas con mensajes motivacionales");
      p20.setCostPrice(new BigDecimal("3000"));
      p20.setSalePrice(new BigDecimal("6000"));
      p20.setCategory(categories.get(3));

      Product p21 = new Product();
      p21.setName("Memoria USB 16GB");
      p21.setDescription("Memoria USB 16GB, conexión 2.0, carcasa plástica resistente");
      p21.setCostPrice(new BigDecimal("15000"));
      p21.setSalePrice(new BigDecimal("25000"));
      p21.setCategory(categories.get(4));

      Product p22 = new Product();
      p22.setName("Mouse Óptico USB");
      p22.setDescription("Mouse óptico USB con cable, resolución 1000 DPI, diseño ergonómico");
      p22.setCostPrice(new BigDecimal("12000"));
      p22.setSalePrice(new BigDecimal("20000"));
      p22.setCategory(categories.get(4));

      Product p23 = new Product();
      p23.setName("Funda para Laptop 14\"");
      p23.setDescription("Funda protectora para laptop de 14 pulgadas, acolchada, impermeable");
      p23.setCostPrice(new BigDecimal("18000"));
      p23.setSalePrice(new BigDecimal("30000"));
      p23.setCategory(categories.get(4));

      Product p24 = new Product();
      p24.setName("Audífonos con Cable");
      p24.setDescription("Audífonos con cable, conector 3.5mm, control de volumen integrado");
      p24.setCostPrice(new BigDecimal("8000"));
      p24.setSalePrice(new BigDecimal("15000"));
      p24.setCategory(categories.get(4));

      Product p25 = new Product();
      p25.setName("Adaptador HDMI a VGA");
      p25.setDescription("Adaptador conversor de HDMI a VGA, compatible con PC y dispositivos móviles");
      p25.setCostPrice(new BigDecimal("10000"));
      p25.setSalePrice(new BigDecimal("18000"));
      p25.setCategory(categories.get(4));

      Product p26 = new Product();
      p26.setName("Set Acuarelas x12");
      p26.setDescription("Set de 12 pastillas de acuarela con pincel incluido, colores surtidos");
      p26.setCostPrice(new BigDecimal("7000"));
      p26.setSalePrice(new BigDecimal("14000"));
      p26.setCategory(categories.get(5));

      Product p27 = new Product();
      p27.setName("Block Papel Acuarela A4");
      p27.setDescription("Block de papel para acuarela, 10 hojas, 300g, tamaño A4, grano fino");
      p27.setCostPrice(new BigDecimal("6000"));
      p27.setSalePrice(new BigDecimal("12000"));
      p27.setCategory(categories.get(5));

      Product p28 = new Product();
      p28.setName("Set Pinceles Artísticos x5");
      p28.setDescription("Set de 5 pinceles de diferentes tamaños para técnicas húmedas");
      p28.setCostPrice(new BigDecimal("5000"));
      p28.setSalePrice(new BigDecimal("10000"));
      p28.setCategory(categories.get(5));

      Product p29 = new Product();
      p29.setName("Lienzo para Pintura 30x40cm");
      p29.setDescription("Lienzo de algodón montado en bastidor de madera, 30x40cm, imprimado");
      p29.setCostPrice(new BigDecimal("9000"));
      p29.setSalePrice(new BigDecimal("16000"));
      p29.setCategory(categories.get(5));

      Product p30 = new Product();
      p30.setName("Lápices de Dibujo Profesionales x6");
      p30.setDescription("Set de 6 lápices de grafito para dibujo artístico, diferentes durezas");
      p30.setCostPrice(new BigDecimal("8000"));
      p30.setSalePrice(new BigDecimal("15000"));
      p30.setCategory(categories.get(5));

      Product p31 = new Product();
      p31.setName("Bolígrafo Kilométrico 100 x12");
      p31.setDescription("Caja de 12 bolígrafos Kilométrico 100, punta media, color azul");
      p31.setCostPrice(new BigDecimal("6000"));
      p31.setSalePrice(new BigDecimal("10000"));
      p31.setCategory(categories.get(6));

      Product p32 = new Product();
      p32.setName("Pluma Estilográfica Básica");
      p32.setDescription("Pluma estilográfica con cartucho recargable, cuerpo metálico, punta fina");
      p32.setCostPrice(new BigDecimal("12000"));
      p32.setSalePrice(new BigDecimal("22000"));
      p32.setCategory(categories.get(6));

      Product p33 = new Product();
      p33.setName("Marcadores Permanentes x4");
      p33.setDescription("Set de 4 marcadores permanentes, colores básicos, punta biselada");
      p33.setCostPrice(new BigDecimal("5000"));
      p33.setSalePrice(new BigDecimal("9000"));
      p33.setCategory(categories.get(6));

      Product p34 = new Product();
      p34.setName("Resaltadores Neón x5");
      p34.setDescription("Set de 5 resaltadores en colores neón, punta biselada, tinta fluorescente");
      p34.setCostPrice(new BigDecimal("6000"));
      p34.setSalePrice(new BigDecimal("11000"));
      p34.setCategory(categories.get(6));

      Product p35 = new Product();
      p35.setName("Lápiz Corrector Líquido");
      p35.setDescription("Lápiz corrector líquido de secado rápido, punta metálica de precisión");
      p35.setCostPrice(new BigDecimal("2500"));
      p35.setSalePrice(new BigDecimal("4500"));
      p35.setCategory(categories.get(6));

      Product p36 = new Product();
      p36.setName("Cuaderno Argollado 5 Materias");
      p36.setDescription("Cuaderno argollado de 5 materias, 200 hojas, separadores de colores");
      p36.setCostPrice(new BigDecimal("9000"));
      p36.setSalePrice(new BigDecimal("16000"));
      p36.setCategory(categories.get(7));

      Product p37 = new Product();
      p37.setName("Libreta de Bolsillo Cuadriculada");
      p37.setDescription("Libreta de bolsillo, 80 hojas cuadriculadas, tapa dura, 9x14cm");
      p37.setCostPrice(new BigDecimal("2500"));
      p37.setSalePrice(new BigDecimal("5000"));
      p37.setCategory(categories.get(7));

      Product p38 = new Product();
      p38.setName("Agenda Anual Ejecutiva");
      p38.setDescription("Agenda anual ejecutiva, una página por día, tapa dura, cinta separadora");
      p38.setCostPrice(new BigDecimal("15000"));
      p38.setSalePrice(new BigDecimal("25000"));
      p38.setCategory(categories.get(7));

      Product p39 = new Product();
      p39.setName("Block de Notas Adhesivas Neón");
      p39.setDescription("Block de 5 colores neón de notas adhesivas, 100 hojas cada color");
      p39.setCostPrice(new BigDecimal("3000"));
      p39.setSalePrice(new BigDecimal("6000"));
      p39.setCategory(categories.get(7));

      Product p40 = new Product();
      p40.setName("Diario Personal con Candado");
      p40.setDescription("Diario personal con candado, tapa acolchada, 200 páginas, 15x21cm");
      p40.setCostPrice(new BigDecimal("12000"));
      p40.setSalePrice(new BigDecimal("20000"));
      p40.setCategory(categories.get(7));

      Product p41 = new Product();
      p41.setName("Archivador AZ Carta");
      p41.setDescription("Archivador AZ tamaño carta, lomo ancho, con palanca y gancho");
      p41.setCostPrice(new BigDecimal("8000"));
      p41.setSalePrice(new BigDecimal("15000"));
      p41.setCategory(categories.get(8));

      Product p42 = new Product();
      p42.setName("Carpeta Plástica con Gancho");
      p42.setDescription("Carpeta plástica con gancho legajador, colores surtidos, tamaño carta");
      p42.setCostPrice(new BigDecimal("2000"));
      p42.setSalePrice(new BigDecimal("4000"));
      p42.setCategory(categories.get(8));

      Product p43 = new Product();
      p43.setName("Separadores Plásticos x5");
      p43.setDescription("Juego de 5 separadores plásticos, colores surtidos, tamaño carta");
      p43.setCostPrice(new BigDecimal("1500"));
      p43.setSalePrice(new BigDecimal("3000"));
      p43.setCategory(categories.get(8));

      Product p44 = new Product();
      p44.setName("Caja Organizadora Plástica");
      p44.setDescription("Caja organizadora plástica con tapa, capacidad 5 litros, transparente");
      p44.setCostPrice(new BigDecimal("7000"));
      p44.setSalePrice(new BigDecimal("12000"));
      p44.setCategory(categories.get(8));

      Product p45 = new Product();
      p45.setName("Mochila Escolar Básica");
      p45.setDescription("Mochila escolar con 2 compartimientos, bolsillo lateral, acolchada");
      p45.setCostPrice(new BigDecimal("25000"));
      p45.setSalePrice(new BigDecimal("45000"));
      p45.setCategory(categories.get(9));

      Product p46 = new Product();
      p46.setName("Cartuchera de Tela Simple");
      p46.setDescription("Cartuchera de tela con cierre, capacidad para 10 lápices, diseños surtidos");
      p46.setCostPrice(new BigDecimal("4000"));
      p46.setSalePrice(new BigDecimal("8000"));
      p46.setCategory(categories.get(9));

      Product p47 = new Product();
      p47.setName("Ábaco Infantil de Colores");
      p47.setDescription("Ábaco de madera con cuentas de colores, 10 filas, 10 cuentas por fila");
      p47.setCostPrice(new BigDecimal("15000"));
      p47.setSalePrice(new BigDecimal("25000"));
      p47.setCategory(categories.get(10));

      Product p48 = new Product();
      p48.setName("Mapamundi Didáctico");
      p48.setDescription("Mapamundi escolar didáctico plastificado, 50x70cm, doble cara");
      p48.setCostPrice(new BigDecimal("12000"));
      p48.setSalePrice(new BigDecimal("20000"));
      p48.setCategory(categories.get(10));

      Product p49 = new Product();
      p49.setName("Set Arcilla para Modelar");
      p49.setDescription("Set de arcilla para modelar, 4 colores básicos, no tóxica, 500g");
      p49.setCostPrice(new BigDecimal("8000"));
      p49.setSalePrice(new BigDecimal("14000"));
      p49.setCategory(categories.get(11));

      Product p50 = new Product();
      p50.setName("Kit Bisutería Básico");
      p50.setDescription("Kit para elaboración de bisutería, incluye cuentas, hilos y herramientas");
      p50.setCostPrice(new BigDecimal("18000"));
      p50.setSalePrice(new BigDecimal("30000"));
      p50.setCategory(categories.get(11));

      productRepository.saveAll(List.of(
          p1, p2, p3, p4, p5, p6, p7, p8, p9, p10,
          p11, p12, p13, p14, p15, p16, p17, p18, p19, p20,
          p21, p22, p23, p24, p25, p26, p27, p28, p29, p30,
          p31, p32, p33, p34, p35, p36, p37, p38, p39, p40,
          p41, p42, p43, p44, p45, p46, p47, p48, p49, p50));
      log.info("Products seeded successfully.");
    } else {
      log.info("Products already exist, skipping seeding.");
    }
  }

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