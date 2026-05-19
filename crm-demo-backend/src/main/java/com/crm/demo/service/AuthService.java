package com.crm.demo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AuthService {
    private final JdbcTemplate jdbc;
    private final Map<String, Map<String, Object>> tokenStore = new HashMap<>();

    public AuthService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Map<String, Object> login(String username, String password) {
        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT id, username, role, display_name FROM user WHERE username=? AND password=?",
            username, password);
        if (rows.isEmpty()) throw new RuntimeException("Username or password incorrect");
        Map<String, Object> user = rows.get(0);
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("role", user.get("role"));
        result.put("displayName", user.get("display_name"));
        return result;
    }

    public Map<String, Object> getUserByToken(String token) {
        Map<String, Object> user = tokenStore.get(token);
        if (user == null) throw new RuntimeException("Not logged in");
        return user;
    }
}
