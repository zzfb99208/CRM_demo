package com.crm.demo.controller;

import com.crm.demo.common.R;
import com.crm.demo.service.DemoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final DemoService demoService;
    public ProductController(DemoService ds) { this.demoService = ds; }

    @GetMapping
    public R<?> list(@RequestParam(required = false) String keyword,
                     @RequestParam(required = false) String category) {
        return R.ok(demoService.getProducts(keyword, category));
    }
}
