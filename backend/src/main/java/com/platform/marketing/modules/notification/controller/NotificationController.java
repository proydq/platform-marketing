package com.platform.marketing.modules.notification.controller;

import com.platform.marketing.modules.notification.entity.Notification;
import com.platform.marketing.modules.notification.service.NotificationService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('notification:list')")
    public ResponseEntity<ResponsePageDataEntity<Notification>> list(@RequestParam(defaultValue = "") String type,
                                                                    @RequestParam(defaultValue = "") String status,
                                                                    @RequestParam(defaultValue = "") String keyword,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Page<Notification> p = notificationService.search(type, status, keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('notification:view')")
    public ResponseEntity<Notification> get(@PathVariable String id) {
        return notificationService.findById(id)
                .map(ResponseEntity::success)
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }

    @PostMapping
    @PreAuthorize("hasPermission('notification:create')")
    public ResponseEntity<Notification> create(@RequestBody Notification notification) {
        return ResponseEntity.success(notificationService.create(notification));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasPermission('notification:mark-read')")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.success(null);
    }

    @PatchMapping("/batch-read")
    @PreAuthorize("hasPermission('notification:batch-read')")
    public ResponseEntity<Void> markBatchAsRead(@RequestBody List<String> ids) {
        notificationService.markBatchAsRead(ids);
        return ResponseEntity.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('notification:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationService.delete(id);
        return ResponseEntity.success(null);
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasPermission('notification:batch-delete')")
    public ResponseEntity<Void> deleteBatch(@RequestBody List<String> ids) {
        notificationService.deleteBatch(ids);
        return ResponseEntity.success(null);
    }
}
