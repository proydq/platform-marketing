package com.platform.marketing.modules.dashboard.service;

import com.platform.marketing.modules.dashboard.dto.KeywordSearchCountDto;

public interface DashboardService {
    KeywordSearchCountDto getKeywordSearchCount(String keyword);
}
