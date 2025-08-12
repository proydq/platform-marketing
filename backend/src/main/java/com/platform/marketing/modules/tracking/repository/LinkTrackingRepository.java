package com.platform.marketing.modules.tracking.repository;

import com.platform.marketing.modules.tracking.entity.LinkTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LinkTrackingRepository extends JpaRepository<LinkTracking, Long> {
    
    Optional<LinkTracking> findByShortUrl(String shortUrl);
    
    List<LinkTracking> findByTrackingId(String trackingId);
    
    @Query("SELECT l FROM LinkTracking l WHERE l.trackingId = :trackingId ORDER BY l.clickCount DESC")
    List<LinkTracking> findTopClickedLinksByTrackingId(@Param("trackingId") String trackingId);
    
    @Query("SELECT SUM(l.clickCount) FROM LinkTracking l WHERE l.trackingId = :trackingId")
    Long getTotalClicksByTrackingId(@Param("trackingId") String trackingId);
    
    @Query("SELECT SUM(l.uniqueClicks) FROM LinkTracking l WHERE l.trackingId = :trackingId")
    Long getTotalUniqueClicksByTrackingId(@Param("trackingId") String trackingId);
    
    @Query("SELECT l.originalUrl, SUM(l.clickCount) as clicks FROM LinkTracking l " +
           "WHERE l.clickedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY l.originalUrl ORDER BY clicks DESC")
    List<Object[]> getTopClickedUrls(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
}