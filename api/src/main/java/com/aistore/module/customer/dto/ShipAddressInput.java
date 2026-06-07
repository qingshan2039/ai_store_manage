package com.aistore.module.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 送货地址输入 DTO（创建/更新顾客时的 shipAddresses 元素）
 * 严格对齐 OpenAPI 契约 ShipAddressInput Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipAddressInput {

    /**
     * 收/发货地址
     */
    @NotBlank(message = "收/发货地址不能为空")
    @Size(min = 2, max = 255, message = "收/发货地址长度必须在2-255个字符之间")
    private String address;

    /**
     * 送货地址备注（如客户报错地址后填写的修正说明）
     */
    @Size(max = 255, message = "送货地址备注长度不能超过255个字符")
    private String remark;
}
