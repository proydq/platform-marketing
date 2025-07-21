package com.platform.marketing.module.auth.repository;

import com.platform.marketing.module.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
}
