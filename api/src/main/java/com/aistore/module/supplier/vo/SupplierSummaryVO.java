package com.aistore.module.supplier.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 供应商列表项 VO（对齐 SupplierSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierSummaryVO {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String contact;
    private String phone;
    private Integer status;
    private LocalDateTime createdAt;
}
