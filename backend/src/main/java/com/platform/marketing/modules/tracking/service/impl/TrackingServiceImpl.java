package com.platform.marketing.modules.tracking.service.impl;

import com.platform.marketing.modules.tracking.entity.EmailTracking;
import com.platform.marketing.modules.tracking.entity.LinkTracking;
import com.platform.marketing.modules.tracking.repository.EmailTrackingRepository;
import com.platform.marketing.modules.tracking.repository.LinkTrackingRepository;
import com.platform.marketing.modules.tracking.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TrackingServiceImpl implements TrackingService {
    
    @Autowired
    private EmailTrackingRepository emailTrackingRepository;
    
    @Autowired
    private LinkTrackingRepository linkTrackingRepository;
    
    private static final String DOMAIN = "https://yourtracking.domain.com/t/";
    
    @Override
    public String generateTrackingId() {
        return "track_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public EmailTracking createEmailTracking(Long campaignId, Long customerId, String emailAddress, String subject) {
        EmailTracking tracking = new EmailTracking();
        tracking.setTrackingId(generateTrackingId());
        tracking.setCampaignId(campaignId);
        tracking.setCustomerId(customerId);
        tracking.setEmailAddress(emailAddress);
        tracking.setSubject(subject);
        tracking.setStatus("CREATED");
        
        return emailTrackingRepository.save(tracking);
    }
    
    @Override
    public void recordEmailSent(String trackingId) {
        emailTrackingRepository.findByTrackingId(trackingId).ifPresent(tracking -> {
            tracking.setSentAt(LocalDateTime.now());
            tracking.setStatus("SENT");
            emailTrackingRepository.save(tracking);
        });
    }
    
    @Override
    public void recordEmailDelivered(String trackingId) {
        emailTrackingRepository.findByTrackingId(trackingId).ifPresent(tracking -> {
            tracking.setDeliveredAt(LocalDateTime.now());
            tracking.setStatus("DELIVERED");
            emailTrackingRepository.save(tracking);
        });
    }
    
    @Override
    public void recordEmailOpened(String trackingId, HttpServletRequest request) {
        emailTrackingRepository.findByTrackingId(trackingId).ifPresent(tracking -> {
            LocalDateTime now = LocalDateTime.now();
            
            if (tracking.getOpenedAt() == null) {
                tracking.setOpenedAt(now);
                tracking.setStatus("OPENED");
            }
            
            tracking.setOpenCount(tracking.getOpenCount() + 1);
            
            if (request != null) {
                tracking.setUserAgent(request.getHeader("User-Agent"));
                tracking.setIpAddress(getClientIpAddress(request));
                tracking.setDeviceType(determineDeviceType(request.getHeader("User-Agent")));
            }
            
            emailTrackingRepository.save(tracking);
        });
    }
    
    @Override
    public void recordEmailClicked(String trackingId, HttpServletRequest request) {
        emailTrackingRepository.findByTrackingId(trackingId).ifPresent(tracking -> {
            LocalDateTime now = LocalDateTime.now();
            
            if (tracking.getFirstClickAt() == null) {
                tracking.setFirstClickAt(now);
                if (!"REPLIED".equals(tracking.getStatus())) {
                    tracking.setStatus("CLICKED");
                }
            }
            
            tracking.setClickCount(tracking.getClickCount() + 1);
            
            if (request != null) {
                tracking.setUserAgent(request.getHeader("User-Agent"));
                tracking.setIpAddress(getClientIpAddress(request));
                tracking.setDeviceType(determineDeviceType(request.getHeader("User-Agent")));
            }
            
            emailTrackingRepository.save(tracking);
        });
    }
    
    @Override
    public void recordEmailReplied(String trackingId) {
        emailTrackingRepository.findByTrackingId(trackingId).ifPresent(tracking -> {
            tracking.setRepliedAt(LocalDateTime.now());
            tracking.setStatus("REPLIED");
            emailTrackingRepository.save(tracking);
        });
    }
    
    @Override
    public void recordEmailBounced(String trackingId) {
        emailTrackingRepository.findByTrackingId(trackingId).ifPresent(tracking -> {
            tracking.setBouncedAt(LocalDateTime.now());
            tracking.setStatus("BOUNCED");
            emailTrackingRepository.save(tracking);
        });
    }
    
    @Override
    public void recordEmailUnsubscribed(String trackingId) {
        emailTrackingRepository.findByTrackingId(trackingId).ifPresent(tracking -> {
            tracking.setUnsubscribedAt(LocalDateTime.now());
            tracking.setStatus("UNSUBSCRIBED");
            emailTrackingRepository.save(tracking);
        });
    }
    
    @Override
    public Optional<EmailTracking> getEmailTracking(String trackingId) {
        return emailTrackingRepository.findByTrackingId(trackingId);
    }
    
    @Override
    public List<EmailTracking> getEmailTrackingByCampaign(Long campaignId) {
        return emailTrackingRepository.findByCampaignId(campaignId);
    }
    
    @Override
    public String createShortLink(String trackingId, String originalUrl) {
        String shortCode = generateShortCode();
        
        LinkTracking linkTracking = new LinkTracking();
        linkTracking.setTrackingId(trackingId);
        linkTracking.setOriginalUrl(originalUrl);
        linkTracking.setShortUrl(shortCode);
        
        linkTrackingRepository.save(linkTracking);
        
        return DOMAIN + shortCode;
    }
    
    @Override
    public String getOriginalUrl(String shortUrl) {
        String shortCode = extractShortCode(shortUrl);
        Optional<LinkTracking> tracking = linkTrackingRepository.findByShortUrl(shortCode);
        return tracking.map(LinkTracking::getOriginalUrl).orElse(null);
    }
    
    @Override
    public void recordLinkClick(String shortUrl, HttpServletRequest request) {
        String shortCode = extractShortCode(shortUrl);
        linkTrackingRepository.findByShortUrl(shortCode).ifPresent(linkTracking -> {
            LocalDateTime now = LocalDateTime.now();
            
            if (linkTracking.getClickedAt() == null) {
                linkTracking.setClickedAt(now);
            }
            
            linkTracking.setClickCount(linkTracking.getClickCount() + 1);
            
            String ipAddress = getClientIpAddress(request);
            if (!ipAddress.equals(linkTracking.getIpAddress())) {
                linkTracking.setUniqueClicks(linkTracking.getUniqueClicks() + 1);
            }
            
            linkTracking.setIpAddress(ipAddress);
            linkTracking.setUserAgent(request.getHeader("User-Agent"));
            linkTracking.setReferrer(request.getHeader("Referer"));
            
            linkTrackingRepository.save(linkTracking);
            
            recordEmailClicked(linkTracking.getTrackingId(), request);
        });
    }
    
    @Override
    public List<LinkTracking> getLinkTrackingByEmail(String trackingId) {
        return linkTrackingRepository.findByTrackingId(trackingId);
    }
    
    @Override
    public Map<String, Long> getCampaignStats(Long campaignId) {
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("totalSent", emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "SENT") +
                              emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "DELIVERED") +
                              emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "OPENED") +
                              emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "CLICKED") +
                              emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "REPLIED"));
        
        stats.put("delivered", emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "DELIVERED") +
                              emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "OPENED") +
                              emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "CLICKED") +
                              emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "REPLIED"));
        
        stats.put("opened", emailTrackingRepository.countOpenedEmailsByCampaignId(campaignId));
        stats.put("clicked", emailTrackingRepository.countClickedEmailsByCampaignId(campaignId));
        stats.put("replied", emailTrackingRepository.countRepliedEmailsByCampaignId(campaignId));
        stats.put("bounced", emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "BOUNCED"));
        stats.put("unsubscribed", emailTrackingRepository.countByCampaignIdAndStatus(campaignId, "UNSUBSCRIBED"));
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getEmailPerformanceStats(String trackingId) {
        Map<String, Object> stats = new HashMap<>();
        
        Optional<EmailTracking> trackingOpt = emailTrackingRepository.findByTrackingId(trackingId);
        if (trackingOpt.isPresent()) {
            EmailTracking tracking = trackingOpt.get();
            
            stats.put("sent", tracking.getSentAt() != null);
            stats.put("delivered", tracking.getDeliveredAt() != null);
            stats.put("opened", tracking.getOpenedAt() != null);
            stats.put("clicked", tracking.getFirstClickAt() != null);
            stats.put("replied", tracking.getRepliedAt() != null);
            stats.put("bounced", tracking.getBouncedAt() != null);
            stats.put("unsubscribed", tracking.getUnsubscribedAt() != null);
            
            stats.put("openCount", tracking.getOpenCount());
            stats.put("clickCount", tracking.getClickCount());
            stats.put("status", tracking.getStatus());
            stats.put("deviceType", tracking.getDeviceType());
            
            List<LinkTracking> links = linkTrackingRepository.findByTrackingId(trackingId);
            stats.put("totalLinkClicks", links.stream().mapToInt(LinkTracking::getClickCount).sum());
            stats.put("uniqueLinkClicks", links.stream().mapToInt(LinkTracking::getUniqueClicks).sum());
        }
        
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getCampaignTimeSeriesData(Long campaignId, int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<Object[]> dailyStats = emailTrackingRepository.getDailySendStatsByCampaignId(
                campaignId, startDate, endDate);
        
        List<Map<String, Object>> timeSeriesData = new ArrayList<>();
        for (Object[] stat : dailyStats) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("date", stat[0]);
            dataPoint.put("count", stat[1]);
            timeSeriesData.add(dataPoint);
        }
        
        return timeSeriesData;
    }
    
    @Override
    public Map<String, Object> getOverallStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalEmails = emailTrackingRepository.count();
        long totalOpened = emailTrackingRepository.countOpenedEmailsByCampaignId(null);
        long totalClicked = emailTrackingRepository.countClickedEmailsByCampaignId(null);
        long totalReplied = emailTrackingRepository.countRepliedEmailsByCampaignId(null);
        
        stats.put("totalEmails", totalEmails);
        stats.put("totalOpened", totalOpened);
        stats.put("totalClicked", totalClicked);
        stats.put("totalReplied", totalReplied);
        
        if (totalEmails > 0) {
            stats.put("openRate", (double) totalOpened / totalEmails * 100);
            stats.put("clickRate", (double) totalClicked / totalEmails * 100);
            stats.put("replyRate", (double) totalReplied / totalEmails * 100);
        } else {
            stats.put("openRate", 0.0);
            stats.put("clickRate", 0.0);
            stats.put("replyRate", 0.0);
        }
        
        return stats;
    }
    
    @Override
    public byte[] getTrackingPixel() {
        // 1x1 透明像素 PNG
        return new byte[]{
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte)0xC4,
            (byte)0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54, 0x78, (byte)0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
            0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte)0xB4, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte)0xAE,
            0x42, 0x60, (byte)0x82
        };
    }
    
    private String generateShortCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
    
    private String extractShortCode(String shortUrl) {
        if (shortUrl.startsWith(DOMAIN)) {
            return shortUrl.substring(DOMAIN.length());
        }
        return shortUrl;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String determineDeviceType(String userAgent) {
        if (userAgent == null) return "UNKNOWN";
        
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "MOBILE";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "TABLET";
        } else {
            return "DESKTOP";
        }
    }
}