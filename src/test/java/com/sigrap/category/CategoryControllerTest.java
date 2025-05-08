package com.sigrap.category;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

class CategoryControllerTest {

  private MockMvc mockMvc;
  private CategoryService categoryService;
  private ObjectMapper objectMapper;

  @ControllerAdvice
  public static class TestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  @BeforeEach
  void setup() {
    categoryService = mock(CategoryService.class);
    objectMapper = new ObjectMapper();
    CategoryController categoryController = new CategoryController(categoryService);

    mockMvc = standaloneSetup(categoryController)
        .setControllerAdvice(new TestExceptionHandler())
        .build();
  }

  @Test
  void getAll_shouldReturnAllCategories() throws Exception {
    Integer id1 = 1;
    Integer id2 = 2;

    CategoryInfo category1 = CategoryInfo.builder()
        .id(id1)
        .name("Category 1")
        .build();

    CategoryInfo category2 = CategoryInfo.builder()
        .id(id2)
        .name("Category 2")
        .build();

    List<CategoryInfo> categories = List.of(category1, category2);
    when(categoryService.findAll()).thenReturn(categories);

    mockMvc.perform(get("/api/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id1))
        .andExpect(jsonPath("$[0].name").value("Category 1"))
        .andExpect(jsonPath("$[1].id").value(id2))
        .andExpect(jsonPath("$[1].name").value("Category 2"));
  }

  @Test
  void getById_shouldReturnCategory_whenExists() throws Exception {
    Integer id = 1;
    CategoryInfo category = CategoryInfo.builder()
        .id(id)
        .name("Test Category")
        .build();

    when(categoryService.findById(id)).thenReturn(category);

    mockMvc.perform(get("/api/categories/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Test Category"));
  }

  @Test
  void getById_shouldReturnNotFound_whenCategoryDoesNotExist() throws Exception {
    Integer id = 1;
    when(categoryService.findById(id)).thenThrow(new EntityNotFoundException());

    mockMvc.perform(get("/api/categories/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_shouldCreateCategory() throws Exception {
    Integer id = 1;
    CategoryInfo categoryInfo = CategoryInfo.builder()
        .id(id)
        .name("New Category")
        .description("Test Description")
        .build();

    when(categoryService.create(any(CategoryData.class))).thenReturn(categoryInfo);

    CategoryData requestData = CategoryData.builder()
        .name("New Category")
        .description("Test Description")
        .build();

    mockMvc.perform(post("/api/categories")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestData)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("New Category"))
        .andExpect(jsonPath("$.description").value("Test Description"));

    verify(categoryService).create(any(CategoryData.class));
  }

  @Test
  void update_shouldUpdateCategory_whenExists() throws Exception {
    Integer id = 1;
    CategoryInfo updatedCategory = CategoryInfo.builder()
        .id(id)
        .name("Updated Category")
        .description("Updated Description")
        .build();

    when(categoryService.update(eq(id), any(CategoryData.class))).thenReturn(updatedCategory);

    CategoryData updateRequest = CategoryData.builder()
        .name("Updated Category")
        .description("Updated Description")
        .build();

    mockMvc.perform(put("/api/categories/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Updated Category"))
        .andExpect(jsonPath("$.description").value("Updated Description"));

    verify(categoryService).update(eq(id), any(CategoryData.class));
  }

  @Test
  void update_shouldReturnNotFound_whenCategoryDoesNotExist() throws Exception {
    Integer id = 1;
    when(categoryService.update(eq(id), any(CategoryData.class))).thenThrow(new EntityNotFoundException());

    CategoryData updateRequest = CategoryData.builder()
        .name("Updated Category")
        .build();

    mockMvc.perform(put("/api/categories/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_shouldDeleteCategory_whenExists() throws Exception {
    Integer id = 1;
    doNothing().when(categoryService).delete(id);

    mockMvc.perform(delete("/api/categories/{id}", id))
        .andExpect(status().isNoContent());

    verify(categoryService).delete(id);
  }

  @Test
  void delete_shouldReturnNotFound_whenCategoryDoesNotExist() throws Exception {
    Integer id = 1;
    doThrow(new EntityNotFoundException()).when(categoryService).delete(id);

    mockMvc.perform(delete("/api/categories/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteMultiple_shouldDeleteCategories() throws Exception {
    List<Integer> ids = List.of(1, 2);
    doNothing().when(categoryService).deleteAllById(ids);

    mockMvc.perform(delete("/api/categories/delete-many")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(ids)))
        .andExpect(status().isNoContent());

    verify(categoryService).deleteAllById(ids);
  }

  @Test
  void deleteMultiple_shouldReturnNotFound_whenAnyCategoryDoesNotExist() throws Exception {
    List<Integer> ids = List.of(1, 2);
    doThrow(new EntityNotFoundException()).when(categoryService).deleteAllById(ids);

    mockMvc.perform(delete("/api/categories/delete-many")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(ids)))
        .andExpect(status().isNotFound());
  }
}