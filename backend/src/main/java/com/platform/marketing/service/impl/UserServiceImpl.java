package com.platform.marketing.service.impl;

import com.platform.marketing.entity.User;
import com.platform.marketing.repository.UserRepository;
import com.platform.marketing.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        existing.setRoleId(user.getRoleId());
        existing.setStatus(user.isStatus());
        return userRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        userRepository.deleteById(id);
    }
}
