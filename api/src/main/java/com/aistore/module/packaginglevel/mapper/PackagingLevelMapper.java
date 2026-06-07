package com.aistore.module.packaginglevel.mapper;

import com.aistore.module.packaginglevel.entity.PackagingLevel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 包装层级 Mapper */
@Mapper
public interface PackagingLevelMapper extends BaseMapper<PackagingLevel> {

    IPage<PackagingLevel> selectLevelPage(
            Page<PackagingLevel> page,
            @Param("skuId") Long skuId,
            @Param("status") Integer status
    );
}
