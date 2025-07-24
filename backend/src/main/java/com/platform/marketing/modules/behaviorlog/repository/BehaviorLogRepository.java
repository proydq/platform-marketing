package com.platform.marketing.modules.behaviorlog.repository;

import com.platform.marketing.modules.behaviorlog.entity.BehaviorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BehaviorLogRepository extends JpaRepository<BehaviorLog, String> {
    @Query("SELECT b FROM BehaviorLog b WHERE (:action = '' OR lower(b.action) LIKE lower(concat('%',:action,'%'))) ORDER BY b.time DESC")
    Page<BehaviorLog> search(@Param("action") String action, Pageable pageable);
}
