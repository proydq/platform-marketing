package com.platform.marketing.dashboard.service.impl;

import com.platform.marketing.dashboard.dto.DashboardMetricsDto;
import com.platform.marketing.dashboard.dto.RecentTaskDto;
import com.platform.marketing.dashboard.dto.TrendPoint;
import com.platform.marketing.dashboard.entity.DashboardMetrics;
import com.platform.marketing.dashboard.entity.RecentTask;
import com.platform.marketing.dashboard.repository.DashboardMetricsRepository;
import com.platform.marketing.dashboard.repository.RecentTaskRepository;
import com.platform.marketing.dashboard.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMetricsRepository metricsRepository;
    private final RecentTaskRepository recentTaskRepository;

    public DashboardServiceImpl(DashboardMetricsRepository metricsRepository, RecentTaskRepository recentTaskRepository) {
        this.metricsRepository = metricsRepository;
        this.recentTaskRepository = recentTaskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardMetricsDto getDashboardStats() {
        DashboardMetrics metrics = metricsRepository.findTopByOrderByCreatedAtDesc();
        if (metrics == null) {
            return new DashboardMetricsDto();
        }
        DashboardMetricsDto dto = new DashboardMetricsDto();
        dto.setTotalCustomers(metrics.getTotalCustomers());
        dto.setEmailsSentToday(metrics.getEmailsSentToday());
        dto.setOpenRate(metrics.getOpenRate());
        dto.setRunningTasks(metrics.getRunningTasks());
        return dto;
    }

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(readOnly = true)
    public List<TrendPoint> getSendTrend() {
        List<DashboardMetrics> metrics = metricsRepository.findTop30ByOrderByCreatedAtAsc();
        return metrics.stream()
                .map(m -> new TrendPoint(m.getCreatedAt().format(DF), m.getEmailsSentToday()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrendPoint> getCustomerTrend() {
        List<DashboardMetrics> metrics = metricsRepository.findTop30ByOrderByCreatedAtAsc();
        return metrics.stream()
                .map(m -> new TrendPoint(m.getCreatedAt().format(DF), m.getTotalCustomers()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecentTaskDto> getRecentTasks() {
        return recentTaskRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RecentTaskDto> getTask(String id) {
        return recentTaskRepository.findById(id).map(this::convert);
    }

    private RecentTaskDto convert(RecentTask task) {
        RecentTaskDto dto = new RecentTaskDto();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setStatus(task.getStatus());
        dto.setProgress(task.getProgress());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }
}
