package com.aistore.module.barcode.dto;

import com.aistore.module.barcode.enums.BarcodeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** 更新条码请求 DTO（level_id/barcode 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBarcodeRequest {

    private BarcodeType barcodeType;

    @Min(value = 0, message = "取值无效")
    @Max(value = 1, message = "取值无效")
    private Integer isPrimary;

    private LocalDate validFrom;
    private LocalDate validTo;
}
