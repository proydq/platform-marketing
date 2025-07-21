package com.platform.marketing.service;

import com.platform.marketing.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<User> search(String keyword, Pageable pageable);
    User create(User user);
    User update(String id, User user);
    void delete(String id);
    void resetPassword(String id);
    void updateStatus(String id, boolean status);
    User findByUsername(String username);
}
