package com.platform.marketing.modules.acquisition.service;

import com.platform.marketing.modules.acquisition.dto.AcquisitionTaskDto;
import com.platform.marketing.modules.acquisition.entity.AcquisitionTask;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface AcquisitionTaskService {

    /**
     * 创建快速获取任务
     */
    AcquisitionTaskDto createQuickTask(AcquisitionTaskDto taskDto, String currentUserId);

    /**
     * 创建获取任务
     */
    AcquisitionTaskDto createTask(AcquisitionTaskDto taskDto, String currentUserId);

    /**
     * 更新任务
     */
    AcquisitionTaskDto updateTask(String taskId, AcquisitionTaskDto taskDto, String currentUserId);

    /**
     * 删除任务
     */
    void deleteTask(String taskId, String currentUserId);

    /**
     * 根据ID获取任务
     */
    AcquisitionTaskDto getTaskById(String taskId, String currentUserId);

    /**
     * 分页获取当前用户的任务列表
     */
    Page<AcquisitionTaskDto> getTasksByUser(String currentUserId, int page, int size);

    /**
     * 启动任务
     */
    void startTask(String taskId, String currentUserId);

    /**
     * 停止任务
     */
    void stopTask(String taskId, String currentUserId);

    /**
     * 暂停任务
     */
    void pauseTask(String taskId, String currentUserId);

    /**
     * 恢复任务
     */
    void resumeTask(String taskId, String currentUserId);

    /**
     * 获取任务统计信息
     */
    Map<String, Object> getTaskStatistics(String currentUserId);

    /**
     * 获取任务执行历史
     */
    List<Map<String, Object>> getTaskHistory(String taskId, String currentUserId);

    /**
     * 导出任务数据
     */
    byte[] exportTaskData(String taskId, String currentUserId);

    /**
     * 获取预览数据
     */
    List<Map<String, Object>> getPreviewData(String currentUserId);

    /**
     * 更新任务进度
     */
    void updateTaskProgress(String taskId, double progress, int collectedCount);

    /**
     * 标记任务完成
     */
    void completeTask(String taskId);

    /**
     * 标记任务失败
     */
    void failTask(String taskId, String errorMessage);
}