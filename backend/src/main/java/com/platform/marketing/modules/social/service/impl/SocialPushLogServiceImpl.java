package com.platform.marketing.modules.social.service.impl;

import com.platform.marketing.modules.social.entity.SocialPushLog;
import com.platform.marketing.modules.social.repository.SocialPushLogRepository;
import com.platform.marketing.modules.social.service.SocialPushLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocialPushLogServiceImpl implements SocialPushLogService {

    private final SocialPushLogRepository logRepository;

    public SocialPushLogServiceImpl(SocialPushLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public Page<SocialPushLog> search(String accountId, String status, Pageable pageable) {
        if (accountId == null) accountId = "";
        if (status == null) status = "";
        return logRepository.search(accountId, status, pageable);
    }

    @Override
    @Transactional
    public SocialPushLog create(SocialPushLog log) {
        return logRepository.save(log);
    }
}
