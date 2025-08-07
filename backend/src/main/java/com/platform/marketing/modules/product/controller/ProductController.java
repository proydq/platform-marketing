package com.platform.marketing.modules.product.controller;

import com.platform.marketing.modules.product.entity.Product;
import com.platform.marketing.modules.product.service.ProductService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasPermission('product:list')")
    public ResponseEntity<ResponsePageDataEntity<Product>> list(@RequestParam(defaultValue = "") String name,
                                                                 @RequestParam(defaultValue = "") String category,
                                                                 @RequestParam(defaultValue = "") String tag,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Page<Product> p = productService.search(name, category, tag, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @PostMapping
    @PreAuthorize("hasPermission('product:create')")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.success(productService.create(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('product:update')")
    public ResponseEntity<Product> update(@PathVariable String id, @RequestBody Product product) {
        return ResponseEntity.success(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('product:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.success(null);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasPermission('product:upload')")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.fail(400, "file is empty");
        }
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path dir = Paths.get("uploads");
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            Map<String, String> resp = new HashMap<>();
            resp.put("url", "/uploads/" + filename);
            return ResponseEntity.success(resp);
        } catch (IOException e) {
            return ResponseEntity.fail(500, "upload error");
        }
    }
}
