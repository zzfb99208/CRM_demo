package com.crm.demo.controller;

import com.crm.demo.common.R;
import com.crm.demo.mapper.CustomerMapper;
import com.crm.demo.service.DemoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final DemoService demoService;
    private final CustomerMapper customerMapper;
    public CustomerController(DemoService ds, CustomerMapper cm) { this.demoService = ds; this.customerMapper = cm; }

    @GetMapping
    public R<?> list() { return R.ok(customerMapper.selectList(null)); }

    @GetMapping("/search")
    public R<?> search(@RequestParam String keyword) { return R.ok(demoService.searchCustomers(keyword)); }

    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) { return R.ok(demoService.getCustomerDetail(id)); }
}
