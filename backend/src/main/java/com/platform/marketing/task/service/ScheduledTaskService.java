package com.platform.marketing.task.service;

import com.platform.marketing.task.dto.ScheduledTaskDto;
import com.platform.marketing.task.entity.ScheduledTask;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ScheduledTaskService {
    List<ScheduledTask> search(String keyword, String status, List<String> tags, Pageable pageable);

    ScheduledTask create(ScheduledTaskDto taskDto);

    ScheduledTask update(ScheduledTaskDto taskDto);

    Optional<ScheduledTask> findById(String id);

    void delete(String id);

    void toggle(String id);
}
