package com.platform.marketing.modules.contentgeneration.repository;

import com.platform.marketing.modules.contentgeneration.entity.ContentGeneration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentGenerationRepository extends JpaRepository<ContentGeneration, String> {
    @Query("SELECT c FROM ContentGeneration c WHERE " +
           "(:status = '' OR c.status = :status) AND " +
           "(:channel = '' OR c.channels LIKE concat('%',:channel,'%')) AND " +
           "(:kw = '' OR lower(c.name) LIKE lower(concat('%',:kw,'%')))" )
    Page<ContentGeneration> search(@Param("status") String status,
                                   @Param("channel") String channel,
                                   @Param("kw") String keyword,
                                   Pageable pageable);
}
