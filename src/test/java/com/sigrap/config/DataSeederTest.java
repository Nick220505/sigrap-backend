package com.sigrap.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.product.ProductRepository;
import com.sigrap.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private DataSeeder dataSeeder;

  @BeforeEach
  void setUp() {
    dataSeeder = spy(new DataSeeder(
        categoryRepository,
        productRepository,
        userRepository,
        passwordEncoder));
  }

  @Test
  void run_shouldSeedAllEntities_whenRepositoriesAreEmpty() throws Exception {
    when(categoryRepository.count()).thenReturn(0L);
    when(productRepository.count()).thenReturn(0L);
    when(userRepository.count()).thenReturn(0L);

    List<Category> categories = new ArrayList<>();
    for (int i = 0; i < 12; i++) {
      Category category = new Category();
      category.setId(i);
      categories.add(category);
    }
    when(categoryRepository.findAll()).thenReturn(categories);
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    dataSeeder.run();

    verify(categoryRepository, times(1)).saveAll(anyList());
    verify(productRepository, times(1)).saveAll(anyList());
    verify(userRepository, times(1)).saveAll(anyList());
  }

  @Test
  void run_shouldSkipSeeding_whenRepositoriesHaveData() throws Exception {
    when(categoryRepository.count()).thenReturn(5L);
    when(productRepository.count()).thenReturn(10L);
    when(userRepository.count()).thenReturn(2L);

    dataSeeder.run();

    verify(categoryRepository, never()).saveAll(anyList());
    verify(productRepository, never()).saveAll(anyList());
    verify(userRepository, never()).saveAll(anyList());
  }

  @Test
  void seedProducts_shouldWarnAndReturn_whenNoCategoriesExist() throws Exception {
    when(categoryRepository.count()).thenReturn(0L);
    when(productRepository.count()).thenReturn(0L);
    when(userRepository.count()).thenReturn(0L);

    when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

    dataSeeder.run();

    verify(productRepository, never()).saveAll(anyList());
  }

  @Test
  void seedCategories_shouldCreateCategories_whenRepositoryIsEmpty() throws Exception {
    when(categoryRepository.count()).thenReturn(0L);
    when(productRepository.count()).thenReturn(1L);
    when(userRepository.count()).thenReturn(1L);

    dataSeeder.run();

    verify(categoryRepository, times(1)).saveAll(anyList());
  }

  @Test
  void seedProducts_shouldCreateProducts_withCorrectCategories() throws Exception {
    when(categoryRepository.count()).thenReturn(0L);
    when(productRepository.count()).thenReturn(0L);
    when(userRepository.count()).thenReturn(1L);

    List<Category> categories = new ArrayList<>();
    for (int i = 0; i < 12; i++) {
      Category category = new Category();
      category.setId(i);
      categories.add(category);
    }
    when(categoryRepository.findAll()).thenReturn(categories);
    when(categoryRepository.saveAll(anyList())).thenReturn(categories);

    dataSeeder.run();

    verify(productRepository, times(1)).saveAll(anyList());
  }

  @Test
  void seedUsers_shouldCreateDefaultUsers_whenRepositoryIsEmpty() throws Exception {
    when(categoryRepository.count()).thenReturn(1L);
    when(productRepository.count()).thenReturn(1L);
    when(userRepository.count()).thenReturn(0L);
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    dataSeeder.run();

    verify(userRepository, times(1)).saveAll(anyList());
  }
}