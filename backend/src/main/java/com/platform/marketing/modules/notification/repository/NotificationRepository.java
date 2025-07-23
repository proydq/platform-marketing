package com.platform.marketing.modules.notification.repository;

import com.platform.marketing.modules.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    @Query("SELECT n FROM Notification n WHERE " +
            "(:type = '' OR n.type = :type) AND " +
            "(:status = '' OR n.status = :status) AND " +
            "(:kw = '' OR lower(n.title) LIKE lower(concat('%',:kw,'%')) OR " +
            "lower(n.content) LIKE lower(concat('%',:kw,'%'))) " +
            "ORDER BY n.createdTime DESC")
    Page<Notification> search(@Param("type") String type,
                              @Param("status") String status,
                              @Param("kw") String keyword,
                              Pageable pageable);
}
