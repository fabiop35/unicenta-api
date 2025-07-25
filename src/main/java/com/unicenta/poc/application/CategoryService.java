package com.unicenta.poc.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(String name) {
        Category category = new Category(name);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    /**
     * Updates an existing category's name.
     *
     * @param id
     * @return
     */
    @Transactional
    public Category updateCategory(String id, String newName) {
        Category category = getCategoryById(id); // Reuse getById to handle the not-found case
        category.setName(newName);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
