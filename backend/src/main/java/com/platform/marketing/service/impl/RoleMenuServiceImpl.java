package com.platform.marketing.service.impl;

import com.platform.marketing.entity.Menu;
import com.platform.marketing.entity.SysRoleMenu;
import com.platform.marketing.entity.SysRoleMenuId;
import com.platform.marketing.entity.UserRole;
import com.platform.marketing.entity.User;
import com.platform.marketing.entity.Role;
import com.platform.marketing.repository.MenuRepository;
import com.platform.marketing.repository.RoleMenuRepository;
import com.platform.marketing.repository.UserRoleRepository;
import com.platform.marketing.repository.UserRepository;
import com.platform.marketing.repository.RoleRepository;
import com.platform.marketing.service.RoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;
    private final MenuRepository menuRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RoleMenuServiceImpl(RoleMenuRepository roleMenuRepository,
                               MenuRepository menuRepository,
                               UserRoleRepository userRoleRepository,
                               UserRepository userRepository,
                               RoleRepository roleRepository) {
        this.roleMenuRepository = roleMenuRepository;
        this.menuRepository = menuRepository;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void assignMenusToRole(String roleId, List<String> menuIds) {
        roleMenuRepository.deleteByRoleId(roleId);
        if (menuIds != null) {
            for (String menuId : menuIds) {
                SysRoleMenuId id = new SysRoleMenuId(roleId, menuId);
                roleMenuRepository.save(new SysRoleMenu(id));
            }
        }
    }

    @Override
    public List<Menu> getMenusByUser(String userId) {
        List<UserRole> roles = userRoleRepository.findByIdUserId(userId);
        Set<String> menuIds = new HashSet<>();
        for (UserRole ur : roles) {
            List<SysRoleMenu> list = roleMenuRepository.findByIdRoleId(ur.getId().getRoleId());
            for (SysRoleMenu rm : list) {
                menuIds.add(rm.getId().getMenuId());
            }
        }
        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }
        return menuRepository.findAllById(menuIds);
    }

    @Override
    public List<User> getUsersByMenu(String menuId) {
        List<SysRoleMenu> roleMenus = roleMenuRepository.findByIdMenuId(menuId);
        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> roleIds = new HashSet<>();
        for (SysRoleMenu rm : roleMenus) {
            roleIds.add(rm.getId().getRoleId());
        }
        List<UserRole> userRoles = userRoleRepository.findByIdRoleIdIn(roleIds);
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> userIds = new HashSet<>();
        for (UserRole ur : userRoles) {
            userIds.add(ur.getId().getUserId());
        }
        return userRepository.findAllById(userIds);
    }

    @Override
    @Transactional
    public void assignRolesToMenu(String menuId, List<String> roleIds) {
        roleMenuRepository.deleteByMenuId(menuId);
        if (roleIds != null) {
            for (String roleId : roleIds) {
                SysRoleMenuId id = new SysRoleMenuId(roleId, menuId);
                roleMenuRepository.save(new SysRoleMenu(id));
            }
        }
    }

    @Override
    public List<Role> getRolesByMenu(String menuId) {
        List<SysRoleMenu> roleMenus = roleMenuRepository.findByIdMenuId(menuId);
        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> roleIds = new HashSet<>();
        for (SysRoleMenu rm : roleMenus) {
            roleIds.add(rm.getId().getRoleId());
        }
        return roleRepository.findAllById(roleIds);
    }
}
