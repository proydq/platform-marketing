package com.platform.marketing.modules.email.repository;

import com.platform.marketing.modules.email.entity.EmailCampaignRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailCampaignRecordRepository extends JpaRepository<EmailCampaignRecord, String> {
    @Query("SELECT r FROM EmailCampaignRecord r WHERE (:status = '' OR r.status = :status)")
    Page<EmailCampaignRecord> search(@Param("status") String status, Pageable pageable);
}
