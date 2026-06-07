package com.aistore.module.warehouse.vo;

import com.aistore.module.warehouse.enums.WarehouseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 仓库列表项 VO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseSummaryVO {
    private Long id;
    private String code;
    private String name;
    private WarehouseType type;
    private Integer status;
    private LocalDateTime createdAt;
}
