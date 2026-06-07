package com.aistore.module.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更新顾客请求 DTO
 * 严格对齐 OpenAPI 契约 UpdateCustomerRequest Schema
 * 所有字段均为可选；code 创建后不可修改，状态通过独立接口变更
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {

    @Size(min = 2, max = 128, message = "客户公司名称长度必须在2-128个字符之间")
    private String name;

    @Size(min = 2, max = 255, message = "客户公司地址长度必须在2-255个字符之间")
    private String address;

    /**
     * 收/发货地址列表（提供则整列表替换，至少 1 个；不传表示不修改）
     */
    @Size(min = 1, message = "提供收/发货地址时至少需要一个")
    @Valid
    private List<ShipAddressInput> shipAddresses;

    @Size(max = 64, message = "联系人长度不能超过64个字符")
    private String contact;

    @Size(max = 32, message = "联系电话长度不能超过32个字符")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128个字符")
    private String email;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
