package com.crm.demo.controller;

import com.crm.demo.common.R;
import com.crm.demo.service.DemoService;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/packing-lists")
public class PackingListController {
    private final DemoService demoService;
    public PackingListController(DemoService ds) { this.demoService = ds; }

    @PostMapping("/generate/{piId}")
    public R<?> generate(@PathVariable Long piId) {
        try { return R.ok(demoService.generatePL(piId)); }
        catch (Exception e) { return R.fail("PL generation failed: " + e.getMessage()); }
    }

    @GetMapping
    public R<?> list(@RequestParam(required = false) Long customerId) {
        return R.ok(demoService.getPLList(customerId));
    }

    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) { return R.ok(demoService.getPLDetail(id)); }

    @GetMapping("/{id}/export")
    public void export(@PathVariable Long id, HttpServletResponse response) {
        try { demoService.exportPL(id, response); } catch (Exception e) {}
    }
}
