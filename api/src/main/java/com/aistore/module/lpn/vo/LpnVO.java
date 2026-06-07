package com.aistore.module.lpn.vo;

import com.aistore.module.lpn.enums.LpnStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 托盘实例详情响应 VO（对齐 Lpn Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LpnVO {
    private Long id;
    private String lpnCode;
    private Long palletTypeId;
    private String palletTypeName;
    private Long warehouseId;
    private String warehouseName;
    private Long locationId;
    private String locationCode;
    private LpnStatus status;
    private BigDecimal grossWeight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
