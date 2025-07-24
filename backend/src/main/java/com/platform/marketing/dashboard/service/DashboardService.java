package com.platform.marketing.dashboard.service;

import com.platform.marketing.dashboard.dto.DashboardMetricsDto;
import com.platform.marketing.dashboard.dto.RecentTaskDto;
import com.platform.marketing.dashboard.dto.TrendPoint;

import java.util.List;
import java.util.Optional;

public interface DashboardService {
    DashboardMetricsDto getDashboardStats();

    List<TrendPoint> getSendTrend();

    List<TrendPoint> getCustomerTrend();

    List<RecentTaskDto> getRecentTasks();

    Optional<RecentTaskDto> getTask(String id);
}
