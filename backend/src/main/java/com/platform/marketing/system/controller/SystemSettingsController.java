package com.platform.marketing.system.controller;

import com.platform.marketing.system.dto.*;
import com.platform.marketing.system.service.SystemSettingsService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/settings")
public class SystemSettingsController {

    private final SystemSettingsService service;

    public SystemSettingsController(SystemSettingsService service) {
        this.service = service;
    }

    @GetMapping("/")
    @PreAuthorize("hasPermission('settings:view')")
    public ResponseEntity<SystemSettingsDto> getSettings() {
        return ResponseEntity.success(service.getSettings());
    }

    @PostMapping("/basic")
    @PreAuthorize("hasPermission('settings:update')")
    public ResponseEntity<Void> saveBasic(@RequestBody BasicSettingsDto dto) {
        service.saveBasicSettings(dto);
        return ResponseEntity.success(null);
    }

    @PostMapping("/notify")
    @PreAuthorize("hasPermission('settings:update')")
    public ResponseEntity<Void> saveNotify(@RequestBody NotifySettingsDto dto) {
        service.saveNotificationSettings(dto);
        return ResponseEntity.success(null);
    }

    @PostMapping("/security")
    @PreAuthorize("hasPermission('settings:update')")
    public ResponseEntity<Void> saveSecurity(@RequestBody SecuritySettingsDto dto) {
        service.saveSecuritySettings(dto);
        return ResponseEntity.success(null);
    }
}
