package com.unicenta.poc.application;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.unicenta.poc.domain.Category;
import com.unicenta.poc.domain.CategoryRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache allCategoriesCache;  // needed because cacheManager.getCache(...) is called

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getCategoryById_WhenFound_ShouldReturnCategory() {
        Category category = new Category("Test");
        category.setId("test-id");
        when(categoryRepository.findById("test-id")).thenReturn(Optional.of(category));

        Category found = categoryService.getCategoryById("test-id");

        assertNotNull(found);
        assertEquals("Test", found.getName());
    }

    @Test
    void getCategoryById_WhenNotFound_ShouldThrowException() {
        when(categoryRepository.findById("test-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById("test-id");
        });
    }

    @Test
    void updateCategory_WhenExists_ShouldUpdateAndReturnCategory() {
        // ✅ Stub cache ONLY here
        when(cacheManager.getCache("allCategories")).thenReturn(allCategoriesCache);
        
        Category existingCategory = new Category("Old Name");
        existingCategory.setId("existing-id");

        when(categoryRepository.findById("existing-id")).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category updatedCategory = categoryService.updateCategory("existing-id", "New Name");

        assertNotNull(updatedCategory);
        assertEquals("New Name", updatedCategory.getName());
        verify(categoryRepository).save(existingCategory);
        verify(allCategoriesCache).invalidate();
    }
}
