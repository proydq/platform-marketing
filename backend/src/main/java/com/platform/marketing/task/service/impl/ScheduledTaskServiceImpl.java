package com.platform.marketing.task.service.impl;

import com.platform.marketing.task.dto.ScheduledTaskDto;
import com.platform.marketing.task.entity.ScheduledTask;
import com.platform.marketing.task.repository.ScheduledTaskRepository;
import com.platform.marketing.task.service.ScheduledTaskService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    private final ScheduledTaskRepository repository;

    public ScheduledTaskServiceImpl(ScheduledTaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ScheduledTask> search(String keyword, String status, List<String> tags, Pageable pageable) {
        Specification<ScheduledTask> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (tags != null && !tags.isEmpty()) {
                for (String tag : tags) {
                    predicates.add(cb.like(root.get("tags"), "%" + tag + "%"));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        if (pageable == null) {
            return repository.findAll(spec);
        }
        return repository.findAll(spec, pageable).getContent();
    }

    @Override
    @Transactional
    public ScheduledTask create(ScheduledTaskDto taskDto) {
        ScheduledTask entity = new ScheduledTask();
        ScheduledTaskDto.copyToEntity(taskDto, entity);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ScheduledTask update(ScheduledTaskDto taskDto) {
        ScheduledTask existing = repository.findById(taskDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        ScheduledTaskDto.copyToEntity(taskDto, existing);
        return repository.save(existing);
    }

    @Override
    public Optional<ScheduledTask> findById(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggle(String id) {
        repository.findById(id).ifPresent(task -> {
            task.setEnabled(!task.isEnabled());
            if (task.isEnabled()) {
                task.setStatus("running");
            } else {
                task.setStatus("paused");
            }
            repository.save(task);
        });
    }
}
