package com.platform.marketing.modules.contentgeneration.service;

import com.platform.marketing.modules.contentgeneration.entity.ContentGeneration;
import com.platform.marketing.modules.contentgeneration.dto.ContentGenerationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContentGenerationService {
    Page<ContentGeneration> search(String status, String channel, String keyword, Pageable pageable);
    Optional<ContentGeneration> findById(String id);
    ContentGeneration create(ContentGenerationDto content);
    ContentGeneration update(String id, ContentGenerationDto content);
    void delete(String id);
}
