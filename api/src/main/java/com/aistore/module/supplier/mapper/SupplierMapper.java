package com.aistore.module.supplier.mapper;

import com.aistore.module.supplier.entity.Supplier;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 供应商 Mapper */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {

    IPage<Supplier> selectSupplierPage(
            Page<Supplier> page,
            @Param("keyword") String keyword,
            @Param("status") Integer status
    );
}
