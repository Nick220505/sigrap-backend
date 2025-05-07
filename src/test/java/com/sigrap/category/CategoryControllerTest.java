package com.sigrap.category;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
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
    List<CategoryInfo> categories = List.of(
        createCategoryInfo(1, "Category 1"),
        createCategoryInfo(2, "Category 2"));
    when(categoryService.findAll()).thenReturn(categories);

    mockMvc.perform(get("/api/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Category 1"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].name").value("Category 2"));
  }

  @Test
  void getById_shouldReturnCategory_whenExists() throws Exception {
    Integer id = 1;
    CategoryInfo category = createCategoryInfo(id, "Test Category");
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
    CategoryData categoryData = new CategoryData();
    categoryData.setName("New Category");
    categoryData.setDescription("Test Description");

    CategoryInfo createdCategory = new CategoryInfo();
    createdCategory.setId(1);
    createdCategory.setName("New Category");
    createdCategory.setDescription("Test Description");

    when(categoryService.create(any(CategoryData.class))).thenReturn(createdCategory);

    mockMvc.perform(post("/api/categories")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(categoryData)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("New Category"))
        .andExpect(jsonPath("$.description").value("Test Description"));

    verify(categoryService).create(any(CategoryData.class));
  }

  @Test
  void update_shouldUpdateCategory_whenExists() throws Exception {
    Integer id = 1;
    CategoryData categoryData = new CategoryData();
    categoryData.setName("Updated Category");
    categoryData.setDescription("Updated Description");

    CategoryInfo updatedCategory = new CategoryInfo();
    updatedCategory.setId(id);
    updatedCategory.setName("Updated Category");
    updatedCategory.setDescription("Updated Description");

    when(categoryService.update(eq(id), any(CategoryData.class))).thenReturn(updatedCategory);

    mockMvc.perform(put("/api/categories/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(categoryData)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Updated Category"))
        .andExpect(jsonPath("$.description").value("Updated Description"));

    verify(categoryService).update(eq(id), any(CategoryData.class));
  }

  @Test
  void update_shouldReturnNotFound_whenCategoryDoesNotExist() throws Exception {
    Integer id = 1;
    CategoryData categoryData = new CategoryData();
    categoryData.setName("Updated Category");

    when(categoryService.update(eq(id), any(CategoryData.class))).thenThrow(new EntityNotFoundException());

    mockMvc.perform(put("/api/categories/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(categoryData)))
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

  private CategoryInfo createCategoryInfo(Integer id, String name) {
    CategoryInfo categoryInfo = new CategoryInfo();
    categoryInfo.setId(id);
    categoryInfo.setName(name);
    return categoryInfo;
  }
}