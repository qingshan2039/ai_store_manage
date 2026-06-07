package com.aistore.module.supplier.dto;

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

/** 创建供应商请求 DTO（对齐 CreateSupplierRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupplierRequest {

    @NotBlank(message = "供应商编码不能为空")
    @Size(min = 2, max = 32, message = "供应商编码长度必须在2-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "供应商编码只能包含字母、数字和连字符")
    private String code;

    @NotBlank(message = "供应商名称不能为空")
    @Size(min = 2, max = 128, message = "供应商名称长度必须在2-128个字符之间")
    private String name;

    @NotBlank(message = "地址不能为空")
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

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
