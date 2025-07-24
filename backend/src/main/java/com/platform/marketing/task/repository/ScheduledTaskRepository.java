package com.platform.marketing.task.repository;

import com.platform.marketing.task.entity.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, String>, JpaSpecificationExecutor<ScheduledTask> {
}
