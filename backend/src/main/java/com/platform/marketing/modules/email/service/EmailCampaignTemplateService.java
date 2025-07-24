package com.platform.marketing.modules.email.service;

import com.platform.marketing.modules.email.entity.EmailCampaignTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmailCampaignTemplateService {
    Page<EmailCampaignTemplate> search(String keyword, Pageable pageable);
    Optional<EmailCampaignTemplate> findById(String id);
    EmailCampaignTemplate create(EmailCampaignTemplate template);
    EmailCampaignTemplate update(String id, EmailCampaignTemplate template);
    void delete(String id);
}
