package com.aistore.module.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 更新供应商请求 DTO（所有字段可选；code 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSupplierRequest {

    @Size(min = 2, max = 128, message = "供应商名称长度必须在2-128个字符之间")
    private String name;

    @Size(min = 2, max = 255, message = "地址长度必须在2-255个字符之间")
    private String address;

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
