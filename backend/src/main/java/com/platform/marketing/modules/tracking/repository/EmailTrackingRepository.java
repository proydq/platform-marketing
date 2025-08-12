package com.platform.marketing.modules.tracking.repository;

import com.platform.marketing.modules.tracking.entity.EmailTracking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTrackingRepository extends JpaRepository<EmailTracking, Long> {
    
    Optional<EmailTracking> findByTrackingId(String trackingId);
    
    List<EmailTracking> findByCampaignId(Long campaignId);
    
    List<EmailTracking> findByCustomerId(Long customerId);
    
    Page<EmailTracking> findByEmailAddressContaining(String emailAddress, Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM EmailTracking e WHERE e.campaignId = :campaignId AND e.status = :status")
    Long countByCampaignIdAndStatus(@Param("campaignId") Long campaignId, @Param("status") String status);
    
    @Query("SELECT COUNT(e) FROM EmailTracking e WHERE e.campaignId = :campaignId AND e.openedAt IS NOT NULL")
    Long countOpenedEmailsByCampaignId(@Param("campaignId") Long campaignId);
    
    @Query("SELECT COUNT(e) FROM EmailTracking e WHERE e.campaignId = :campaignId AND e.firstClickAt IS NOT NULL")
    Long countClickedEmailsByCampaignId(@Param("campaignId") Long campaignId);
    
    @Query("SELECT COUNT(e) FROM EmailTracking e WHERE e.campaignId = :campaignId AND e.repliedAt IS NOT NULL")
    Long countRepliedEmailsByCampaignId(@Param("campaignId") Long campaignId);
    
    @Query("SELECT e FROM EmailTracking e WHERE e.sentAt BETWEEN :startDate AND :endDate ORDER BY e.sentAt DESC")
    List<EmailTracking> findEmailsInDateRange(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e.status, COUNT(e) FROM EmailTracking e WHERE e.campaignId = :campaignId GROUP BY e.status")
    List<Object[]> getStatusDistributionByCampaignId(@Param("campaignId") Long campaignId);
    
    @Query("SELECT DATE(e.sentAt) as date, COUNT(e) as count FROM EmailTracking e " +
           "WHERE e.campaignId = :campaignId AND e.sentAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(e.sentAt) ORDER BY DATE(e.sentAt)")
    List<Object[]> getDailySendStatsByCampaignId(@Param("campaignId") Long campaignId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM EmailTracking e WHERE e.openedAt IS NOT NULL AND " +
           "e.openedAt BETWEEN :startDate AND :endDate ORDER BY e.openedAt DESC")
    List<EmailTracking> findRecentlyOpenedEmails(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate,
                                                Pageable pageable);
}