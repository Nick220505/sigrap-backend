package com.sigrap.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  @Override
  public void run(String... args) throws Exception {
    seedCategories();
    seedProducts();
  }

  private void seedCategories() {
    if (categoryRepository.count() == 0) {
      log.info("Seeding categories...");

      Category cat1 = new Category();
      cat1.setName("Útiles Escolares");
      cat1.setDescription("Artículos para estudiantes y actividades escolares.");

      Category cat2 = new Category();
      cat2.setName("Artículos de Oficina");
      cat2.setDescription("Suministros básicos para el trabajo de oficina.");

      Category cat3 = new Category();
      cat3.setName("Papelería General");
      cat3.setDescription("Papeles, sobres, cuadernos y otros artículos de papel.");

      Category cat4 = new Category();
      cat4.setName("Regalos y Detalles");
      cat4.setDescription("Artículos para regalo, tarjetas y empaques.");

      Category cat5 = new Category();
      cat5.setName("Tecnología Básica");
      cat5.setDescription("Accesorios de computación básicos y almacenamiento.");

      categoryRepository.saveAll(List.of(cat1, cat2, cat3, cat4, cat5));
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

      Category escolares = categories.get(0);

      Product p1 = new Product();
      p1.setName("Cuaderno Universitario");
      p1.setDescription("Cuaderno universitario de 100 hojas, cuadriculado");
      p1.setSku("ESC-001");
      p1.setCostPrice(new BigDecimal("1.50"));
      p1.setSalePrice(new BigDecimal("2.50"));
      p1.setCategory(escolares);
      p1.setActive(true);
      p1.setBarcode("7891234567890");

      Product p2 = new Product();
      p2.setName("Lápiz HB");
      p2.setDescription("Lápiz grafito HB, cuerpo hexagonal");
      p2.setSku("ESC-002");
      p2.setCostPrice(new BigDecimal("0.30"));
      p2.setSalePrice(new BigDecimal("0.70"));
      p2.setCategory(escolares);
      p2.setActive(true);
      p2.setBarcode("7891234567891");

      Category oficina = categories.get(1);

      Product p3 = new Product();
      p3.setName("Grapadora");
      p3.setDescription("Grapadora metálica de escritorio");
      p3.setSku("OFI-001");
      p3.setCostPrice(new BigDecimal("3.50"));
      p3.setSalePrice(new BigDecimal("6.99"));
      p3.setCategory(oficina);
      p3.setActive(true);
      p3.setBarcode("7891234567892");

      Product p4 = new Product();
      p4.setName("Perforadora");
      p4.setDescription("Perforadora de papel de 2 huecos");
      p4.setSku("OFI-002");
      p4.setCostPrice(new BigDecimal("4.20"));
      p4.setSalePrice(new BigDecimal("7.50"));
      p4.setCategory(oficina);
      p4.setActive(true);
      p4.setBarcode("7891234567893");

      Category papeleria = categories.get(2);

      Product p5 = new Product();
      p5.setName("Papel Bond A4");
      p5.setDescription("Resma de papel bond A4, 75g, 500 hojas");
      p5.setSku("PAP-001");
      p5.setCostPrice(new BigDecimal("3.80"));
      p5.setSalePrice(new BigDecimal("5.50"));
      p5.setCategory(papeleria);
      p5.setActive(true);
      p5.setBarcode("7891234567894");

      productRepository.saveAll(List.of(p1, p2, p3, p4, p5));
      log.info("Products seeded.");
    } else {
      log.info("Products already exist, skipping seeding.");
    }
  }
}