package com.platform.marketing.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardMetricsDto {
    private int totalCustomers;
    private int emailsSentToday;
    private BigDecimal openRate;
    private int runningTasks;
}
