package com.platform.marketing.modules.acquisition.repository;

import com.platform.marketing.modules.acquisition.entity.AcquisitionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcquisitionLogRepository extends JpaRepository<AcquisitionLog, String> {

    /**
     * 根据任务ID分页查询日志
     */
    Page<AcquisitionLog> findByTaskIdOrderByCreatedAtDesc(String taskId, Pageable pageable);

    /**
     * 根据任务ID查询最近的日志
     */
    List<AcquisitionLog> findTop10ByTaskIdOrderByCreatedAtDesc(String taskId);

    /**
     * 根据任务ID和日志类型查询
     */
    List<AcquisitionLog> findByTaskIdAndType(String taskId, AcquisitionLog.LogType type);

    /**
     * 根据任务ID删除所有日志
     */
    void deleteByTaskId(String taskId);
}