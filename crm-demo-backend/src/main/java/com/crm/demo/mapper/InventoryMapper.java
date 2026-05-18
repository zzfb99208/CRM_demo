package com.crm.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.demo.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {}
