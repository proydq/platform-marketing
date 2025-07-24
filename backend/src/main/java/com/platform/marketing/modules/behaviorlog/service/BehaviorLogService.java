package com.platform.marketing.modules.behaviorlog.service;

import com.platform.marketing.modules.behaviorlog.entity.BehaviorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BehaviorLogService {
    Page<BehaviorLog> search(String action, Pageable pageable);
    Optional<BehaviorLog> findById(String id);
}
