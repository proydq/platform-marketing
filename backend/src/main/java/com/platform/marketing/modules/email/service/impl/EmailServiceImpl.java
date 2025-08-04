package com.platform.marketing.modules.email.service.impl;

import com.platform.marketing.modules.email.entity.EmailSendRecord;
import com.platform.marketing.modules.email.service.EmailService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final List<EmailSendRecord> records = new ArrayList<>();

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String subject, String content, List<String> toList) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setTo(toList.toArray(new String[0]));
            mailSender.send(message);
            EmailSendRecord record = new EmailSendRecord(subject, content, new ArrayList<>(toList), LocalDateTime.now());
            records.add(record);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public List<String> parseRecipients(MultipartFile file) {
        List<String> emails = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : records) {
                if (record.size() > 0) {
                    String email = record.get(0).trim();
                    if (!email.isEmpty()) {
                        emails.add(email);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV", e);
        }
        return emails;
    }

    @Override
    public Page<EmailSendRecord> getRecords(int page, int size) {
        int start = Math.max(0, (page - 1) * size);
        int end = Math.min(start + size, records.size());
        List<EmailSendRecord> sub = records.subList(start, end);
        return new PageImpl<>(new ArrayList<>(sub), PageRequest.of(page - 1, size), records.size());
    }
}
