package com.aistore.module.department.mapper;

import com.aistore.module.department.entity.SysDepartment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 部门 Mapper 接口
 * 继承 MyBatis-Plus BaseMapper，获得基础 CRUD 能力
 */
@Mapper
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {

    /**
     * 分页查询部门列表
     * 支持关键词搜索、类型与状态筛选
     *
     * @param page    分页参数
     * @param keyword 关键词（模糊匹配名称、编码）
     * @param type    类型筛选（DepartmentType 枚举名）
     * @param status  状态筛选
     * @return 分页结果
     */
    IPage<SysDepartment> selectDepartmentPage(
            Page<SysDepartment> page,
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("status") Integer status
    );
}
