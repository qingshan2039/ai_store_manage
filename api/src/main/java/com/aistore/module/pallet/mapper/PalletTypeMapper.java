package com.aistore.module.pallet.mapper;

import com.aistore.module.pallet.entity.PalletType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 托盘类型 Mapper */
@Mapper
public interface PalletTypeMapper extends BaseMapper<PalletType> {

    IPage<PalletType> selectPalletTypePage(
            Page<PalletType> page,
            @Param("keyword") String keyword,
            @Param("status") Integer status
    );
}
