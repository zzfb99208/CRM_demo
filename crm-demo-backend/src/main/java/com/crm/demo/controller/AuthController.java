package com.crm.demo.controller;

import com.crm.demo.common.R;
import com.crm.demo.service.AuthService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService as) { this.authService = as; }

    @PostMapping("/login")
    public R<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        try {
            Map<String, Object> result = authService.login(username, password);
            return R.ok(result);
        } catch (RuntimeException e) {
            return R.fail(401, e.getMessage());
        }
    }

    @GetMapping("/me")
    public R<?> me(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        return R.ok(authService.getUserByToken(token));
    }
}
