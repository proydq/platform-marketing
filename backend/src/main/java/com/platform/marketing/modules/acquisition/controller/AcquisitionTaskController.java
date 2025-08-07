package com.platform.marketing.modules.acquisition.controller;

import com.platform.marketing.modules.acquisition.dto.AcquisitionTaskDto;
import com.platform.marketing.modules.acquisition.service.AcquisitionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/acquisition/tasks")
@CrossOrigin(origins = "*")
public class AcquisitionTaskController {

    @Autowired
    private AcquisitionTaskService acquisitionTaskService;

    /**
     * 创建快速获取任务
     */
    @PostMapping("/quick")
    public ResponseEntity<Map<String, Object>> createQuickTask(
            @RequestBody AcquisitionTaskDto taskDto,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        AcquisitionTaskDto result = acquisitionTaskService.createQuickTask(taskDto, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "快速任务创建成功");
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 创建获取任务
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTask(
            @RequestBody AcquisitionTaskDto taskDto,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        AcquisitionTaskDto result = acquisitionTaskService.createTask(taskDto, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务创建成功");
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 更新任务
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> updateTask(
            @PathVariable String taskId,
            @RequestBody AcquisitionTaskDto taskDto,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        AcquisitionTaskDto result = acquisitionTaskService.updateTask(taskId, taskDto, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务更新成功");
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteTask(
            @PathVariable String taskId,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        acquisitionTaskService.deleteTask(taskId, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务删除成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> getTask(
            @PathVariable String taskId,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        AcquisitionTaskDto result = acquisitionTaskService.getTaskById(taskId, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 分页获取任务列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        Page<AcquisitionTaskDto> taskPage = acquisitionTaskService.getTasksByUser(currentUserId, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", taskPage.getContent());
        response.put("total", taskPage.getTotalElements());
        response.put("page", taskPage.getNumber());
        response.put("size", taskPage.getSize());
        response.put("totalPages", taskPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 启动任务
     */
    @PostMapping("/{taskId}/start")
    public ResponseEntity<Map<String, Object>> startTask(
            @PathVariable String taskId,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        acquisitionTaskService.startTask(taskId, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务启动成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 停止任务
     */
    @PostMapping("/{taskId}/stop")
    public ResponseEntity<Map<String, Object>> stopTask(
            @PathVariable String taskId,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        acquisitionTaskService.stopTask(taskId, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务停止成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 暂停任务
     */
    @PostMapping("/{taskId}/pause")
    public ResponseEntity<Map<String, Object>> pauseTask(
            @PathVariable String taskId,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        acquisitionTaskService.pauseTask(taskId, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务暂停成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 恢复任务
     */
    @PostMapping("/{taskId}/resume")
    public ResponseEntity<Map<String, Object>> resumeTask(
            @PathVariable String taskId,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        acquisitionTaskService.resumeTask(taskId, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务恢复成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取任务统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(Authentication authentication) {
        String currentUserId = authentication.getName();
        Map<String, Object> statistics = acquisitionTaskService.getTaskStatistics(currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", statistics);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取任务执行历史
     */
    @GetMapping("/{taskId}/history")
    public ResponseEntity<Map<String, Object>> getTaskHistory(
            @PathVariable String taskId,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        List<Map<String, Object>> history = acquisitionTaskService.getTaskHistory(taskId, currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", history);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 导出任务数据
     */
    @GetMapping("/{taskId}/export")
    public ResponseEntity<byte[]> exportTaskData(
            @PathVariable String taskId,
            Authentication authentication) {
        
        String currentUserId = authentication.getName();
        byte[] data = acquisitionTaskService.exportTaskData(taskId, currentUserId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "task_" + taskId + "_data.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    /**
     * 获取预览数据
     */
    @GetMapping("/preview")
    public ResponseEntity<Map<String, Object>> getPreviewData(Authentication authentication) {
        String currentUserId = authentication.getName();
        List<Map<String, Object>> previewData = acquisitionTaskService.getPreviewData(currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", previewData);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 异常处理
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleException(RuntimeException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", e.getMessage());
        
        return ResponseEntity.status(500).body(response);
    }
}