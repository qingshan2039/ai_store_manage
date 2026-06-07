package com.aistore.module.inventory.mapper;

import com.aistore.module.inventory.entity.Inventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 库存 Mapper */
@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    IPage<Inventory> selectInventoryPage(
            Page<Inventory> page,
            @Param("skuId") Long skuId,
            @Param("lpnId") Long lpnId,
            @Param("locationId") Long locationId
    );
}
