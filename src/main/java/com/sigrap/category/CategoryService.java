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
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryInfo> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toInfo)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryInfo findById(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return categoryMapper.toInfo(category);
    }

    @Transactional
    public CategoryInfo create(CategoryData categoryData) {
        Category category = categoryMapper.toEntity(categoryData);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toInfo(savedCategory);
    }

    @Transactional
    public CategoryInfo update(Integer id, CategoryData categoryData) {
        Category category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        categoryMapper.updateEntityFromData(categoryData, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toInfo(updatedCategory);
    }

    @Transactional
    public void delete(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        categoryRepository.delete(category);
    }

    @Transactional
    public void deleteAllById(List<Integer> ids) {
        ids.forEach(id -> {
            if (!categoryRepository.existsById(id)) {
                throw new EntityNotFoundException("Category with id " + id + " not found");
            }
        });
        categoryRepository.deleteAllById(ids);
    }
}