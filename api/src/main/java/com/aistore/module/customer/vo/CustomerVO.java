package com.aistore.module.customer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 顾客详情响应 VO
 * 严格对齐 OpenAPI 契约 Customer Schema；deleted 永不出现在响应中
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVO {

    private Long id;
    private String code;
    private String name;
    private String address;
    /** 收/发货地址列表（连锁客户可有多个） */
    private List<ShipAddressVO> shipAddresses;
    private String contact;
    private String phone;
    private String email;
    private String remark;
    /** 状态：0=禁用，1=启用 */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
