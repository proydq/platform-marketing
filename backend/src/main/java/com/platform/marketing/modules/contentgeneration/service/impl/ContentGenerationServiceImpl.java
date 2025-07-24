package com.platform.marketing.modules.contentgeneration.service.impl;

import com.platform.marketing.modules.contentgeneration.entity.ContentGeneration;
import com.platform.marketing.modules.contentgeneration.repository.ContentGenerationRepository;
import com.platform.marketing.modules.contentgeneration.service.ContentGenerationService;
import com.platform.marketing.modules.contentgeneration.dto.ContentGenerationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ContentGenerationServiceImpl implements ContentGenerationService {

    private final ContentGenerationRepository repository;

    public ContentGenerationServiceImpl(ContentGenerationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<ContentGeneration> search(String status, String channel, String keyword, Pageable pageable) {
        if (status == null) status = "";
        if (channel == null) channel = "";
        if (keyword == null) keyword = "";
        return repository.search(status, channel, keyword, pageable);
    }

    @Override
    public Optional<ContentGeneration> findById(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public ContentGeneration create(ContentGenerationDto dto) {
        ContentGeneration entity = new ContentGeneration();
        ContentGenerationDto.copyToEntity(dto, entity);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ContentGeneration update(String id, ContentGenerationDto dto) {
        ContentGeneration existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));
        ContentGenerationDto.copyToEntity(dto, existing);
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }
}
