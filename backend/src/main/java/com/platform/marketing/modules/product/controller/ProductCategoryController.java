package com.platform.marketing.modules.product.controller;

import com.platform.marketing.modules.product.entity.ProductCategory;
import com.platform.marketing.modules.product.repository.ProductCategoryRepository;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/product-categories")
public class ProductCategoryController {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryController(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductCategory>> list() {
        List<ProductCategory> categories = productCategoryRepository.findAll();
        return ResponseEntity.success(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> get(@PathVariable String id) {
        Optional<ProductCategory> category = productCategoryRepository.findById(id);
        if (category.isPresent()) {
            return ResponseEntity.success(category.get());
        }
        return ResponseEntity.fail(404, "Category not found");
    }

    @PostMapping
    public ResponseEntity<ProductCategory> create(@RequestBody ProductCategory category) {
        category.setId(UUID.randomUUID().toString());
        ProductCategory saved = productCategoryRepository.save(category);
        return ResponseEntity.success(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> update(@PathVariable String id, @RequestBody ProductCategory category) {
        if (!productCategoryRepository.existsById(id)) {
            return ResponseEntity.fail(404, "Category not found");
        }
        category.setId(id);
        ProductCategory saved = productCategoryRepository.save(category);
        return ResponseEntity.success(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!productCategoryRepository.existsById(id)) {
            return ResponseEntity.fail(404, "Category not found");
        }
        productCategoryRepository.deleteById(id);
        return ResponseEntity.success(null);
    }
}