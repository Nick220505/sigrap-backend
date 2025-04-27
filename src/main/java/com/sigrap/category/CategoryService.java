package com.sigrap.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getById(Integer id) {
        return categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Integer id, Category categoryDetails) {
        var category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Integer id) {
        var category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        categoryRepository.delete(category);
    }

    @Transactional
    public void deleteMany(List<Integer> ids) {
        for (Integer id : ids) {
            if (!categoryRepository.existsById(id)) {
                throw new EntityNotFoundException("Category with id " + id + " not found");
            }
        }
        categoryRepository.deleteAllById(ids);
    }
}