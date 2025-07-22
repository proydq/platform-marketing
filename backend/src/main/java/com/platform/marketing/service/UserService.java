package com.platform.marketing.service;

import com.platform.marketing.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<User> search(String keyword, Pageable pageable);
    User create(User user);
    User update(String id, User user);
    User update(User user);
    void delete(String id);
    User findByUsername(String username);

    void assignRoles(String userId, java.util.List<String> roleIds);

    java.util.List<String> getRoleIdsByUser(String userId);
}
