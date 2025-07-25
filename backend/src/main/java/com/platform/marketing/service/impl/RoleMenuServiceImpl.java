package com.platform.marketing.service.impl;

import com.platform.marketing.entity.Menu;
import com.platform.marketing.entity.SysRoleMenu;
import com.platform.marketing.entity.SysRoleMenuId;
import com.platform.marketing.entity.UserRole;
import com.platform.marketing.repository.MenuRepository;
import com.platform.marketing.repository.RoleMenuRepository;
import com.platform.marketing.repository.UserRoleRepository;
import com.platform.marketing.service.RoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;
    private final MenuRepository menuRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleMenuServiceImpl(RoleMenuRepository roleMenuRepository,
                               MenuRepository menuRepository,
                               UserRoleRepository userRoleRepository) {
        this.roleMenuRepository = roleMenuRepository;
        this.menuRepository = menuRepository;
        this.userRoleRepository = userRoleRepository;
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
}
