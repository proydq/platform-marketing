package com.platform.marketing.modules.ai.repository;

import com.platform.marketing.modules.ai.entity.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {
    
    List<MessageTemplate> findByTypeAndLanguageAndIsActiveTrue(String type, String language);
    
    List<MessageTemplate> findByIndustryAndTypeAndLanguageAndIsActiveTrue(
        String industry, String type, String language);
    
    @Query("SELECT t FROM MessageTemplate t WHERE t.isActive = true AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:industry IS NULL OR t.industry = :industry) AND " +
           "(:language IS NULL OR t.language = :language) AND " +
           "(:category IS NULL OR t.category = :category)")
    Page<MessageTemplate> findTemplatesWithFilters(
        @Param("type") String type,
        @Param("industry") String industry, 
        @Param("language") String language,
        @Param("category") String category,
        Pageable pageable);
    
    @Query("SELECT t FROM MessageTemplate t WHERE t.isActive = true ORDER BY t.successRate DESC, t.usageCount DESC")
    List<MessageTemplate> findTopPerformingTemplates(Pageable pageable);
    
    @Query("SELECT DISTINCT t.industry FROM MessageTemplate t WHERE t.isActive = true")
    List<String> findDistinctIndustries();
    
    @Query("SELECT DISTINCT t.category FROM MessageTemplate t WHERE t.isActive = true")
    List<String> findDistinctCategories();
}