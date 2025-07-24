package com.platform.marketing.modules.social.repository;

import com.platform.marketing.modules.social.entity.SocialAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, String> {
    @Query("SELECT a FROM SocialAccount a WHERE " +
           "(:platform = '' OR a.platform = :platform) AND " +
           "(:status = '' OR a.status = :status) AND " +
           "(:kw = '' OR lower(a.name) LIKE lower(concat('%',:kw,'%')))" )
    Page<SocialAccount> search(@Param("platform") String platform,
                               @Param("status") String status,
                               @Param("kw") String keyword,
                               Pageable pageable);
}
