package com.sigrap.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
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

      var c1 = new Category();
      c1.setName("Útiles Escolares");
      c1.setDescription("Artículos para estudiantes y actividades escolares.");

      var c2 = new Category();
      c2.setName("Artículos de Oficina");
      c2.setDescription("Suministros básicos para el trabajo de oficina.");

      var c3 = new Category();
      c3.setName("Papelería General");
      c3.setDescription("Papeles, sobres, cuadernos y otros artículos de papel.");

      var c4 = new Category();
      c4.setName("Regalos y Detalles");
      c4.setDescription("Artículos para regalo, tarjetas y empaques.");

      var c5 = new Category();
      c5.setName("Tecnología Básica");
      c5.setDescription("Accesorios de computación básicos y almacenamiento.");

      categoryRepository.saveAll(List.of(c1, c2, c3, c4, c5));
      log.info("Categories seeded.");
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

      var p1 = new Product();
      p1.setName("Cuaderno Universitario");
      p1.setDescription("Cuaderno universitario de 100 hojas, cuadriculado");
      p1.setCostPrice(new BigDecimal("3000"));
      p1.setSalePrice(new BigDecimal("5000"));
      p1.setCategory(categories.get(0));

      var p2 = new Product();
      p2.setName("Lápiz HB");
      p2.setDescription("Lápiz grafito HB, cuerpo hexagonal");
      p2.setCostPrice(new BigDecimal("500"));
      p2.setSalePrice(new BigDecimal("1000"));
      p2.setCategory(categories.get(0));

      var p3 = new Product();
      p3.setName("Grapadora");
      p3.setDescription("Grapadora metálica de escritorio");
      p3.setCostPrice(new BigDecimal("8000"));
      p3.setSalePrice(new BigDecimal("15000"));
      p3.setCategory(categories.get(1));

      var p4 = new Product();
      p4.setName("Perforadora");
      p4.setDescription("Perforadora de papel de 2 huecos");
      p4.setCostPrice(new BigDecimal("10000"));
      p4.setSalePrice(new BigDecimal("18000"));
      p4.setCategory(categories.get(1));

      var p5 = new Product();
      p5.setName("Papel Bond A4");
      p5.setDescription("Resma de papel bond A4, 75g, 500 hojas");
      p5.setCostPrice(new BigDecimal("12000"));
      p5.setSalePrice(new BigDecimal("20000"));
      p5.setCategory(categories.get(2));

      productRepository.saveAll(List.of(p1, p2, p3, p4, p5));
      log.info("Products seeded.");
    } else {
      log.info("Products already exist, skipping seeding.");
    }
  }

  private void seedUsers() {
    if (userRepository.count() == 0) {
      log.info("Seeding users...");

      User adminUser = User.builder()
          .name("Admin")
          .email("admin@sigrap.com")
          .password(passwordEncoder.encode("Admin123!"))
          .build();

      userRepository.save(adminUser);
      log.info("Users seeded.");
    } else {
      log.info("Users already exist, skipping seeding.");
    }
  }
}