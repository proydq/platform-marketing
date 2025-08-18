package com.platform.marketing.modules.ai.dto;

public class AIAssistantResponseDto {
    private String response;
    private String[] suggestions;
    private String context;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String[] getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String[] suggestions) {
        this.suggestions = suggestions;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}