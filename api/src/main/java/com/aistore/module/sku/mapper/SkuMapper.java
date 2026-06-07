package com.aistore.module.sku.mapper;

import com.aistore.module.sku.entity.Sku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** SKU Mapper */
@Mapper
public interface SkuMapper extends BaseMapper<Sku> {

    IPage<Sku> selectSkuPage(
            Page<Sku> page,
            @Param("keyword") String keyword,
            @Param("spuId") Long spuId,
            @Param("itemType") String itemType,
            @Param("status") Integer status
    );
}
