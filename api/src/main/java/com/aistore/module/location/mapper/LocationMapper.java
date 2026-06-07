package com.aistore.module.location.mapper;

import com.aistore.module.location.entity.Location;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 库位 Mapper */
@Mapper
public interface LocationMapper extends BaseMapper<Location> {

    IPage<Location> selectLocationPage(
            Page<Location> page,
            @Param("keyword") String keyword,
            @Param("warehouseId") Long warehouseId,
            @Param("zoneId") Long zoneId,
            @Param("status") Integer status
    );
}
