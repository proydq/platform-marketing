package com.platform.marketing.modules.ai.dto;

import java.util.List;

public class BatchProcessRequestDto {
    private List<String> tasks;
    private String batchType;
    private String priority;

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}