package com.aistore.module.packagingrelation.mapper;

import com.aistore.module.packagingrelation.entity.PackagingRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 包装关系 Mapper */
@Mapper
public interface PackagingRelationMapper extends BaseMapper<PackagingRelation> {

    IPage<PackagingRelation> selectRelationPage(
            Page<PackagingRelation> page,
            @Param("parentLevelId") Long parentLevelId,
            @Param("status") Integer status
    );
}
