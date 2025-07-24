package com.platform.marketing.modules.behaviorlog.service.impl;

import com.platform.marketing.modules.behaviorlog.entity.BehaviorLog;
import com.platform.marketing.modules.behaviorlog.repository.BehaviorLogRepository;
import com.platform.marketing.modules.behaviorlog.service.BehaviorLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BehaviorLogServiceImpl implements BehaviorLogService {

    private final BehaviorLogRepository behaviorLogRepository;

    public BehaviorLogServiceImpl(BehaviorLogRepository behaviorLogRepository) {
        this.behaviorLogRepository = behaviorLogRepository;
    }

    @Override
    public Page<BehaviorLog> search(String action, Pageable pageable) {
        if (action == null) action = "";
        return behaviorLogRepository.search(action, pageable);
    }

    @Override
    public Optional<BehaviorLog> findById(String id) {
        return behaviorLogRepository.findById(id);
    }
}
