package com.platform.marketing.modules.product.service;

import com.platform.marketing.modules.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    Page<Product> search(String name, String category, String tag, Pageable pageable);
    Optional<Product> findById(String id);
    Product create(Product product);
    Product update(String id, Product product);
    void delete(String id);
}
