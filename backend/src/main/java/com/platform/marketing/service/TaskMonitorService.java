package com.platform.marketing.service;

import com.platform.marketing.websocket.TaskMonitorWebSocketHandler;
import com.platform.marketing.modules.campaign.repository.MarketingCampaignRepository;
import com.platform.marketing.dashboard.repository.RecentTaskRepository;
import com.platform.marketing.dashboard.repository.DashboardMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class TaskMonitorService {

    @Autowired
    private TaskMonitorWebSocketHandler webSocketHandler;
    
    @Autowired
    private MarketingCampaignRepository campaignRepository;
    
    @Autowired
    private RecentTaskRepository taskRepository;
    
    @Autowired
    private DashboardMetricsRepository metricsRepository;

    /**
     * 定时推送任务状态更新 (每30秒)
     */
    @Scheduled(fixedRate = 30000)
    public void pushTaskUpdates() {
        try {
            // 获取最新的任务统计数据
            Map<String, Object> stats = getCurrentStats();
            webSocketHandler.broadcastStatsUpdate(stats);
            
            // 模拟推送一些任务更新
            simulateTaskUpdates();
            
        } catch (Exception e) {
            System.err.println("推送任务更新失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发任务状态更新
     */
    public void triggerTaskUpdate(String taskId, String taskName, String status, int progress, String message) {
        webSocketHandler.broadcastTaskUpdate(taskId, taskName, status, progress, message);
    }

    /**
     * 手动触发营销活动状态更新
     */
    public void triggerCampaignUpdate(String campaignId, String campaignName, String status, String channel, String message) {
        webSocketHandler.broadcastCampaignUpdate(campaignId, campaignName, status, channel, message);
    }

    /**
     * 获取当前统计数据
     */
    private Map<String, Object> getCurrentStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 获取营销活动统计
            long totalCampaigns = campaignRepository.count();
            long runningCampaigns = campaignRepository.countByStatus("running");
            long pausedCampaigns = campaignRepository.countByStatus("paused");
            long completedCampaigns = campaignRepository.countByStatus("completed");
            
            // 获取任务统计
            long totalTasks = taskRepository.count();
            long runningTasks = taskRepository.countByStatus("running");
            long pendingTasks = taskRepository.countByStatus("pending");
            long completedTasks = taskRepository.countByStatus("completed");
            
            stats.put("campaigns", Map.of(
                "total", totalCampaigns,
                "running", runningCampaigns,
                "paused", pausedCampaigns,
                "completed", completedCampaigns
            ));
            
            stats.put("tasks", Map.of(
                "total", totalTasks,
                "running", runningTasks,
                "pending", pendingTasks,
                "completed", completedTasks
            ));
            
            stats.put("activeConnections", webSocketHandler.getActiveConnectionCount());
            
        } catch (Exception e) {
            System.err.println("获取统计数据失败: " + e.getMessage());
        }
        
        return stats;
    }

    /**
     * 模拟任务更新（用于演示）
     */
    private void simulateTaskUpdates() {
        // 这里可以模拟一些任务状态变化
        String[] taskNames = {"邮件群发任务", "客户数据同步", "报告生成", "社交媒体发布"};
        String[] statuses = {"running", "completed", "pending", "failed"};
        
        for (int i = 0; i < 2; i++) {
            String taskName = taskNames[(int)(Math.random() * taskNames.length)];
            String status = statuses[(int)(Math.random() * statuses.length)];
            int progress = status.equals("completed") ? 100 : (int)(Math.random() * 100);
            
            String message = getStatusMessage(status, progress);
            
            webSocketHandler.broadcastTaskUpdate(
                "task_" + System.currentTimeMillis() + "_" + i,
                taskName,
                status,
                progress,
                message
            );
            
            try {
                Thread.sleep(1000); // 间隔1秒发送
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private String getStatusMessage(String status, int progress) {
        switch (status) {
            case "running":
                return "任务正在执行中，进度 " + progress + "%";
            case "completed":
                return "任务执行完成";
            case "pending":
                return "任务等待执行";
            case "failed":
                return "任务执行失败，请检查配置";
            default:
                return "任务状态未知";
        }
    }
}