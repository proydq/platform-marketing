package com.platform.marketing.modules.email.repository;

import com.platform.marketing.modules.email.entity.EmailCampaignTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailCampaignTemplateRepository extends JpaRepository<EmailCampaignTemplate, String> {
    @Query("SELECT t FROM EmailCampaignTemplate t WHERE (:kw = '' OR lower(t.name) LIKE lower(concat('%',:kw,'%')) " +
            "OR lower(t.description) LIKE lower(concat('%',:kw,'%')))")
    Page<EmailCampaignTemplate> search(@Param("kw") String keyword, Pageable pageable);
}
