package com.aistore.module.lpn.vo;

import com.aistore.module.lpn.enums.LpnStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 托盘实例列表项 VO（对齐 LpnSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LpnSummaryVO {
    private Long id;
    private String lpnCode;
    private String palletTypeName;
    private String warehouseName;
    private String locationCode;
    private LpnStatus status;
    private LocalDateTime createdAt;
}
