package com.platform.marketing.modules.email.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Simple record of a sent email.
 */
public class EmailSendRecord {
    private String subject;
    private String content;
    private List<String> toList;
    private LocalDateTime sentAt;

    public EmailSendRecord() {
    }

    public EmailSendRecord(String subject, String content, List<String> toList, LocalDateTime sentAt) {
        this.subject = subject;
        this.content = content;
        this.toList = toList;
        this.sentAt = sentAt;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getToList() {
        return toList;
    }

    public void setToList(List<String> toList) {
        this.toList = toList;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}

