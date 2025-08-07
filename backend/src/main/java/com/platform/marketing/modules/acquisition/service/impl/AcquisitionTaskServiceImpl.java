package com.platform.marketing.modules.acquisition.service.impl;

import com.platform.marketing.modules.acquisition.dto.AcquisitionTaskDto;
import com.platform.marketing.modules.acquisition.entity.AcquisitionLog;
import com.platform.marketing.modules.acquisition.entity.AcquisitionResult;
import com.platform.marketing.modules.acquisition.entity.AcquisitionTask;
import com.platform.marketing.modules.acquisition.repository.AcquisitionLogRepository;
import com.platform.marketing.modules.acquisition.repository.AcquisitionResultRepository;
import com.platform.marketing.modules.acquisition.repository.AcquisitionTaskRepository;
import com.platform.marketing.modules.acquisition.service.AcquisitionTaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class AcquisitionTaskServiceImpl implements AcquisitionTaskService {

    @Autowired
    private AcquisitionTaskRepository taskRepository;

    @Autowired
    private AcquisitionResultRepository resultRepository;

    @Autowired
    private AcquisitionLogRepository logRepository;

    @Override
    public AcquisitionTaskDto createQuickTask(AcquisitionTaskDto taskDto, String currentUserId) {
        // 生成任务名称
        if (taskDto.getName() == null || taskDto.getName().trim().isEmpty()) {
            taskDto.setName(generateTaskName(taskDto));
        }
        
        return createTask(taskDto, currentUserId);
    }

    @Override
    public AcquisitionTaskDto createTask(AcquisitionTaskDto taskDto, String currentUserId) {
        // 验证必填字段
        validateTaskDto(taskDto);
        
        AcquisitionTask task = new AcquisitionTask();
        convertDtoToEntity(taskDto, task);
        
        task.setId(UUID.randomUUID().toString());
        task.setCreatedBy(currentUserId);
        task.setStatus(AcquisitionTask.TaskStatus.PENDING);
        
        // 计算下次运行时间
        if (task.getFrequency() != AcquisitionTask.TaskFrequency.ONCE) {
            task.setNextRunTime(calculateNextRunTime(task.getFrequency()));
        }
        
        task = taskRepository.save(task);
        
        // 记录创建日志
        createTaskLog(task.getId(), AcquisitionLog.LogType.START, 
                     "任务创建成功", "任务名称: " + task.getName());
        
        return convertEntityToDto(task);
    }

    @Override
    public AcquisitionTaskDto updateTask(String taskId, AcquisitionTaskDto taskDto, String currentUserId) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        if (!task.getCreatedBy().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此任务");
        }
        
        if (task.getStatus() == AcquisitionTask.TaskStatus.RUNNING) {
            throw new RuntimeException("任务正在运行中，无法修改");
        }
        
        convertDtoToEntity(taskDto, task);
        task = taskRepository.save(task);
        
        createTaskLog(taskId, AcquisitionLog.LogType.PROGRESS, 
                     "任务更新", "任务配置已更新");
        
        return convertEntityToDto(task);
    }

    @Override
    public void deleteTask(String taskId, String currentUserId) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        if (!task.getCreatedBy().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此任务");
        }
        
        if (task.getStatus() == AcquisitionTask.TaskStatus.RUNNING) {
            throw new RuntimeException("任务正在运行中，无法删除");
        }
        
        // 删除相关数据
        resultRepository.deleteByTaskId(taskId);
        logRepository.deleteByTaskId(taskId);
        taskRepository.delete(task);
    }

    @Override
    public AcquisitionTaskDto getTaskById(String taskId, String currentUserId) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        if (!task.getCreatedBy().equals(currentUserId)) {
            throw new RuntimeException("无权限查看此任务");
        }
        
        return convertEntityToDto(task);
    }

    @Override
    public Page<AcquisitionTaskDto> getTasksByUser(String currentUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AcquisitionTask> taskPage = taskRepository.findByCreatedByOrderByCreatedAtDesc(currentUserId, pageable);
        
        return taskPage.map(this::convertEntityToDto);
    }

    @Override
    public void startTask(String taskId, String currentUserId) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        if (!task.getCreatedBy().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此任务");
        }
        
        if (task.getStatus() != AcquisitionTask.TaskStatus.PENDING && 
            task.getStatus() != AcquisitionTask.TaskStatus.PAUSED) {
            throw new RuntimeException("任务状态不允许启动");
        }
        
        task.setStatus(AcquisitionTask.TaskStatus.RUNNING);
        task.setStartedAt(LocalDateTime.now());
        taskRepository.save(task);
        
        createTaskLog(taskId, AcquisitionLog.LogType.START, 
                     "任务启动", "任务开始执行");
        
        // TODO: 启动实际的数据采集任务
        // 这里可以发送消息到消息队列或调用异步服务
    }

    @Override
    public void stopTask(String taskId, String currentUserId) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        if (!task.getCreatedBy().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此任务");
        }
        
        if (task.getStatus() != AcquisitionTask.TaskStatus.RUNNING) {
            throw new RuntimeException("任务未在运行中");
        }
        
        task.setStatus(AcquisitionTask.TaskStatus.PAUSED);
        taskRepository.save(task);
        
        createTaskLog(taskId, AcquisitionLog.LogType.WARNING, 
                     "任务停止", "用户手动停止任务");
    }

    @Override
    public void pauseTask(String taskId, String currentUserId) {
        stopTask(taskId, currentUserId);
    }

    @Override
    public void resumeTask(String taskId, String currentUserId) {
        startTask(taskId, currentUserId);
    }

    @Override
    public Map<String, Object> getTaskStatistics(String currentUserId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总任务数
        long totalTasks = taskRepository.countByCreatedBy(currentUserId);
        stats.put("totalTasks", totalTasks);
        
        // 运行中任务数
        long runningTasks = taskRepository.countByStatusAndCreatedBy(
                AcquisitionTask.TaskStatus.RUNNING, currentUserId);
        stats.put("runningTasks", runningTasks);
        
        // 已完成任务数
        long completedTasks = taskRepository.countByStatusAndCreatedBy(
                AcquisitionTask.TaskStatus.COMPLETED, currentUserId);
        stats.put("completedTasks", completedTasks);
        
        // 失败任务数
        long failedTasks = taskRepository.countByStatusAndCreatedBy(
                AcquisitionTask.TaskStatus.FAILED, currentUserId);
        stats.put("failedTasks", failedTasks);
        
        // 各状态统计
        List<Object[]> statusStats = taskRepository.countTasksByStatus(currentUserId);
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusMap.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("statusStats", statusMap);
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getTaskHistory(String taskId, String currentUserId) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        if (!task.getCreatedBy().equals(currentUserId)) {
            throw new RuntimeException("无权限查看此任务");
        }
        
        List<AcquisitionLog> logs = logRepository.findTop10ByTaskIdOrderByCreatedAtDesc(taskId);
        List<Map<String, Object>> history = new ArrayList<>();
        
        for (AcquisitionLog log : logs) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("id", log.getId());
            logMap.put("type", log.getType().name().toLowerCase());
            logMap.put("message", log.getMessage());
            logMap.put("details", log.getDetails());
            logMap.put("createdAt", log.getCreatedAt());
            history.add(logMap);
        }
        
        return history;
    }

    @Override
    public byte[] exportTaskData(String taskId, String currentUserId) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        if (!task.getCreatedBy().equals(currentUserId)) {
            throw new RuntimeException("无权限操作此任务");
        }
        
        // TODO: 实现数据导出逻辑
        // 这里可以导出为Excel、CSV等格式
        List<AcquisitionResult> results = resultRepository.findByTaskIdAndStatus(
                taskId, AcquisitionResult.ResultStatus.VALID);
        
        // 简单实现：返回JSON格式数据
        StringBuilder sb = new StringBuilder();
        sb.append("任务名称,平台,姓名,邮箱,电话,公司,职位,地址\n");
        for (AcquisitionResult result : results) {
            sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    task.getName(),
                    result.getPlatform(),
                    result.getName() != null ? result.getName() : "",
                    result.getEmail() != null ? result.getEmail() : "",
                    result.getPhone() != null ? result.getPhone() : "",
                    result.getCompany() != null ? result.getCompany() : "",
                    result.getTitle() != null ? result.getTitle() : "",
                    result.getLocation() != null ? result.getLocation() : ""));
        }
        
        return sb.toString().getBytes();
    }

    @Override
    public List<Map<String, Object>> getPreviewData(String currentUserId) {
        // 返回模拟预览数据
        List<Map<String, Object>> previewData = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "示例客户 " + (i + 1));
            data.put("email", "customer" + (i + 1) + "@example.com");
            data.put("phone", "138000000" + String.format("%02d", i + 1));
            data.put("company", "示例公司 " + (i + 1));
            data.put("title", "市场经理");
            data.put("location", "北京市");
            data.put("source", i % 2 == 0 ? "LinkedIn" : "Google Maps");
            previewData.add(data);
        }
        
        return previewData;
    }

    @Override
    public void updateTaskProgress(String taskId, double progress, int collectedCount) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        task.setProgress(BigDecimal.valueOf(progress));
        task.setCollectedCount(collectedCount);
        
        // 计算有效数量和成功率
        long validCount = resultRepository.countByTaskIdAndStatus(taskId, AcquisitionResult.ResultStatus.VALID);
        task.setValidCount((int) validCount);
        
        if (collectedCount > 0) {
            BigDecimal successRate = BigDecimal.valueOf(validCount)
                    .divide(BigDecimal.valueOf(collectedCount), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            task.setSuccessRate(successRate);
        }
        
        taskRepository.save(task);
        
        createTaskLog(taskId, AcquisitionLog.LogType.PROGRESS, 
                     "进度更新", String.format("进度: %.1f%%, 已采集: %d, 有效: %d", 
                     progress, collectedCount, validCount));
    }

    @Override
    public void completeTask(String taskId) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        task.setStatus(AcquisitionTask.TaskStatus.COMPLETED);
        task.setProgress(BigDecimal.valueOf(100));
        task.setCompletedAt(LocalDateTime.now());
        
        // 设置下次运行时间（如果是定时任务）
        if (task.getFrequency() != AcquisitionTask.TaskFrequency.ONCE) {
            task.setNextRunTime(calculateNextRunTime(task.getFrequency()));
            task.setStatus(AcquisitionTask.TaskStatus.PENDING);
        }
        
        taskRepository.save(task);
        
        createTaskLog(taskId, AcquisitionLog.LogType.SUCCESS, 
                     "任务完成", "数据采集任务执行完成");
    }

    @Override
    public void failTask(String taskId, String errorMessage) {
        AcquisitionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        task.setStatus(AcquisitionTask.TaskStatus.FAILED);
        task.setErrorMessage(errorMessage);
        taskRepository.save(task);
        
        createTaskLog(taskId, AcquisitionLog.LogType.ERROR, 
                     "任务失败", errorMessage);
    }

    // 私有方法

    private String generateTaskName(AcquisitionTaskDto taskDto) {
        StringBuilder name = new StringBuilder();
        
        if (taskDto.getPlatforms() != null && !taskDto.getPlatforms().isEmpty()) {
            name.append(String.join("+", taskDto.getPlatforms()));
        }
        
        if (taskDto.getType() != null) {
            name.append("_").append(taskDto.getType().name().toLowerCase());
        }
        
        if (taskDto.getAmount() != null) {
            name.append("_").append(taskDto.getAmount());
        }
        
        name.append("_").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMdd_HHmm")));
        
        return name.toString();
    }

    private LocalDateTime calculateNextRunTime(AcquisitionTask.TaskFrequency frequency) {
        LocalDateTime now = LocalDateTime.now();
        switch (frequency) {
            case DAILY:
                return now.plusDays(1);
            case WEEKLY:
                return now.plusWeeks(1);
            case MONTHLY:
                return now.plusMonths(1);
            default:
                return null;
        }
    }

    private void createTaskLog(String taskId, AcquisitionLog.LogType type, String message, String details) {
        AcquisitionLog log = new AcquisitionLog();
        log.setId(UUID.randomUUID().toString());
        log.setTaskId(taskId);
        log.setType(type);
        log.setMessage(message);
        log.setDetails(details);
        logRepository.save(log);
    }

    private void convertDtoToEntity(AcquisitionTaskDto dto, AcquisitionTask entity) {
        BeanUtils.copyProperties(dto, entity, "id", "createdBy", "createdAt", "updatedAt", "platforms", "fields");
        
        // 处理平台列表
        if (dto.getPlatforms() != null) {
            entity.setPlatforms(String.join(",", dto.getPlatforms()));
        }
        
        // 处理字段列表
        if (dto.getFields() != null) {
            entity.setFields(String.join(",", dto.getFields()));
        }
    }

    private AcquisitionTaskDto convertEntityToDto(AcquisitionTask entity) {
        AcquisitionTaskDto dto = new AcquisitionTaskDto();
        BeanUtils.copyProperties(entity, dto, "platforms", "fields");
        
        // 处理平台列表
        if (entity.getPlatforms() != null) {
            dto.setPlatforms(Arrays.asList(entity.getPlatforms().split(",")));
        }
        
        // 处理字段列表
        if (entity.getFields() != null) {
            dto.setFields(Arrays.asList(entity.getFields().split(",")));
        }
        
        return dto;
    }

    private void validateTaskDto(AcquisitionTaskDto dto) {
        if (dto.getName() != null && dto.getName().trim().isEmpty()) {
            dto.setName(null);  // 空字符串设置为null，让系统自动生成
        }
        
        if (dto.getType() == null) {
            throw new RuntimeException("采集类型不能为空");
        }
        
        if (dto.getPlatforms() == null || dto.getPlatforms().isEmpty()) {
            throw new RuntimeException("平台列表不能为空");
        }
        
        if (dto.getKeywords() == null || dto.getKeywords().trim().isEmpty()) {
            throw new RuntimeException("关键词不能为空");
        }
        
        if (dto.getAmount() == null || dto.getAmount() < 1 || dto.getAmount() > 10000) {
            throw new RuntimeException("目标数量必须在1-10000之间");
        }
        
        if (dto.getFields() == null || dto.getFields().isEmpty()) {
            throw new RuntimeException("采集字段不能为空");
        }
        
        if (dto.getQuality() == null || dto.getQuality() < 1 || dto.getQuality() > 5) {
            throw new RuntimeException("质量要求必须在1-5之间");
        }
    }
}