package com.platform.marketing.modules.social.repository;

import com.platform.marketing.modules.social.entity.SocialPushLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialPushLogRepository extends JpaRepository<SocialPushLog, String> {
    @Query("SELECT l FROM SocialPushLog l WHERE " +
           "(:accountId = '' OR l.accountId = :accountId) AND " +
           "(:status = '' OR l.status = :status) " +
           "ORDER BY l.time DESC")
    Page<SocialPushLog> search(@Param("accountId") String accountId,
                               @Param("status") String status,
                               Pageable pageable);
}
