package com.aistore.module.customer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 送货地址响应 VO
 * 严格对齐 OpenAPI 契约 ShipAddress Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipAddressVO {

    private Long id;
    private String address;
    private String remark;
}
