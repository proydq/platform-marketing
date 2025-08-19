package com.platform.marketing.modules.product.service.impl;

import com.platform.marketing.modules.product.entity.Product;
import com.platform.marketing.modules.product.repository.ProductRepository;
import com.platform.marketing.modules.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> search(String name, String category, String tag, Pageable pageable) {
        if (name == null) name = "";
        if (category == null) category = "";
        Page<Product> page = productRepository.search(name, category, pageable);
        if (tag == null || tag.isEmpty()) {
            return page;
        }
        List<Product> filtered = page.getContent().stream()
                .filter(p -> p.getTags() != null && p.getTags().contains(tag))
                .collect(Collectors.toList());
        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product update(String id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        existing.setName(product.getName());
        existing.setCategoryId(product.getCategoryId());
        existing.setBrand(product.getBrand());
        existing.setShortDescription(product.getShortDescription());
        existing.setDescription(product.getDescription());
        existing.setStatus(product.getStatus());
        existing.setFeatured(product.getFeatured());
        existing.setTags(product.getTags());
        existing.setSeoKeywords(product.getSeoKeywords());
        existing.setImages(product.getImages());
        existing.setVideos(product.getVideos());
        existing.setDocuments(product.getDocuments());
        existing.setPrices(product.getPrices());
        existing.setSpecifications(product.getSpecifications());
        existing.setLanguages(product.getLanguages());
        existing.setSortOrder(product.getSortOrder());
        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        productRepository.deleteById(id);
    }
}
