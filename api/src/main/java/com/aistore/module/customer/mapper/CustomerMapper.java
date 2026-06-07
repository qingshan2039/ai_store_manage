package com.aistore.module.customer.mapper;

import com.aistore.module.customer.entity.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 顾客 Mapper 接口
 * 继承 MyBatis-Plus BaseMapper，获得基础 CRUD 能力
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    /**
     * 分页查询顾客列表（关键词 + 状态筛选）
     *
     * @param page    分页参数
     * @param keyword 关键词（模糊匹配名称、编码、联系人）
     * @param status  状态筛选
     * @return 分页结果
     */
    IPage<Customer> selectCustomerPage(
            Page<Customer> page,
            @Param("keyword") String keyword,
            @Param("status") Integer status
    );
}
