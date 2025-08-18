package com.platform.marketing.controller;

import com.platform.marketing.service.TaskMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@CrossOrigin(origins = "*")
public class MonitorController {

    @Autowired
    private TaskMonitorService taskMonitorService;

    /**
     * 手动触发任务状态更新
     */
    @PostMapping("/task/{taskId}/update")
    public ResponseEntity<String> updateTaskStatus(
            @PathVariable String taskId,
            @RequestParam String taskName,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int progress,
            @RequestParam(defaultValue = "") String message) {
        
        taskMonitorService.triggerTaskUpdate(taskId, taskName, status, progress, message);
        return ResponseEntity.ok("任务状态更新已推送");
    }

    /**
     * 手动触发营销活动状态更新
     */
    @PostMapping("/campaign/{campaignId}/update")
    public ResponseEntity<String> updateCampaignStatus(
            @PathVariable String campaignId,
            @RequestParam String campaignName,
            @RequestParam String status,
            @RequestParam String channel,
            @RequestParam(defaultValue = "") String message) {
        
        taskMonitorService.triggerCampaignUpdate(campaignId, campaignName, status, channel, message);
        return ResponseEntity.ok("营销活动状态更新已推送");
    }

    /**
     * 模拟邮件发送任务
     */
    @PostMapping("/simulate/email-campaign")
    public ResponseEntity<String> simulateEmailCampaign() {
        // 模拟邮件营销活动的执行过程
        new Thread(() -> {
            try {
                taskMonitorService.triggerTaskUpdate("email_task_" + System.currentTimeMillis(), 
                    "邮件群发任务", "running", 0, "开始发送邮件...");
                
                Thread.sleep(2000);
                taskMonitorService.triggerTaskUpdate("email_task_" + System.currentTimeMillis(), 
                    "邮件群发任务", "running", 25, "已发送 25% 的邮件");
                
                Thread.sleep(2000);
                taskMonitorService.triggerTaskUpdate("email_task_" + System.currentTimeMillis(), 
                    "邮件群发任务", "running", 50, "已发送 50% 的邮件");
                
                Thread.sleep(2000);
                taskMonitorService.triggerTaskUpdate("email_task_" + System.currentTimeMillis(), 
                    "邮件群发任务", "running", 75, "已发送 75% 的邮件");
                
                Thread.sleep(2000);
                taskMonitorService.triggerTaskUpdate("email_task_" + System.currentTimeMillis(), 
                    "邮件群发任务", "completed", 100, "邮件发送完成，共发送1000封邮件");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return ResponseEntity.ok("邮件发送任务模拟已启动");
    }

    /**
     * 模拟社交媒体发布任务
     */
    @PostMapping("/simulate/social-media")
    public ResponseEntity<String> simulateSocialMedia() {
        new Thread(() -> {
            try {
                taskMonitorService.triggerCampaignUpdate("social_campaign_" + System.currentTimeMillis(), 
                    "产品推广活动", "running", "社交媒体", "开始发布到多个平台...");
                
                Thread.sleep(3000);
                taskMonitorService.triggerCampaignUpdate("social_campaign_" + System.currentTimeMillis(), 
                    "产品推广活动", "running", "社交媒体", "已发布到 Facebook");
                
                Thread.sleep(3000);
                taskMonitorService.triggerCampaignUpdate("social_campaign_" + System.currentTimeMillis(), 
                    "产品推广活动", "running", "社交媒体", "已发布到 Twitter");
                
                Thread.sleep(3000);
                taskMonitorService.triggerCampaignUpdate("social_campaign_" + System.currentTimeMillis(), 
                    "产品推广活动", "completed", "社交媒体", "所有平台发布完成");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return ResponseEntity.ok("社交媒体发布任务模拟已启动");
    }
}