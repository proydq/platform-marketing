package com.platform.marketing.service.impl;

import com.platform.marketing.entity.User;
import com.platform.marketing.entity.UserRole;
import com.platform.marketing.entity.UserRoleId;
import com.platform.marketing.repository.UserRepository;
import com.platform.marketing.repository.UserRoleRepository;
import com.platform.marketing.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Page<User> search(String keyword, Pageable pageable) {
        if (keyword == null) keyword = "";
        return userRepository.search(keyword, pageable);
    }

    @Override
    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(String id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        return userRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        userRepository.deleteById(id);
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    @Transactional
    public void assignRoles(String userId, java.util.List<String> roleIds) {
        userRoleRepository.deleteByIdUserId(userId);
        if (roleIds != null) {
            for (String roleId : roleIds) {
                UserRoleId id = new UserRoleId(userId, roleId);
                userRoleRepository.save(new UserRole(id));
            }
        }
    }

    @Override
    public java.util.List<String> getRoleIdsByUser(String userId) {
        java.util.List<UserRole> list = userRoleRepository.findByIdUserId(userId);
        java.util.List<String> ids = new java.util.ArrayList<>();
        for (UserRole ur : list) {
            ids.add(ur.getId().getRoleId());
        }
        return ids;
    }

    @Override
    @Transactional
    public void assignRoles(String userId, java.util.List<String> roleIds) {
        userRoleRepository.deleteByIdUserId(userId);
        if (roleIds != null) {
            for (String roleId : roleIds) {
                UserRoleId id = new UserRoleId(userId, roleId);
                userRoleRepository.save(new UserRole(id));
            }
            if (!roleIds.isEmpty()) {
                userRepository.findById(userId).ifPresent(u -> {
                    u.setRoleId(roleIds.get(0));
                    userRepository.save(u);
                });
            }
        }
    }

    @Override
    public java.util.List<String> getRoleIdsByUser(String userId) {
        java.util.List<UserRole> list = userRoleRepository.findByIdUserId(userId);
        java.util.List<String> ids = new java.util.ArrayList<>();
        for (UserRole ur : list) {
            ids.add(ur.getId().getRoleId());
        }
        return ids;
    }

}
