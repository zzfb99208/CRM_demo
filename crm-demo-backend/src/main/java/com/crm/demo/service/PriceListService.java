package com.crm.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.demo.entity.PriceList;
import com.crm.demo.entity.PriceListItem;
import com.crm.demo.mapper.PriceListMapper;
import com.crm.demo.mapper.PriceListItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceListService {

    private final PriceListMapper priceListMapper;
    private final PriceListItemMapper priceListItemMapper;

    public PriceListService(PriceListMapper plm, PriceListItemMapper plim) {
        this.priceListMapper = plm; this.priceListItemMapper = plim;
    }

    public List<PriceList> list(String bizLevel1, String bizLevel2, String region, String status) {
        LambdaQueryWrapper<PriceList> q = new LambdaQueryWrapper<>();
        if (bizLevel1 != null && !bizLevel1.isEmpty()) q.eq(PriceList::getBizLevel1Tag, bizLevel1);
        if (bizLevel2 != null && !bizLevel2.isEmpty()) q.eq(PriceList::getBizLevel2Tag, bizLevel2);
        if (region != null && !region.isEmpty()) q.eq(PriceList::getRegion, region);
        if (status != null && !status.isEmpty()) q.eq(PriceList::getStatus, status);
        else q.eq(PriceList::getStatus, "启用");
        q.orderByDesc(PriceList::getCreatedAt);
        return priceListMapper.selectList(q);
    }

    public Map<String, Object> detail(Long id) {
        PriceList pl = priceListMapper.selectById(id);
        if (pl == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("header", pl);
        m.put("items", priceListItemMapper.selectList(
            new LambdaQueryWrapper<PriceListItem>().eq(PriceListItem::getPriceListId, id)));
        return m;
    }

    @Transactional
    public PriceList create(PriceList pl, List<PriceListItem> items) {
        priceListMapper.insert(pl);
        if (items != null) {
            for (PriceListItem item : items) {
                item.setPriceListId(pl.getId());
                priceListItemMapper.insert(item);
            }
        }
        return pl;
    }

    @Transactional
    public PriceList updateHeader(Long id, PriceList pl) {
        pl.setId(id);
        priceListMapper.updateById(pl);
        return priceListMapper.selectById(id);
    }

    @Transactional
    public void delete(Long id) {
        priceListItemMapper.delete(new LambdaQueryWrapper<PriceListItem>().eq(PriceListItem::getPriceListId, id));
        priceListMapper.deleteById(id);
    }

    @Transactional
    public PriceListItem addItem(Long priceListId, PriceListItem item) {
        item.setPriceListId(priceListId);
        priceListItemMapper.insert(item);
        return item;
    }

    @Transactional
    public PriceListItem updateItem(Long itemId, PriceListItem item) {
        item.setId(itemId);
        priceListItemMapper.updateById(item);
        return priceListItemMapper.selectById(itemId);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        priceListItemMapper.deleteById(itemId);
    }
}
