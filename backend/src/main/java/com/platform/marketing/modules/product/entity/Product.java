package com.platform.marketing.modules.product.entity;

import com.platform.marketing.modules.product.converter.StringListConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Convert(converter = StringListConverter.class)
    @Column(name = "image_list", columnDefinition = "text")
    private List<String> imageList;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "price_usd", precision = 10, scale = 2)
    private BigDecimal priceUSD;

    @Column(name = "price_cny", precision = 10, scale = 2)
    private BigDecimal priceCNY;

    @Column(name = "price_eur", precision = 10, scale = 2)
    private BigDecimal priceEUR;

    @Column(name = "spec_pdf")
    private String specPdf;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> tags;

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

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getImageList() { return imageList; }
    public void setImageList(List<String> imageList) { this.imageList = imageList; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public BigDecimal getPriceUSD() { return priceUSD; }
    public void setPriceUSD(BigDecimal priceUSD) { this.priceUSD = priceUSD; }

    public BigDecimal getPriceCNY() { return priceCNY; }
    public void setPriceCNY(BigDecimal priceCNY) { this.priceCNY = priceCNY; }

    public BigDecimal getPriceEUR() { return priceEUR; }
    public void setPriceEUR(BigDecimal priceEUR) { this.priceEUR = priceEUR; }

    public String getSpecPdf() { return specPdf; }
    public void setSpecPdf(String specPdf) { this.specPdf = specPdf; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
