package com.aistore.module.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建用户请求 DTO
 * 严格对齐 OpenAPI 契约 CreateUserRequest Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    /**
     * HR 分配的工号（创建后不可修改）
     */
    @NotBlank(message = "工号不能为空")
    @Size(min = 2, max = 32, message = "工号长度必须在2-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "工号只能包含字母、数字和连字符")
    private String employeeNo;

    /**
     * 登录账号（创建后不可修改）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 64, message = "用户名长度必须在4-64个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 登录密码（8~32位，至少含字母和数字）
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度必须在8-32个字符之间")
    private String password;

    /**
     * 员工真实姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 64, message = "姓名长度必须在2-64个字符之间")
    private String name;

    /**
     * 系统内显示名称
     */
    @Size(max = 64, message = "昵称长度不能超过64个字符")
    private String nickname;

    /**
     * 性别：0=未知，1=男，2=女
     */
    @Min(value = 0, message = "性别值无效")
    @Max(value = 2, message = "性别值无效")
    private Integer gender;

    /**
     * 手机号码
     */
    @NotBlank(message = "手机号码不能为空")
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

    /**
     * 账号状态：0=禁用，1=启用
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
