package com.aistore.module.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统用户实体
 * 对应数据库表 sys_user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class SysUser {

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * HR 分配的工号
     */
    private String employeeNo;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 登录密码（BCrypt 加密存储）
     */
    private String password;

    /**
     * 员工真实姓名
     */
    private String name;

    /**
     * 系统内显示名称
     */
    private String nickname;

    /**
     * 头像图片 URL
     */
    private String avatar;

    /**
     * 性别：0=未知，1=男，2=女
     */
    private Integer gender;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 企业邮箱
     */
    private String email;

    /**
     * 职位名称
     */
    private String jobTitle;

    /**
     * 所属部门 ID
     */
    private Long departmentId;

    /**
     * 是否隐藏手机号：false=否，true=是
     */
    private Boolean hidePhoneNumber;

    /**
     * 是否隐藏姓名：false=否，true=是
     */
    private Boolean hideName;

    /**
     * 管理员备注
     */
    private String remark;

    /**
     * 账号状态：0=禁用，1=启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 创建人 ID
     */
    private Long createdBy;

    /**
     * 更新人 ID
     */
    private Long updatedBy;

    /**
     * 逻辑删除：0=未删除，1=已删除
     */
    @TableLogic
    private Integer deleted;
}
