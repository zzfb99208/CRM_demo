package com.crm.demo.controller;

import com.crm.demo.common.R;
import com.crm.demo.service.DemoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/proforma-invoices")
public class ProformaInvoiceController {
    private final DemoService demoService;
    public ProformaInvoiceController(DemoService ds) { this.demoService = ds; }

    @PostMapping("/generate/{poId}")
    public R<?> generate(@PathVariable Long poId) {
        try { return R.ok(demoService.generatePI(poId)); }
        catch (Exception e) { return R.fail("PI generation failed: " + e.getMessage()); }
    }

    @GetMapping
    public R<?> list(@RequestParam(required = false) Long customerId) {
        return R.ok(demoService.getPIList(customerId));
    }

    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) { return R.ok(demoService.getPIDetail(id)); }

    @GetMapping("/{id}/export")
    public void export(@PathVariable Long id, HttpServletResponse response) {
        try { demoService.exportPI(id, response); } catch (Exception e) {}
    }

    @PutMapping("/{piId}/items/{itemId}")
    public R<?> updateItem(@PathVariable Long piId, @PathVariable Long itemId,
                           @RequestBody java.util.Map<String, Object> updates) {
        try { return R.ok(demoService.updatePIItem(piId, itemId, updates)); }
        catch (Exception e) { return R.fail("Update failed: " + e.getMessage()); }
    }

    @PostMapping("/{id}/submit")
    public R<?> submit(@PathVariable Long id) {
        try { return R.ok(demoService.submitPIForApproval(id)); }
        catch (Exception e) { return R.fail("Submit failed: " + e.getMessage()); }
    }

    @PostMapping("/{id}/import")
    public R<?> reimport(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try { return R.ok(demoService.reimportPI(id, file)); }
        catch (Exception e) { return R.fail("PI reimport failed: " + e.getMessage()); }
    }
}
