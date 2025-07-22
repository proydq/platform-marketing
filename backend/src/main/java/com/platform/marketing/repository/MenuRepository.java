package com.platform.marketing.repository;

import com.platform.marketing.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {

    boolean existsByParentId(String parentId);

    @Query("SELECT m FROM Menu m WHERE (:keyword = '' OR lower(m.name) LIKE lower(concat('%', :keyword, '%')))")
    Page<Menu> search(@Param("keyword") String keyword, Pageable pageable);

    List<Menu> findByParentId(String parentId);
}
