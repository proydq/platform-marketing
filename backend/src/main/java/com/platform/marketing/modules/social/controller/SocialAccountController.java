package com.platform.marketing.modules.social.controller;

import com.platform.marketing.modules.social.entity.SocialAccount;
import com.platform.marketing.modules.social.service.SocialAccountService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/social-account")
public class SocialAccountController {

    private final SocialAccountService socialAccountService;

    public SocialAccountController(SocialAccountService socialAccountService) {
        this.socialAccountService = socialAccountService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('social-account:list')")
    public ResponseEntity<ResponsePageDataEntity<SocialAccount>> list(@RequestParam(defaultValue = "") String platform,
                                                                     @RequestParam(defaultValue = "") String status,
                                                                     @RequestParam(defaultValue = "") String keyword,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Page<SocialAccount> p = socialAccountService.search(platform, status, keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('social-account:view')")
    public ResponseEntity<SocialAccount> get(@PathVariable String id) {
        return socialAccountService.findById(id)
                .map(ResponseEntity::success)
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }

    @PostMapping
    @PreAuthorize("hasPermission('social-account:create')")
    public ResponseEntity<SocialAccount> create(@RequestBody SocialAccount account) {
        return ResponseEntity.success(socialAccountService.create(account));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('social-account:update')")
    public ResponseEntity<SocialAccount> update(@PathVariable String id, @RequestBody SocialAccount account) {
        return ResponseEntity.success(socialAccountService.update(id, account));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('social-account:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        socialAccountService.delete(id);
        return ResponseEntity.success(null);
    }
}
