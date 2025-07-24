package com.platform.marketing.dashboard.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecentTaskDto {
    private String id;
    private String name;
    private String status;
    private int progress;
    private LocalDateTime createdAt;
}
