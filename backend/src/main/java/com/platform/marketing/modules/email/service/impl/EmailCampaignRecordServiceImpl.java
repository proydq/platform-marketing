package com.platform.marketing.modules.email.service.impl;

import com.platform.marketing.modules.email.entity.EmailCampaignRecord;
import com.platform.marketing.modules.email.repository.EmailCampaignRecordRepository;
import com.platform.marketing.modules.email.service.EmailCampaignRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailCampaignRecordServiceImpl implements EmailCampaignRecordService {

    private final EmailCampaignRecordRepository recordRepository;

    public EmailCampaignRecordServiceImpl(EmailCampaignRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public Page<EmailCampaignRecord> search(String status, Pageable pageable) {
        if (status == null) status = "";
        return recordRepository.search(status, pageable);
    }

    @Override
    @Transactional
    public EmailCampaignRecord create(EmailCampaignRecord record) {
        return recordRepository.save(record);
    }
}
