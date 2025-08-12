package com.platform.marketing.modules.behaviorlog.controller;

import com.platform.marketing.modules.behaviorlog.entity.BehaviorLog;
import com.platform.marketing.modules.behaviorlog.service.BehaviorLogService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/behavior-log")
public class BehaviorLogController {

    private final BehaviorLogService behaviorLogService;

    public BehaviorLogController(BehaviorLogService behaviorLogService) {
        this.behaviorLogService = behaviorLogService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('behavior-log:list')")
    public ResponseEntity<ResponsePageDataEntity<BehaviorLog>> list(@RequestParam(defaultValue = "") String action,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Page<BehaviorLog> p = behaviorLogService.search(action, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<BehaviorLog>(p.getTotalElements(), p.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('behavior-log:view')")
    public ResponseEntity<BehaviorLog> get(@PathVariable String id) {
        return behaviorLogService.findById(id)
                .map(ResponseEntity::success)
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }
}
