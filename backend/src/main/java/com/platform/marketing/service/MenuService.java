package com.platform.marketing.service;

import com.platform.marketing.dto.MenuTreeNode;
import com.platform.marketing.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MenuService {
    Page<Menu> search(String keyword, Pageable pageable);
    Optional<Menu> findById(String id);
    Menu create(Menu menu);
    Menu update(String id, Menu menu);
    void delete(String id);
    void updateStatus(String id, Boolean status);
    List<MenuTreeNode> getTree();
}
