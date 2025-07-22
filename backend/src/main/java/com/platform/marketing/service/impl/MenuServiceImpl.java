package com.platform.marketing.service.impl;

import com.platform.marketing.dto.MenuTreeNode;
import com.platform.marketing.entity.Menu;
import com.platform.marketing.repository.MenuRepository;
import com.platform.marketing.service.MenuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    public MenuServiceImpl(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public Page<Menu> search(String keyword, Pageable pageable) {
        if (keyword == null) keyword = "";
        return menuRepository.search(keyword, pageable);
    }

    @Override
    public Optional<Menu> findById(String id) {
        return menuRepository.findById(id);
    }

    @Override
    @Transactional
    public Menu create(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    @Transactional
    public Menu update(String id, Menu menu) {
        Menu existing = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        existing.setName(menu.getName());
        existing.setParentId(menu.getParentId());
        existing.setPath(menu.getPath());
        existing.setIcon(menu.getIcon());
        existing.setOrderNum(menu.getOrderNum());
        existing.setPermission(menu.getPermission());
        existing.setStatus(menu.getStatus());
        existing.setType(menu.getType());
        existing.setComponent(menu.getComponent());
        existing.setKeepAlive(menu.getKeepAlive());
        existing.setExternal(menu.getExternal());
        existing.setHidden(menu.getHidden());
        existing.setRemark(menu.getRemark());
        return menuRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (menuRepository.existsByParentId(id)) {
            throw new IllegalStateException("Menu has children");
        }
        menuRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(String id, Boolean status) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        menu.setStatus(status);
        menuRepository.save(menu);
    }

    @Override
    public List<MenuTreeNode> getTree() {
        List<Menu> all = menuRepository.findAll();
        Map<String, MenuTreeNode> map = new HashMap<>();
        for (Menu m : all) {
            MenuTreeNode node = new MenuTreeNode();
            node.setId(m.getId());
            node.setName(m.getName());
            node.setParentId(m.getParentId());
            node.setPath(m.getPath());
            node.setIcon(m.getIcon());
            node.setOrderNum(m.getOrderNum());
            node.setPermission(m.getPermission());
            node.setStatus(m.getStatus());
            node.setType(m.getType());
            node.setComponent(m.getComponent());
            node.setKeepAlive(m.getKeepAlive());
            node.setExternal(m.getExternal());
            node.setHidden(m.getHidden());
            node.setRemark(m.getRemark());
            map.put(m.getId(), node);
        }
        List<MenuTreeNode> roots = new ArrayList<>();
        for (MenuTreeNode n : map.values()) {
            if (n.getParentId() == null || n.getParentId().isEmpty()) {
                roots.add(n);
            } else {
                MenuTreeNode parent = map.get(n.getParentId());
                if (parent != null) {
                    parent.getChildren().add(n);
                } else {
                    roots.add(n);
                }
            }
        }
        roots.sort(Comparator.comparing(MenuTreeNode::getOrderNum, Comparator.nullsLast(Integer::compareTo)));
        for (MenuTreeNode n : map.values()) {
            n.getChildren().sort(Comparator.comparing(MenuTreeNode::getOrderNum, Comparator.nullsLast(Integer::compareTo)));
        }
        return roots;
    }
}
