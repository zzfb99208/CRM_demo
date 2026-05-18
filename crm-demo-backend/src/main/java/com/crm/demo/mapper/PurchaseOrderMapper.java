package com.crm.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.demo.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {}
