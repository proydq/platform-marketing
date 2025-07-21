package com.platform.marketing.module.user.service.impl;

import com.platform.marketing.module.user.dto.UserQueryDTO;
import com.platform.marketing.module.user.dto.UserSaveDTO;
import com.platform.marketing.module.user.dto.UserVO;
import com.platform.marketing.module.user.entity.User;
import com.platform.marketing.module.user.repository.UserRepository;
import com.platform.marketing.module.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of user service.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<UserVO> fetchUsers(UserQueryDTO queryDTO) {
        PageRequest page = PageRequest.of(queryDTO.getPage(), queryDTO.getSize());
        Page<User> users;
        if (queryDTO.getKeyword() == null || queryDTO.getKeyword().isEmpty()) {
            users = userRepository.findAllActive(page);
        } else {
            users = userRepository.search(queryDTO.getKeyword(), page);
        }
        List<UserVO> voList = users.stream().map(this::toVO).collect(Collectors.toList());
        return new PageImpl<>(voList, page, users.getTotalElements());
    }

    @Override
    public UserVO createUser(UserSaveDTO saveDTO) {
        User user = new User();
        BeanUtils.copyProperties(saveDTO, user);
        user.setPassword(passwordEncoder.encode(saveDTO.getPassword()));
        userRepository.save(user);
        return toVO(user);
    }

    @Override
    public UserVO updateUser(UserSaveDTO saveDTO) {
        User user = userRepository.findByIdAndIsDeletedFalse(saveDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (saveDTO.getUsername() != null) user.setUsername(saveDTO.getUsername());
        if (saveDTO.getEmail() != null) user.setEmail(saveDTO.getEmail());
        if (saveDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(saveDTO.getPassword()));
        }
        if (saveDTO.getStatus() != null) user.setStatus(saveDTO.getStatus());
        return toVO(userRepository.save(user));
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.findByIdAndIsDeletedFalse(id)
                .ifPresent(u -> { u.setIsDeleted(true); userRepository.save(u); });
    }

    @Override
    public void importUsers(MultipartFile file) {
        if (file == null) return;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split(",");
                if (arr.length >= 2) {
                    User user = new User();
                    user.setUsername(arr[0]);
                    user.setEmail(arr[1]);
                    user.setPassword(passwordEncoder.encode("123456"));
                    userRepository.save(user);
                }
            }
        } catch (IOException ignored) {}
    }

    @Override
    public void updateUserStatus(UUID id, Boolean status) {
        userRepository.findByIdAndIsDeletedFalse(id)
                .ifPresent(u -> { u.setStatus(status); userRepository.save(u); });
    }

    @Override
    public void resetPassword(UUID id) {
        userRepository.findByIdAndIsDeletedFalse(id)
                .ifPresent(u -> { u.setPassword(passwordEncoder.encode("123456")); userRepository.save(u); });
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
