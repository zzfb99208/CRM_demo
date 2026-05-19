package com.crm.demo.controller;

import com.crm.demo.common.R;
import com.crm.demo.service.DemoService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {
    private final DemoService demoService;
    public ApprovalController(DemoService ds) { this.demoService = ds; }

    @GetMapping("/pending")
    public R<?> pending() { return R.ok(demoService.getPendingApprovals()); }

    @PostMapping("/pi/{id}")
    public R<?> approvePI(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            boolean approved = (boolean) body.get("approved");
            String reason = (String) body.getOrDefault("reason", "");
            return R.ok(demoService.approvePI(id, approved, reason));
        } catch (Exception e) { return R.fail("Approval failed: " + e.getMessage()); }
    }
}
