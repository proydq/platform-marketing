package com.platform.marketing.modules.acquisition.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_acquisition_log")
public class AcquisitionLog {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "task_id", nullable = false, length = 36)
    private String taskId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LogType type;

    @Column(nullable = false, length = 1000)
    private String message;

    @Lob
    private String details;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Join with AcquisitionTask
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private AcquisitionTask task;

    public enum LogType {
        START, PROGRESS, SUCCESS, WARNING, ERROR
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public AcquisitionTask getTask() {
        return task;
    }

    public void setTask(AcquisitionTask task) {
        this.task = task;
    }
}