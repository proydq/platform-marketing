package com.platform.marketing.modules.customercollect.repository;

import com.platform.marketing.modules.customercollect.entity.CustomerCollect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCollectRepository extends JpaRepository<CustomerCollect, String> {
    @Query("SELECT c FROM CustomerCollect c WHERE (:kw = '' OR lower(c.name) LIKE lower(concat('%',:kw,'%')) " +
            "OR lower(c.phone) LIKE lower(concat('%',:kw,'%')) " +
            "OR lower(c.email) LIKE lower(concat('%',:kw,'%')) " +
            "OR lower(c.company) LIKE lower(concat('%',:kw,'%')) " +
            "OR lower(c.jobTitle) LIKE lower(concat('%',:kw,'%')) " +
            "OR lower(c.source) LIKE lower(concat('%',:kw,'%')))" )
    Page<CustomerCollect> search(@Param("kw") String keyword, Pageable pageable);
}
