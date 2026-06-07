package com.aistore.module.barcode.vo;

import com.aistore.module.barcode.enums.BarcodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 条码详情响应 VO（对齐 Barcode Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarcodeVO {
    private Long id;
    private Long levelId;
    private String levelName;
    private String barcode;
    private BarcodeType barcodeType;
    private Integer isPrimary;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
