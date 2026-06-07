package com.aistore.module.customer.dto;

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
 * 创建顾客请求 DTO
 * 严格对齐 OpenAPI 契约 CreateCustomerRequest Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    /**
     * 客户编码（创建后不可修改，全局唯一）
     */
    @NotBlank(message = "客户编码不能为空")
    @Size(min = 2, max = 32, message = "客户编码长度必须在2-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "客户编码只能包含字母、数字和连字符")
    private String code;

    /**
     * 客户公司名称（全局唯一）
     */
    @NotBlank(message = "客户公司名称不能为空")
    @Size(min = 2, max = 128, message = "客户公司名称长度必须在2-128个字符之间")
    private String name;

    /**
     * 客户公司地址
     */
    @NotBlank(message = "客户公司地址不能为空")
    @Size(min = 2, max = 255, message = "客户公司地址长度必须在2-255个字符之间")
    private String address;

    /**
     * 收/发货地址（ship-to）
     */
    @NotBlank(message = "收/发货地址不能为空")
    @Size(min = 2, max = 255, message = "收/发货地址长度必须在2-255个字符之间")
    private String shipAddress;

    /**
     * 联系人
     */
    @Size(max = 64, message = "联系人长度不能超过64个字符")
    private String contact;

    /**
     * 联系电话
     */
    @Size(max = 32, message = "联系电话长度不能超过32个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128个字符")
    private String email;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 状态：0=禁用，1=启用
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
