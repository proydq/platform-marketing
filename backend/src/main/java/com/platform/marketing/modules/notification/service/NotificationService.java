package com.platform.marketing.modules.notification.service;

import com.platform.marketing.modules.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Page<Notification> search(String type, String status, String keyword, Pageable pageable);
    Optional<Notification> findById(String id);
    Notification create(Notification notification);
    Notification update(String id, Notification notification);
    void delete(String id);
    void deleteBatch(List<String> ids);
    void markAsRead(String id);
    void markBatchAsRead(List<String> ids);
}
