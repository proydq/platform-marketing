package com.platform.marketing.repository;

import com.platform.marketing.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE (:kw = '' OR lower(u.username) LIKE lower(concat('%', :kw, '%')) " +
           "OR lower(u.email) LIKE lower(concat('%', :kw, '%')))")
    Page<User> search(@Param("kw") String keyword, Pageable pageable);

    java.util.Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
