package com.aistore.module.spu.mapper;

import com.aistore.module.spu.entity.Spu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** SPU Mapper */
@Mapper
public interface SpuMapper extends BaseMapper<Spu> {

    IPage<Spu> selectSpuPage(
            Page<Spu> page,
            @Param("keyword") String keyword,
            @Param("categoryCode") String categoryCode,
            @Param("status") Integer status
    );
}
