package com.platform.marketing.module.user.service;

import com.platform.marketing.module.user.dto.UserQueryDTO;
import com.platform.marketing.module.user.dto.UserSaveDTO;
import com.platform.marketing.module.user.dto.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * User service interface.
 */
public interface UserService {
    Page<UserVO> fetchUsers(UserQueryDTO queryDTO);

    UserVO createUser(UserSaveDTO saveDTO);

    UserVO updateUser(UserSaveDTO saveDTO);

    void deleteUser(UUID id);

    void importUsers(MultipartFile file);

    void updateUserStatus(UUID id, Boolean status);

    void resetPassword(UUID id);
}
