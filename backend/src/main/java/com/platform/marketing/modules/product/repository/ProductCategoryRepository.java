package com.platform.marketing.modules.product.repository;

import com.platform.marketing.modules.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
    
    List<ProductCategory> findByEnabledTrueOrderBySortOrderAsc();
    
    List<ProductCategory> findByParentIdOrderBySortOrderAsc(String parentId);
    
    List<ProductCategory> findByParentIdIsNullOrderBySortOrderAsc();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = ?1")
    long countProductsByCategoryId(String categoryId);
}