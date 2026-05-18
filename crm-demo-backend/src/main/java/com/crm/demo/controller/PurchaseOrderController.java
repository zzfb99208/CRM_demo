package com.crm.demo.controller;

import com.crm.demo.common.R;
import com.crm.demo.service.DemoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {
    private final DemoService demoService;
    public PurchaseOrderController(DemoService ds) { this.demoService = ds; }

    @PostMapping("/import")
    public R<?> importExcel(@RequestParam("file") MultipartFile file,
                            @RequestParam("customerId") Long customerId) {
        try { return R.ok(demoService.importPoExcel(file, customerId)); }
        catch (Exception e) { return R.fail("Import failed: " + e.getMessage()); }
    }

    @GetMapping
    public R<?> list(@RequestParam(required = false) Long customerId) {
        return R.ok(demoService.getPOList(customerId));
    }

    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) { return R.ok(demoService.getPODetail(id)); }
}
