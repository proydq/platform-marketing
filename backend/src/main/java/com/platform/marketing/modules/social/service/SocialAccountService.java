package com.platform.marketing.modules.social.service;

import com.platform.marketing.modules.social.entity.SocialAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SocialAccountService {
    Page<SocialAccount> search(String platform, String status, String keyword, Pageable pageable);
    Optional<SocialAccount> findById(String id);
    SocialAccount create(SocialAccount account);
    SocialAccount update(String id, SocialAccount account);
    void delete(String id);
}
