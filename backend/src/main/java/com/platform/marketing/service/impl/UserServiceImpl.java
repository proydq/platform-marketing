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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
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
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setStatus(user.isStatus());
        return userRepository.save(existing);
    }

    @Override
    @Transactional
    public User update(User user) {
        User existing = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setStatus(user.isStatus());
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
    public void assignRoles(String userId, List<String> roleIds) {
        userRoleRepository.deleteByIdUserId(userId);
        if (roleIds != null) {
            for (String roleId : roleIds) {
                UserRoleId id = new UserRoleId(userId, roleId);
                userRoleRepository.save(new UserRole(id));
            }
        }
    }

    @Override
    public List<String> getRoleIdsByUser(String userId) {
        List<UserRole> list = userRoleRepository.findByIdUserId(userId);
        List<String> ids = new ArrayList<>();
        for (UserRole ur : list) {
            ids.add(ur.getId().getRoleId());
        }
        return ids;
    }

    @Override
    @Transactional
    public void updateStatus(String id, Boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(String id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
