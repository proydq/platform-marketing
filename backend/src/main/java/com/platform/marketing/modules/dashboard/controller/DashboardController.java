package com.platform.marketing.modules.dashboard.controller;

import com.platform.marketing.modules.dashboard.dto.KeywordSearchCountDto;
import com.platform.marketing.modules.dashboard.service.DashboardService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/keyword-search-count")
    @PreAuthorize("hasPermission('dashboard:keyword-search')")
    public ResponseEntity<KeywordSearchCountDto> getKeywordSearchCount(@RequestParam String keyword) {
        return ResponseEntity.success(dashboardService.getKeywordSearchCount(keyword));
    }
}
