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

    @GetMapping("/history")
    public R<?> history(@RequestParam(required = false) String status) {
        return R.ok(demoService.getApprovalHistory(status));
    }

    @PostMapping("/pi/{id}")
    public R<?> approvePI(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Object approvedObj = body.get("approved");
            if (approvedObj == null) throw new RuntimeException("missing 'approved' field");
            boolean approved = Boolean.TRUE.equals(approvedObj) || "true".equals(String.valueOf(approvedObj));
            String reason = body.containsKey("reason") ? String.valueOf(body.get("reason")) : "";
            return R.ok(demoService.approvePI(id, approved, reason));
        } catch (Exception e) { return R.fail("Approval failed: " + e.getMessage()); }
    }
}
