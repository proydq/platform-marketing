package com.platform.marketing.modules.campaign.controller;

import com.platform.marketing.modules.campaign.entity.MarketingCampaign;
import com.platform.marketing.modules.campaign.dto.MarketingCampaignDto;
import com.platform.marketing.modules.campaign.dto.UpdateCampaignStatusDto;
import com.platform.marketing.modules.campaign.service.MarketingCampaignService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/marketing-campaign")
public class MarketingCampaignController {

    private final MarketingCampaignService marketingCampaignService;

    public MarketingCampaignController(MarketingCampaignService marketingCampaignService) {
        this.marketingCampaignService = marketingCampaignService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('campaign:list')")
    public ResponseEntity<ResponsePageDataEntity<MarketingCampaign>> list(@RequestParam(defaultValue = "") String status,
                                                                         @RequestParam(defaultValue = "") String channel,
                                                                         @RequestParam(defaultValue = "") String keyword,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        Page<MarketingCampaign> p = marketingCampaignService.search(status, channel, keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('campaign:detail')")
    public ResponseEntity<MarketingCampaign> get(@PathVariable String id) {
        return marketingCampaignService.findById(id)
                .map(ResponseEntity::success)
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }

    @PostMapping
    @PreAuthorize("hasPermission('campaign:create')")
    public ResponseEntity<MarketingCampaign> create(@RequestBody MarketingCampaignDto campaign) {
        if (campaign.getChannels() == null) {
            campaign.setChannels(new ArrayList<>());
        }
        if (campaign.getStatus() == null || campaign.getStatus().trim().isEmpty()) {
            campaign.setStatus("pending");
        }
        return ResponseEntity.success(marketingCampaignService.create(campaign));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('campaign:update')")
    public ResponseEntity<MarketingCampaign> update(@PathVariable String id, @RequestBody MarketingCampaignDto campaign) {
        if (campaign.getChannels() == null) {
            campaign.setChannels(new ArrayList<>());
        }
        if (campaign.getStatus() == null || campaign.getStatus().trim().isEmpty()) {
            campaign.setStatus("pending");
        }
        return ResponseEntity.success(marketingCampaignService.create(campaign));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('campaign:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        marketingCampaignService.delete(id);
        return ResponseEntity.<Void>success(null);
    }

    @PatchMapping("/status")
    @PreAuthorize("hasPermission('campaign:status:update')")
    public ResponseEntity<Void> updateStatus(@RequestBody UpdateCampaignStatusDto dto) {
        if (dto.getId() == null || dto.getStatus() == null) {
            return ResponseEntity.<Void>fail(400, "id and status required");
        }
        marketingCampaignService.updateStatus(dto.getId(), dto.getStatus());
        return ResponseEntity.<Void>success(null);
    }

    @PostMapping("/publish")
    @PreAuthorize("hasPermission('campaign:publish')")
    public ResponseEntity<Void> publish(@RequestBody java.util.Map<String, String> body) {
        String id = body.get("id");
        if (id == null) {
            return ResponseEntity.<Void>fail(400, "id required");
        }
        marketingCampaignService.updateStatus(id, "running");
        return ResponseEntity.<Void>success(null);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasPermission('campaign:toggle')")
    public ResponseEntity<Void> toggle(@RequestBody java.util.Map<String, String> body) {
        String id = body.get("id");
        if (id == null) {
            return ResponseEntity.<Void>fail(400, "id required");
        }
        return marketingCampaignService.findById(id)
                .map(campaign -> {
                    String current = campaign.getStatus();
                    String status = "paused".equals(current) ? "running" : "paused";
                    marketingCampaignService.updateStatus(id, status);
                    return ResponseEntity.<Void>success(null);
                })
                .orElse(ResponseEntity.<Void>fail(404, "Not Found"));
    }

    @PostMapping("/publish")
    @PreAuthorize("hasPermission('campaign:publish')")
    public ResponseEntity<Void> publish(@RequestBody java.util.Map<String, String> body) {
        String id = body.get("id");
        if (id == null) {
            return ResponseEntity.fail(400, "id required");
        }
        marketingCampaignService.updateStatus(id, "running");
        return ResponseEntity.success(null);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasPermission('campaign:toggle')")
    public ResponseEntity<Void> toggle(@RequestBody java.util.Map<String, String> body) {
        String id = body.get("id");
        if (id == null) {
            return ResponseEntity.fail(400, "id required");
        }
        return marketingCampaignService.findById(id)
                .map(campaign -> {
                    String current = campaign.getStatus();
                    String status = "paused".equals(current) ? "running" : "paused";
                    marketingCampaignService.updateStatus(id, status);
                    return ResponseEntity.success(null);
                })
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }
}
