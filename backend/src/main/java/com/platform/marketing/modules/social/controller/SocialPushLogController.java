package com.platform.marketing.modules.social.controller;

import com.platform.marketing.modules.social.entity.SocialPushLog;
import com.platform.marketing.modules.social.service.SocialPushLogService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/social-log")
public class SocialPushLogController {

    private final SocialPushLogService socialPushLogService;

    public SocialPushLogController(SocialPushLogService socialPushLogService) {
        this.socialPushLogService = socialPushLogService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('social-account:logs')")
    public ResponseEntity<ResponsePageDataEntity<SocialPushLog>> list(@RequestParam(defaultValue = "") String accountId,
                                                                     @RequestParam(defaultValue = "") String status,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Page<SocialPushLog> p = socialPushLogService.search(accountId, status, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }
}
