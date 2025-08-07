package com.platform.marketing.modules.acquisition.repository;

import com.platform.marketing.modules.acquisition.entity.AcquisitionTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AcquisitionTaskRepository extends JpaRepository<AcquisitionTask, String> {

    /**
     * 根据创建人分页查询任务
     */
    Page<AcquisitionTask> findByCreatedByOrderByCreatedAtDesc(String createdBy, Pageable pageable);

    /**
     * 根据状态查询任务
     */
    List<AcquisitionTask> findByStatus(AcquisitionTask.TaskStatus status);

    /**
     * 根据状态和创建人查询任务
     */
    List<AcquisitionTask> findByStatusAndCreatedBy(AcquisitionTask.TaskStatus status, String createdBy);

    /**
     * 查询需要执行的定时任务
     */
    @Query("SELECT t FROM AcquisitionTask t WHERE t.frequency != 'ONCE' AND t.nextRunTime <= :now AND t.status = 'PENDING'")
    List<AcquisitionTask> findScheduledTasks(@Param("now") LocalDateTime now);

    /**
     * 统计各状态的任务数量
     */
    @Query("SELECT t.status, COUNT(t) FROM AcquisitionTask t WHERE t.createdBy = :createdBy GROUP BY t.status")
    List<Object[]> countTasksByStatus(@Param("createdBy") String createdBy);

    /**
     * 统计总任务数
     */
    long countByCreatedBy(String createdBy);

    /**
     * 统计正在运行的任务数
     */
    long countByStatusAndCreatedBy(AcquisitionTask.TaskStatus status, String createdBy);
}