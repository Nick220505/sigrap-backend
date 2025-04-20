package com.sigrap.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

  private final CategoryRepository categoryRepository;

  @Override
  public void run(String... args) throws Exception {
    seedCategories();
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
}