package com.platform.marketing.module.auth.controller;

import com.platform.marketing.module.auth.dto.Result;
import com.platform.marketing.module.auth.dto.UserVO;
import com.platform.marketing.module.auth.service.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public Result<UserVO> me(@AuthenticationPrincipal UserDetails userDetails) {
        return Result.ok(authService.currentUser(userDetails.getUsername()));
    }
}
