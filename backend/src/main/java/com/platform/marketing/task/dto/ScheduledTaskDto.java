package com.platform.marketing.task.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.marketing.task.entity.ScheduledTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduledTaskDto {
    private static final ObjectMapper mapper = new ObjectMapper();

    private String id;
    private String name;
    private String description;
    private String cycle;
    private LocalDateTime startTime;
    private boolean enabled;
    private String status;
    private List<String> actions = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private LocalDateTime lastRun;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }

    public static ScheduledTaskDto fromEntity(ScheduledTask entity) {
        ScheduledTaskDto dto = new ScheduledTaskDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCycle(entity.getCycle());
        dto.setStartTime(entity.getStartTime());
        dto.setEnabled(entity.isEnabled());
        dto.setStatus(entity.getStatus());
        String actionsStr = entity.getActions();
        if (actionsStr != null && !actionsStr.trim().isEmpty()) {
            try {
                dto.setActions(mapper.readValue(actionsStr, new TypeReference<List<String>>(){}));
            } catch (Exception e) {
                dto.setActions(Arrays.asList(actionsStr.split("\n")));
            }
        }
        String tagsStr = entity.getTags();
        if (tagsStr != null && !tagsStr.trim().isEmpty()) {
            dto.setTags(Arrays.asList(tagsStr.split(",")));
        }
        dto.setLastRun(entity.getLastRun());
        return dto;
    }

    public static void copyToEntity(ScheduledTaskDto dto, ScheduledTask entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCycle(dto.getCycle());
        entity.setStartTime(dto.getStartTime());
        entity.setEnabled(dto.isEnabled());
        entity.setStatus(dto.getStatus());
        if (dto.getActions() != null && !dto.getActions().isEmpty()) {
            try {
                entity.setActions(mapper.writeValueAsString(dto.getActions()));
            } catch (Exception e) {
                entity.setActions(String.join("\n", dto.getActions()));
            }
        } else {
            entity.setActions("[]");
        }
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            entity.setTags(String.join(",", dto.getTags()));
        } else {
            entity.setTags("");
        }
        entity.setLastRun(dto.getLastRun());
    }
}
