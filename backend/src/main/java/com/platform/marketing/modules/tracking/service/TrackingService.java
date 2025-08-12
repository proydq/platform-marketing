package com.platform.marketing.modules.tracking.service;

import com.platform.marketing.modules.tracking.entity.EmailTracking;
import com.platform.marketing.modules.tracking.entity.LinkTracking;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrackingService {
    
    // 邮件跟踪相关
    String generateTrackingId();
    
    EmailTracking createEmailTracking(Long campaignId, Long customerId, String emailAddress, String subject);
    
    void recordEmailSent(String trackingId);
    
    void recordEmailDelivered(String trackingId);
    
    void recordEmailOpened(String trackingId, HttpServletRequest request);
    
    void recordEmailClicked(String trackingId, HttpServletRequest request);
    
    void recordEmailReplied(String trackingId);
    
    void recordEmailBounced(String trackingId);
    
    void recordEmailUnsubscribed(String trackingId);
    
    Optional<EmailTracking> getEmailTracking(String trackingId);
    
    List<EmailTracking> getEmailTrackingByCampaign(Long campaignId);
    
    // 链接跟踪相关
    String createShortLink(String trackingId, String originalUrl);
    
    String getOriginalUrl(String shortUrl);
    
    void recordLinkClick(String shortUrl, HttpServletRequest request);
    
    List<LinkTracking> getLinkTrackingByEmail(String trackingId);
    
    // 统计分析相关
    Map<String, Long> getCampaignStats(Long campaignId);
    
    Map<String, Object> getEmailPerformanceStats(String trackingId);
    
    List<Map<String, Object>> getCampaignTimeSeriesData(Long campaignId, int days);
    
    Map<String, Object> getOverallStats();
    
    // 像素追踪图片
    byte[] getTrackingPixel();
}