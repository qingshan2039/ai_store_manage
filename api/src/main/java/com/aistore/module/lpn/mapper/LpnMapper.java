package com.aistore.module.lpn.mapper;

import com.aistore.module.lpn.entity.Lpn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 托盘实例 Mapper */
@Mapper
public interface LpnMapper extends BaseMapper<Lpn> {

    IPage<Lpn> selectLpnPage(
            Page<Lpn> page,
            @Param("keyword") String keyword,
            @Param("warehouseId") Long warehouseId,
            @Param("status") String status
    );
}
