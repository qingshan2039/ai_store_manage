package com.aistore.module.warehouse.vo;

import com.aistore.module.warehouse.enums.WarehouseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 仓库详情响应 VO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseVO {
    private Long id;
    private String code;
    private String name;
    private WarehouseType type;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
