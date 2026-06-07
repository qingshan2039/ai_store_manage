package com.aistore.module.zone.mapper;

import com.aistore.module.zone.entity.Zone;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 库区 Mapper */
@Mapper
public interface ZoneMapper extends BaseMapper<Zone> {

    IPage<Zone> selectZonePage(
            Page<Zone> page,
            @Param("keyword") String keyword,
            @Param("warehouseId") Long warehouseId,
            @Param("status") Integer status
    );
}
