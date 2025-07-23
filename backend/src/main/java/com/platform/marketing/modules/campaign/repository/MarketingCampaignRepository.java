package com.platform.marketing.modules.campaign.repository;

import com.platform.marketing.modules.campaign.entity.MarketingCampaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketingCampaignRepository extends JpaRepository<MarketingCampaign, String> {
    @Query("SELECT c FROM MarketingCampaign c WHERE " +
           "(:status = '' OR c.status = :status) AND " +
           "(:channel = '' OR c.channel = :channel) AND " +
           "(:kw = '' OR lower(c.name) LIKE lower(concat('%',:kw,'%')) OR " +
           "lower(c.description) LIKE lower(concat('%',:kw,'%')))" )
    Page<MarketingCampaign> search(@Param("status") String status,
                                   @Param("channel") String channel,
                                   @Param("kw") String keyword,
                                   Pageable pageable);
}
