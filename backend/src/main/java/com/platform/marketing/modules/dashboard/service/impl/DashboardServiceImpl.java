package com.platform.marketing.modules.dashboard.service.impl;

import com.platform.marketing.modules.dashboard.dto.KeywordSearchCountDto;
import com.platform.marketing.modules.dashboard.service.DashboardService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final RestTemplate restTemplate;

    public DashboardServiceImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Override
    public KeywordSearchCountDto getKeywordSearchCount(String keyword) {
        // simulate request to search engine
        try {
            restTemplate.getForObject("https://www.bing.com/search?q={keyword}", String.class, keyword);
        } catch (Exception ignored) {
        }
        // mock count data
        long count = 8500000L;
        return new KeywordSearchCountDto(keyword, count);
    }
}
