package com.platform.marketing.controller;

import com.platform.marketing.util.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.success("Hello, World!");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> testLogin(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Test login endpoint works");
        response.put("username", request.get("username"));
        response.put("password", request.get("password"));
        return ResponseEntity.success(response);
    }
}