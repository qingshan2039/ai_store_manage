package com.aistore.module.barcode.dto;

import com.aistore.module.barcode.enums.BarcodeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** 创建条码请求 DTO（对齐 CreateBarcodeRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBarcodeRequest {

    @NotNull(message = "所属包装层不能为空")
    private Long levelId;

    @NotBlank(message = "条码不能为空")
    @Size(min = 1, max = 64, message = "条码长度必须在1-64个字符之间")
    private String barcode;

    @NotNull(message = "条码类型不能为空")
    private BarcodeType barcodeType;

    @Min(value = 0, message = "取值无效")
    @Max(value = 1, message = "取值无效")
    private Integer isPrimary;

    private LocalDate validFrom;
    private LocalDate validTo;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
