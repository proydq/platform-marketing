package com.platform.marketing.modules.campaign.service.impl;

import com.platform.marketing.modules.campaign.entity.MarketingCampaign;
import com.platform.marketing.modules.campaign.repository.MarketingCampaignRepository;
import com.platform.marketing.modules.campaign.service.MarketingCampaignService;
import com.platform.marketing.modules.campaign.dto.MarketingCampaignDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MarketingCampaignServiceImpl implements MarketingCampaignService {

    private final MarketingCampaignRepository marketingCampaignRepository;

    public MarketingCampaignServiceImpl(MarketingCampaignRepository marketingCampaignRepository) {
        this.marketingCampaignRepository = marketingCampaignRepository;
    }

    @Override
    public Page<MarketingCampaign> search(String status, String channel, String keyword, Pageable pageable) {
        if (status == null) status = "";
        if (channel == null) channel = "";
        if (keyword == null) keyword = "";
        return marketingCampaignRepository.search(status, channel, keyword, pageable);
    }

    @Override
    public Optional<MarketingCampaign> findById(String id) {
        return marketingCampaignRepository.findById(id);
    }

    @Override
    @Transactional
    public MarketingCampaign create(MarketingCampaignDto campaign) {
        MarketingCampaign entity = new MarketingCampaign();
        MarketingCampaignDto.copyToEntity(campaign, entity);
        return marketingCampaignRepository.save(entity);
    }

    @Override
    @Transactional
    public MarketingCampaign update(String id, MarketingCampaignDto campaign) {
        MarketingCampaign existing = marketingCampaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
        MarketingCampaignDto.copyToEntity(campaign, existing);
        return marketingCampaignRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        marketingCampaignRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(String id, String status) {
        MarketingCampaign campaign = marketingCampaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
        campaign.setStatus(status);
        marketingCampaignRepository.save(campaign);
    }
}
