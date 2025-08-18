package com.platform.marketing.modules.product.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_category")
public class ProductCategory {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "parent_id")
    private String parentId;

    @Column(length = 100)
    private String icon;

    @Column(length = 255)
    private String image;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column
    private Integer level = 1;

    @Column(length = 500)
    private String path;

    @Column(name = "product_count")
    private Integer productCount = 0;

    @Column
    private Boolean enabled = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters and setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    // 为了兼容性，保留isActive的方法
    public Boolean getIsActive() { return enabled; }
    public void setIsActive(Boolean isActive) { this.enabled = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}