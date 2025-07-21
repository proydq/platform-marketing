package com.platform.marketing.module.user.repository;

import com.platform.marketing.module.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity.
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("select u from User u where u.isDeleted=false")
    Page<User> findAllActive(Pageable pageable);

    @Query("select u from User u where u.isDeleted=false and (u.username like %:kw% or u.email like %:kw%)")
    Page<User> search(@Param("kw") String keyword, Pageable pageable);

    Optional<User> findByIdAndIsDeletedFalse(UUID id);
}
