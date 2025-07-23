package com.platform.marketing.modules.campaign.service;

import com.platform.marketing.modules.campaign.entity.MarketingCampaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MarketingCampaignService {
    Page<MarketingCampaign> search(String status, String channel, String keyword, Pageable pageable);
    Optional<MarketingCampaign> findById(String id);
    MarketingCampaign create(MarketingCampaign campaign);
    MarketingCampaign update(String id, MarketingCampaign campaign);
    void delete(String id);
    void updateStatus(String id, String status);
}
