package com.platform.marketing.modules.ai.dto;

import java.util.List;

public class BatchProcessResultDto {
    private String batchId;
    private String status;
    private List<String> results;
    private String[] errors;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }
}