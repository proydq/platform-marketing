package com.platform.marketing.modules.email.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.platform.marketing.modules.email.entity.EmailSendRecord;

public interface EmailService {
    void sendEmail(String subject, String content, List<String> toList);

    List<String> parseRecipients(MultipartFile file);

    Page<EmailSendRecord> getRecords(int page, int size);
}
