package com.aistore.module.customer.mapper;

import com.aistore.module.customer.entity.CustomerShipAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 顾客送货地址 Mapper（基础 CRUD）
 */
@Mapper
public interface CustomerShipAddressMapper extends BaseMapper<CustomerShipAddress> {
}
