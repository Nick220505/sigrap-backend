package com.sigrap.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User("test@example.com", "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
    }

    @Test
    void crudOperations_shouldSucceed() throws Exception {
        CategoryData categoryData = new CategoryData();
        categoryData.setName("Test Category");
        categoryData.setDescription("This is a test category");

        MvcResult createResult = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Category"))
                .andExpect(jsonPath("$.description").value("This is a test category"))
                .andReturn();

        CategoryInfo createdCategory = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                CategoryInfo.class);

        Integer categoryId = createdCategory.getId();
        assertThat(categoryId).isNotNull();

        mockMvc.perform(get("/api/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Test Category"))
                .andExpect(jsonPath("$.description").value("This is a test category"));

        MvcResult getAllResult = mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryInfo> categories = objectMapper.readValue(
                getAllResult.getResponse().getContentAsString(),
                new TypeReference<List<CategoryInfo>>() {
                });

        assertThat(categories).isNotEmpty();
        assertThat(categories.stream().anyMatch(c -> c.getId().equals(categoryId))).isTrue();

        CategoryData updatedData = new CategoryData();
        updatedData.setName("Updated Category");
        updatedData.setDescription("This is an updated test category");

        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Updated Category"))
                .andExpect(jsonPath("$.description").value("This is an updated test category"));

        Category updatedCategory = categoryRepository.findById(categoryId)
                .orElseThrow();
        assertThat(updatedCategory.getName()).isEqualTo("Updated Category");
        assertThat(updatedCategory.getDescription()).isEqualTo("This is an updated test category");

        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.findById(categoryId)).isEmpty();
    }

    @Test
    void getNonExistentCategory_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateNonExistentCategory_shouldReturnNotFound() throws Exception {
        CategoryData categoryData = new CategoryData();
        categoryData.setName("Non-existent Category");

        mockMvc.perform(put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNonExistentCategory_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMultipleCategories_shouldSucceed() throws Exception {
        Category category1 = new Category();
        category1.setName("Category 1");
        category1.setDescription("Description 1");
        category1 = categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Category 2");
        category2.setDescription("Description 2");
        category2 = categoryRepository.save(category2);

        List<Integer> idsToDelete = Arrays.asList(category1.getId(), category2.getId());

        mockMvc.perform(delete("/api/categories/delete-many")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idsToDelete)))
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.findById(category1.getId())).isEmpty();
        assertThat(categoryRepository.findById(category2.getId())).isEmpty();
    }
}