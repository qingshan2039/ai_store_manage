package com.aistore.module.lpn.dto;

import com.aistore.module.lpn.enums.LpnStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 创建托盘实例请求 DTO（对齐 CreateLpnRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLpnRequest {

    @NotBlank(message = "托盘号不能为空")
    @Size(min = 1, max = 64, message = "托盘号长度必须在1-64个字符之间")
    private String lpnCode;

    @NotNull(message = "托盘类型不能为空")
    private Long palletTypeId;

    @NotNull(message = "所属仓库不能为空")
    private Long warehouseId;

    private Long locationId;

    private LpnStatus status;

    @PositiveOrZero(message = "总毛重不能为负")
    private BigDecimal grossWeight;
}
