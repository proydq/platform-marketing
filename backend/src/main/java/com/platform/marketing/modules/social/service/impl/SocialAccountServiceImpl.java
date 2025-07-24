package com.platform.marketing.modules.social.service.impl;

import com.platform.marketing.modules.social.entity.SocialAccount;
import com.platform.marketing.modules.social.repository.SocialAccountRepository;
import com.platform.marketing.modules.social.service.SocialAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SocialAccountServiceImpl implements SocialAccountService {

    private final SocialAccountRepository socialAccountRepository;

    public SocialAccountServiceImpl(SocialAccountRepository socialAccountRepository) {
        this.socialAccountRepository = socialAccountRepository;
    }

    @Override
    public Page<SocialAccount> search(String platform, String status, String keyword, Pageable pageable) {
        if (platform == null) platform = "";
        if (status == null) status = "";
        if (keyword == null) keyword = "";
        return socialAccountRepository.search(platform, status, keyword, pageable);
    }

    @Override
    public Optional<SocialAccount> findById(String id) {
        return socialAccountRepository.findById(id);
    }

    @Override
    @Transactional
    public SocialAccount create(SocialAccount account) {
        return socialAccountRepository.save(account);
    }

    @Override
    @Transactional
    public SocialAccount update(String id, SocialAccount account) {
        SocialAccount existing = socialAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SocialAccount not found"));
        existing.setPlatform(account.getPlatform());
        existing.setName(account.getName());
        existing.setAccessToken(account.getAccessToken());
        existing.setNote(account.getNote());
        existing.setStatus(account.getStatus());
        existing.setBindTime(account.getBindTime());
        return socialAccountRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        socialAccountRepository.deleteById(id);
    }
}
