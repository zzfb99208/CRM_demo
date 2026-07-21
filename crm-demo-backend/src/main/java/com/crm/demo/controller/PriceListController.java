package com.crm.demo.controller;

import com.crm.demo.common.R;
import com.crm.demo.entity.PriceList;
import com.crm.demo.entity.PriceListItem;
import com.crm.demo.service.PriceListService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/price-lists")
public class PriceListController {

    private final PriceListService priceListService;

    public PriceListController(PriceListService pls) { this.priceListService = pls; }

    @GetMapping
    public R<?> list(
            @RequestParam(required = false) String bizLevel1,
            @RequestParam(required = false) String bizLevel2,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String status) {
        return R.ok(priceListService.list(bizLevel1, bizLevel2, region, status));
    }

    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        Map<String, Object> m = priceListService.detail(id);
        return m != null ? R.ok(m) : R.fail("价格表不存在");
    }

    @PostMapping
    public R<?> create(@RequestBody PriceList pl) {
        return R.ok(priceListService.create(pl, null));
    }

    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody PriceList pl) {
        return R.ok(priceListService.updateHeader(id, pl));
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        priceListService.delete(id);
        return R.ok();
    }

    @PostMapping("/{id}/items")
    public R<?> addItem(@PathVariable Long id, @RequestBody PriceListItem item) {
        return R.ok(priceListService.addItem(id, item));
    }

    @PutMapping("/items/{itemId}")
    public R<?> updateItem(@PathVariable Long itemId, @RequestBody PriceListItem item) {
        return R.ok(priceListService.updateItem(itemId, item));
    }

    @DeleteMapping("/items/{itemId}")
    public R<?> deleteItem(@PathVariable Long itemId) {
        priceListService.deleteItem(itemId);
        return R.ok();
    }
}
