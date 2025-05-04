package com.sigrap.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryInfo> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public CategoryInfo findById(@PathVariable Integer id) {
        return categoryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryInfo create(@Valid @RequestBody CategoryData categoryData) {
        return categoryService.create(categoryData);
    }

    @PutMapping("/{id}")
    public CategoryInfo update(@PathVariable Integer id, @Valid @RequestBody CategoryData categoryData) {
        return categoryService.update(id, categoryData);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        categoryService.delete(id);
    }

    @DeleteMapping("/delete-many")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllById(@RequestBody List<Integer> ids) {
        categoryService.deleteAllById(ids);
    }
}