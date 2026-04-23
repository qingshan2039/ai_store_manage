package com.aistore.module.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户请求 DTO
 * 严格对齐 OpenAPI 契约 UpdateUserRequest Schema
 * 所有字段均为可选，仅传入需要修改的字段
 * 不含 employeeNo、username（创建后不可修改）和 password（独立接口重置）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    /**
     * 员工真实姓名
     */
    @Size(min = 2, max = 64, message = "姓名长度必须在2-64个字符之间")
    private String name;

    /**
     * 系统内显示名称
     */
    @Size(max = 64, message = "昵称长度不能超过64个字符")
    private String nickname;

    /**
     * 头像图片 URL
     */
    @Size(max = 512, message = "头像URL长度不能超过512个字符")
    private String avatar;

    /**
     * 性别：0=未知，1=男，2=女
     */
    @Min(value = 0, message = "性别值无效")
    @Max(value = 2, message = "性别值无效")
    private Integer gender;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phoneNumber;

    /**
     * 企业邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128个字符")
    private String email;

    /**
     * 职位名称
     */
    @Size(max = 64, message = "职位名称长度不能超过64个字符")
    private String jobTitle;

    /**
     * 所属部门 ID
     */
    private Long departmentId;

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
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
