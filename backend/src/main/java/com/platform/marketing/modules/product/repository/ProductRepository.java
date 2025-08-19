package com.platform.marketing.modules.product.repository;

import com.platform.marketing.modules.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("SELECT p FROM Product p WHERE (:name = '' OR p.name LIKE %:name%) AND (:categoryId = '' OR p.categoryId = :categoryId)")
    Page<Product> search(String name, String categoryId, Pageable pageable);
}
