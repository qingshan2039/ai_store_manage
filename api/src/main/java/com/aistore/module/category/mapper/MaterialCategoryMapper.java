package com.aistore.module.category.mapper;

import com.aistore.module.category.entity.MaterialCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 物料品类 Mapper */
@Mapper
public interface MaterialCategoryMapper extends BaseMapper<MaterialCategory> {

    IPage<MaterialCategory> selectCategoryPage(
            Page<MaterialCategory> page,
            @Param("keyword") String keyword,
            @Param("status") Integer status
    );
}
