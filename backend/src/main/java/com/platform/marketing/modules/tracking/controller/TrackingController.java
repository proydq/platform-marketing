package com.platform.marketing.modules.tracking.controller;

import com.platform.marketing.modules.tracking.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*")
public class TrackingController {
    
    @Autowired
    private TrackingService trackingService;
    
    @GetMapping("/pixel/{trackingId}")
    public ResponseEntity<byte[]> trackingPixel(
            @PathVariable String trackingId,
            HttpServletRequest request) {
        
        trackingService.recordEmailOpened(trackingId, request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0L);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(trackingService.getTrackingPixel());
    }
    
    @GetMapping("/click/{shortUrl}")
    public RedirectView trackClick(
            @PathVariable String shortUrl,
            HttpServletRequest request) {
        
        trackingService.recordLinkClick(shortUrl, request);
        String originalUrl = trackingService.getOriginalUrl(shortUrl);
        
        if (originalUrl != null) {
            return new RedirectView(originalUrl);
        } else {
            return new RedirectView("https://example.com/404");
        }
    }
    
    @GetMapping("/stats/campaign/{campaignId}")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<Map<String, Long>>> getCampaignStats(
            @PathVariable Long campaignId) {
        
        Map<String, Long> stats = trackingService.getCampaignStats(campaignId);
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Campaign stats retrieved", stats));
    }
    
    @GetMapping("/stats/email/{trackingId}")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<Map<String, Object>>> getEmailStats(
            @PathVariable String trackingId) {
        
        Map<String, Object> stats = trackingService.getEmailPerformanceStats(trackingId);
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Email stats retrieved", stats));
    }
    
    @GetMapping("/stats/campaign/{campaignId}/timeseries")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<List<Map<String, Object>>>> getCampaignTimeSeries(
            @PathVariable Long campaignId,
            @RequestParam(defaultValue = "30") int days) {
        
        List<Map<String, Object>> timeSeriesData = trackingService.getCampaignTimeSeriesData(campaignId, days);
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Time series data retrieved", timeSeriesData));
    }
    
    @GetMapping("/stats/overall")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<Map<String, Object>>> getOverallStats() {
        Map<String, Object> stats = trackingService.getOverallStats();
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Overall stats retrieved", stats));
    }
    
    @PostMapping("/webhook/email-reply")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<String>> handleEmailReply(
            @RequestBody Map<String, Object> payload) {
        
        String trackingId = (String) payload.get("trackingId");
        if (trackingId != null) {
            trackingService.recordEmailReplied(trackingId);
        }
        
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Email reply recorded", null));
    }
    
    @PostMapping("/webhook/email-bounce")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<String>> handleEmailBounce(
            @RequestBody Map<String, Object> payload) {
        
        String trackingId = (String) payload.get("trackingId");
        if (trackingId != null) {
            trackingService.recordEmailBounced(trackingId);
        }
        
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Email bounce recorded", null));
    }
    
    @PostMapping("/webhook/unsubscribe")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<String>> handleUnsubscribe(
            @RequestBody Map<String, Object> payload) {
        
        String trackingId = (String) payload.get("trackingId");
        if (trackingId != null) {
            trackingService.recordEmailUnsubscribed(trackingId);
        }
        
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Unsubscribe recorded", null));
    }
    
    @PostMapping("/link/create")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<Map<String, String>>> createShortLink(
            @RequestBody Map<String, String> request) {
        
        String trackingId = request.get("trackingId");
        String originalUrl = request.get("originalUrl");
        
        if (trackingId == null || originalUrl == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "trackingId and originalUrl are required", null));
        }
        
        String shortUrl = trackingService.createShortLink(trackingId, originalUrl);
        
        Map<String, String> response = new HashMap<>();
        response.put("originalUrl", originalUrl);
        response.put("shortUrl", shortUrl);
        response.put("trackingId", trackingId);
        
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Short link created", response));
    }
}