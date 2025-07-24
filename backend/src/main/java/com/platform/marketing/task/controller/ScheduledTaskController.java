package com.platform.marketing.task.controller;

import com.platform.marketing.task.dto.ScheduledTaskDto;
import com.platform.marketing.task.entity.ScheduledTask;
import com.platform.marketing.task.service.ScheduledTaskService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedule-task")
public class ScheduledTaskController {

    private final ScheduledTaskService service;

    public ScheduledTaskController(ScheduledTaskService service) {
        this.service = service;
    }

    @GetMapping("/list")
    @PreAuthorize("hasPermission('task:list')")
    public ResponseEntity<List<ScheduledTaskDto>> list(@RequestParam(defaultValue = "") String keyword,
                                                      @RequestParam(defaultValue = "") String status,
                                                      @RequestParam(required = false) List<String> tags,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        List<ScheduledTask> tasks = service.search(keyword, status, tags, PageRequest.of(page, size));
        List<ScheduledTaskDto> dtos = tasks.stream().map(ScheduledTaskDto::fromEntity).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.success(dtos);
    }

    @PostMapping("/create")
    @PreAuthorize("hasPermission('task:create')")
    public ResponseEntity<Void> create(@RequestBody ScheduledTaskDto dto) {
        service.create(dto);
        return ResponseEntity.success(null);
    }

    @PutMapping("/update")
    @PreAuthorize("hasPermission('task:update')")
    public ResponseEntity<Void> update(@RequestBody ScheduledTaskDto dto) {
        service.update(dto);
        return ResponseEntity.success(null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('task:view')")
    public ResponseEntity<ScheduledTaskDto> get(@PathVariable String id) {
        Optional<ScheduledTask> task = service.findById(id);
        return task.map(t -> ResponseEntity.success(ScheduledTaskDto.fromEntity(t)))
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('task:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.success(null);
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasPermission('task:toggle')")
    public ResponseEntity<Void> toggle(@PathVariable String id) {
        service.toggle(id);
        return ResponseEntity.success(null);
    }
}
