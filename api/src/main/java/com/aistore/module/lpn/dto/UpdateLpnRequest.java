package com.aistore.module.lpn.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 更新托盘实例请求 DTO（lpn_code/类型/仓库不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLpnRequest {

    private Long locationId;

    @PositiveOrZero(message = "总毛重不能为负")
    private BigDecimal grossWeight;
}
