package com.platform.marketing.controller;

import com.platform.marketing.dto.MenuTreeNode;
import com.platform.marketing.entity.Menu;
import com.platform.marketing.service.MenuService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/tree")
    public ResponseEntity<List<MenuTreeNode>> tree() {
        return ResponseEntity.success(menuService.getTree());
    }

    @GetMapping
    @PreAuthorize("hasPermission('menu:view')")
    public ResponseEntity<ResponsePageDataEntity<Menu>> list(@RequestParam(defaultValue = "") String keyword,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Page<Menu> p = menuService.search(keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('menu:view')")
    public ResponseEntity<Menu> get(@PathVariable String id) {
        return menuService.findById(id)
                .map(ResponseEntity::success)
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }

    @PostMapping
    @PreAuthorize("hasPermission('menu:create')")
    public ResponseEntity<Menu> create(@RequestBody Menu menu) {
        if (menu.getId() == null || menu.getId().isEmpty()) {
            menu.setId(java.util.UUID.randomUUID().toString());
        }
        return ResponseEntity.success(menuService.create(menu));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('menu:update')")
    public ResponseEntity<Menu> update(@PathVariable String id, @RequestBody Menu menu) {
        return ResponseEntity.success(menuService.update(id, menu));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('menu:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            menuService.delete(id);
            return ResponseEntity.success(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.fail(400, e.getMessage());
        }
    }

    @PostMapping("/update-status")
    @PreAuthorize("hasPermission('menu:status')")
    public ResponseEntity<Void> updateStatus(@RequestBody java.util.Map<String, Object> body) {
        String id = (String) body.get("id");
        Boolean status = (Boolean) body.get("status");
        if (id == null || status == null) {
            return ResponseEntity.fail(400, "id and status required");
        }
        menuService.updateStatus(id, status);
        return ResponseEntity.success(null);
    }
}
