package com.aistore.module.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户详情响应 VO
 * 严格对齐 OpenAPI 契约 User Schema
 * 注意：password 和 deleted 永远不出现在响应中
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    /**
     * 用户 ID
     */
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
     * 所属部门名称（关联查询，前端展示用）
     */
    private String departmentName;

    /**
     * 是否隐藏手机号
     */
    private Boolean hidePhoneNumber;

    /**
     * 是否隐藏姓名
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
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建人 ID
     */
    private Long createdBy;

    /**
     * 更新人 ID
     */
    private Long updatedBy;
}
