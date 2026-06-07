package com.aistore.module.barcode.vo;

import com.aistore.module.barcode.enums.BarcodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 条码列表项 VO（对齐 BarcodeSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarcodeSummaryVO {
    private Long id;
    private Long levelId;
    private String levelName;
    private String barcode;
    private BarcodeType barcodeType;
    private Integer isPrimary;
    private Integer status;
    private LocalDateTime createdAt;
}
