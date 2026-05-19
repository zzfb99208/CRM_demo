package com.crm.demo.controller;

import com.crm.demo.common.R;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final JdbcTemplate jdbc;
    public ScheduleController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping
    public R<?> list(@RequestParam(required = false) Long productId) {
        String sql = "SELECT ps.*, p.product_name, p.coyote_code FROM production_schedule ps " +
                     "LEFT JOIN product p ON ps.product_id=p.id";
        if (productId != null) sql += " WHERE ps.product_id=" + productId;
        sql += " ORDER BY ps.scheduled_date";
        return R.ok(jdbc.queryForList(sql));
    }
}
