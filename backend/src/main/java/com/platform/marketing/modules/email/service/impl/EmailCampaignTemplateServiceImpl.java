package com.platform.marketing.modules.email.service.impl;

import com.platform.marketing.modules.email.entity.EmailCampaignTemplate;
import com.platform.marketing.modules.email.repository.EmailCampaignTemplateRepository;
import com.platform.marketing.modules.email.service.EmailCampaignTemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmailCampaignTemplateServiceImpl implements EmailCampaignTemplateService {

    private final EmailCampaignTemplateRepository templateRepository;

    public EmailCampaignTemplateServiceImpl(EmailCampaignTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public Page<EmailCampaignTemplate> search(String keyword, Pageable pageable) {
        if (keyword == null) keyword = "";
        return templateRepository.search(keyword, pageable);
    }

    @Override
    public Optional<EmailCampaignTemplate> findById(String id) {
        return templateRepository.findById(id);
    }

    @Override
    @Transactional
    public EmailCampaignTemplate create(EmailCampaignTemplate template) {
        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public EmailCampaignTemplate update(String id, EmailCampaignTemplate template) {
        EmailCampaignTemplate existing = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));
        existing.setName(template.getName());
        existing.setDescription(template.getDescription());
        existing.setContent(template.getContent());
        return templateRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        templateRepository.deleteById(id);
    }
}
