package com.platform.marketing.modules.notification.service.impl;

import com.platform.marketing.modules.notification.entity.Notification;
import com.platform.marketing.modules.notification.repository.NotificationRepository;
import com.platform.marketing.modules.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Page<Notification> search(String type, String status, String keyword, Pageable pageable) {
        if (type == null) type = "";
        if (status == null) status = "";
        if (keyword == null) keyword = "";
        return notificationRepository.search(type, status, keyword, pageable);
    }

    @Override
    public Optional<Notification> findById(String id) {
        return notificationRepository.findById(id);
    }

    @Override
    @Transactional
    public Notification create(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public Notification update(String id, Notification notification) {
        Notification existing = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        existing.setTitle(notification.getTitle());
        existing.setContent(notification.getContent());
        existing.setType(notification.getType());
        existing.setStatus(notification.getStatus());
        existing.setLink(notification.getLink());
        return notificationRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        notificationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(notificationRepository::deleteById);
    }

    @Override
    @Transactional
    public void markAsRead(String id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setStatus("read");
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void markBatchAsRead(List<String> ids) {
        ids.forEach(this::markAsRead);
    }
}
