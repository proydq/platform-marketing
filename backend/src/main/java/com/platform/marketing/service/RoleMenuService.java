package com.platform.marketing.service;

import com.platform.marketing.entity.Menu;

import java.util.List;

public interface RoleMenuService {
    void assignMenusToRole(String roleId, List<String> menuIds);
    List<Menu> getMenusByUser(String userId);
    java.util.List<com.platform.marketing.entity.User> getUsersByMenu(String menuId);
}
