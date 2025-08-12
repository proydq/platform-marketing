package com.platform.marketing.modules.customer.repository;

import com.platform.marketing.modules.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {
    
    @Query("SELECT c FROM Customer c WHERE (:kw = '' OR lower(c.name) LIKE lower(concat('%',:kw,'%')) " +
            "OR lower(c.email) LIKE lower(concat('%',:kw,'%')) OR lower(c.phone) LIKE lower(concat('%',:kw,'%'))) ")
    Page<Customer> search(@Param("kw") String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT c.source FROM Customer c WHERE c.source IS NOT NULL ORDER BY c.source")
    List<String> findDistinctSources();
}
