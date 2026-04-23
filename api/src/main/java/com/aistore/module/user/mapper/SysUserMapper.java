package com.aistore.module.user.mapper;

import com.aistore.module.user.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 系统用户 Mapper 接口
 * 继承 MyBatis-Plus BaseMapper，获得基础 CRUD 能力
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 分页查询用户列表
     * 支持多条件筛选和关键词搜索
     *
     * @param page           分页参数
     * @param keyword        关键词（模糊匹配姓名、工号、手机号）
     * @param employeeNo     工号筛选
     * @param name           姓名筛选
     * @param phoneNumber    手机号筛选
     * @param status         状态筛选
     * @param departmentId   部门 ID 筛选
     * @param jobTitle       职位筛选
     * @param gender         性别筛选
     * @param createdAtStart 创建时间起始
     * @param createdAtEnd   创建时间截止
     * @return 分页结果
     */
    IPage<SysUser> selectUserPage(
            Page<SysUser> page,
            @Param("keyword") String keyword,
            @Param("employeeNo") String employeeNo,
            @Param("name") String name,
            @Param("phoneNumber") String phoneNumber,
            @Param("status") Integer status,
            @Param("departmentId") Long departmentId,
            @Param("jobTitle") String jobTitle,
            @Param("gender") Integer gender,
            @Param("createdAtStart") LocalDateTime createdAtStart,
            @Param("createdAtEnd") LocalDateTime createdAtEnd
    );
}
