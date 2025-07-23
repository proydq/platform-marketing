package com.platform.marketing.modules.campaign.controller;

import com.platform.marketing.modules.campaign.entity.MarketingCampaign;
import com.platform.marketing.modules.campaign.service.MarketingCampaignService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marketing-campaign")
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
    public ResponseEntity<MarketingCampaign> create(@RequestBody MarketingCampaign campaign) {
        return ResponseEntity.success(marketingCampaignService.create(campaign));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('campaign:update')")
    public ResponseEntity<MarketingCampaign> update(@PathVariable String id, @RequestBody MarketingCampaign campaign) {
        return ResponseEntity.success(marketingCampaignService.update(id, campaign));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('campaign:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        marketingCampaignService.delete(id);
        return ResponseEntity.success(null);
    }

    @PostMapping("/update-status")
    @PreAuthorize("hasPermission('campaign:status:update')")
    public ResponseEntity<Void> updateStatus(@RequestBody java.util.Map<String, Object> body) {
        String id = (String) body.get("id");
        String status = (String) body.get("status");
        if (id == null || status == null) {
            return ResponseEntity.fail(400, "id and status required");
        }
        marketingCampaignService.updateStatus(id, status);
        return ResponseEntity.success(null);
    }
}
