package com.aistore.module.supplier.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 供应商详情响应 VO（对齐 Supplier Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierVO {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String contact;
    private String phone;
    private String email;
    private String remark;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
