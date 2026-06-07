package com.aistore.module.warehouse.mapper;

import com.aistore.module.warehouse.entity.Warehouse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 仓库 Mapper */
@Mapper
public interface WarehouseMapper extends BaseMapper<Warehouse> {

    IPage<Warehouse> selectWarehousePage(
            Page<Warehouse> page,
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("status") Integer status
    );
}
