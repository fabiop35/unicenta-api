package com.unicenta.poc.interfaces;

import com.unicenta.poc.application.CategoryService;
import com.unicenta.poc.domain.Category;
import com.unicenta.poc.interfaces.dto.NameDto;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "http://localhost:4200, https://localhost:4200, http://192.168.10.3:4200, https://192.168.10.3:4200")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody NameDto dto) {
        Category createdCategory = categoryService.createCategory(dto.getName());
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable String id, @Valid @RequestBody NameDto dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto.getName()));
    }
}
