package com.aistore.module.itemimage.mapper;

import com.aistore.module.itemimage.entity.ItemImage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 物料图片 Mapper */
@Mapper
public interface ItemImageMapper extends BaseMapper<ItemImage> {

    IPage<ItemImage> selectImagePage(
            Page<ItemImage> page,
            @Param("spuId") Long spuId,
            @Param("skuId") Long skuId,
            @Param("levelId") Long levelId,
            @Param("status") Integer status
    );
}
