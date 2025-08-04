package com.platform.marketing.modules.email.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmailService {
    void sendEmail(String subject, String content, List<String> toList);

    List<String> parseRecipients(MultipartFile file);
}
