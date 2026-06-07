package com.aistore.module.unitconversion.mapper;

import com.aistore.module.unitconversion.entity.UnitConversion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 计量换算 Mapper */
@Mapper
public interface UnitConversionMapper extends BaseMapper<UnitConversion> {

    IPage<UnitConversion> selectConversionPage(
            Page<UnitConversion> page,
            @Param("skuId") Long skuId,
            @Param("status") Integer status
    );
}
