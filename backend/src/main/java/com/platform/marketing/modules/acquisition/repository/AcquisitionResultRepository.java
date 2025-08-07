package com.platform.marketing.modules.acquisition.repository;

import com.platform.marketing.modules.acquisition.entity.AcquisitionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcquisitionResultRepository extends JpaRepository<AcquisitionResult, String> {

    /**
     * 根据任务ID分页查询结果
     */
    Page<AcquisitionResult> findByTaskIdOrderByCreatedAtDesc(String taskId, Pageable pageable);

    /**
     * 根据任务ID和状态查询结果
     */
    List<AcquisitionResult> findByTaskIdAndStatus(String taskId, AcquisitionResult.ResultStatus status);

    /**
     * 统计任务的结果数量
     */
    long countByTaskId(String taskId);

    /**
     * 统计任务的有效结果数量
     */
    long countByTaskIdAndStatus(String taskId, AcquisitionResult.ResultStatus status);

    /**
     * 根据邮箱和任务ID检查是否存在重复
     */
    boolean existsByTaskIdAndEmail(String taskId, String email);

    /**
     * 根据电话和任务ID检查是否存在重复
     */
    boolean existsByTaskIdAndPhone(String taskId, String phone);

    /**
     * 统计各平台的结果数量
     */
    @Query("SELECT r.platform, COUNT(r) FROM AcquisitionResult r WHERE r.taskId = :taskId GROUP BY r.platform")
    List<Object[]> countResultsByPlatform(@Param("taskId") String taskId);

    /**
     * 根据任务ID删除所有结果
     */
    void deleteByTaskId(String taskId);
}