package com.platform.marketing.modules.social.service;

import com.platform.marketing.modules.social.entity.SocialPushLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SocialPushLogService {
    Page<SocialPushLog> search(String accountId, String status, Pageable pageable);
    SocialPushLog create(SocialPushLog log);
}
