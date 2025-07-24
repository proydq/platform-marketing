package com.platform.marketing.modules.email.service;

import com.platform.marketing.modules.email.entity.EmailCampaignRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailCampaignRecordService {
    Page<EmailCampaignRecord> search(String status, Pageable pageable);
    EmailCampaignRecord create(EmailCampaignRecord record);
}
