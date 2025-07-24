package com.platform.marketing.dashboard.controller;

import com.platform.marketing.dashboard.dto.DashboardMetricsDto;
import com.platform.marketing.dashboard.dto.RecentTaskDto;
import com.platform.marketing.dashboard.dto.TrendPoint;
import com.platform.marketing.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasPermission('dashboard:stats')")
    public ResponseEntity<DashboardMetricsDto> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/trend/send")
    @PreAuthorize("hasPermission('dashboard:trend')")
    public ResponseEntity<List<TrendPoint>> getSendTrend() {
        return ResponseEntity.ok(dashboardService.getSendTrend());
    }

    @GetMapping("/trend/customers")
    @PreAuthorize("hasPermission('dashboard:trend')")
    public ResponseEntity<List<TrendPoint>> getCustomerTrend() {
        return ResponseEntity.ok(dashboardService.getCustomerTrend());
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasPermission('dashboard:tasks')")
    public ResponseEntity<List<RecentTaskDto>> getTasks() {
        return ResponseEntity.ok(dashboardService.getRecentTasks());
    }

    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasPermission('dashboard:task-view')")
    public ResponseEntity<RecentTaskDto> getTask(@PathVariable String id) {
        return dashboardService.getTask(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
