package com.aistore.module.barcode.mapper;

import com.aistore.module.barcode.entity.Barcode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 条码 Mapper */
@Mapper
public interface BarcodeMapper extends BaseMapper<Barcode> {

    IPage<Barcode> selectBarcodePage(
            Page<Barcode> page,
            @Param("keyword") String keyword,
            @Param("levelId") Long levelId,
            @Param("status") Integer status
    );
}
