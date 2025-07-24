package com.platform.marketing.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.marketing.system.dto.*;
import com.platform.marketing.system.entity.SystemSettings;
import com.platform.marketing.system.repository.SystemSettingsRepository;
import com.platform.marketing.system.service.SystemSettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemSettingsServiceImpl implements SystemSettingsService {

    private final SystemSettingsRepository repository;
    private final ObjectMapper mapper;

    public SystemSettingsServiceImpl(SystemSettingsRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    private SystemSettings getEntity() {
        return repository.findById("default").orElseGet(() -> {
            SystemSettings s = new SystemSettings();
            s.setId("default");
            s.setBasicSettings("{}");
            s.setNotificationSettings("{}");
            s.setSecuritySettings("{}");
            return repository.save(s);
        });
    }

    private <T> T read(String json, Class<T> clazz) {
        try {
            if (json == null || json.isEmpty()) {
                return clazz.getConstructor().newInstance();
            }
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private String write(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SystemSettingsDto getSettings() {
        SystemSettings entity = getEntity();
        SystemSettingsDto dto = new SystemSettingsDto();
        dto.setBasicSettings(read(entity.getBasicSettings(), BasicSettingsDto.class));
        dto.setNotificationSettings(read(entity.getNotificationSettings(), NotifySettingsDto.class));
        dto.setSecuritySettings(read(entity.getSecuritySettings(), SecuritySettingsDto.class));
        return dto;
    }

    @Override
    @Transactional
    public void saveBasicSettings(BasicSettingsDto dto) {
        SystemSettings entity = getEntity();
        entity.setBasicSettings(write(dto));
        repository.save(entity);
    }

    @Override
    @Transactional
    public void saveNotificationSettings(NotifySettingsDto dto) {
        SystemSettings entity = getEntity();
        entity.setNotificationSettings(write(dto));
        repository.save(entity);
    }

    @Override
    @Transactional
    public void saveSecuritySettings(SecuritySettingsDto dto) {
        SystemSettings entity = getEntity();
        entity.setSecuritySettings(write(dto));
        repository.save(entity);
    }
}
