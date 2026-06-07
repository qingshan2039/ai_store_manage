package com.aistore.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用状态变更请求 DTO（0=禁用，1=启用）
 * 供应商/仓库/库区/托盘类型等模块的 PATCH .../status 共用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值无效，必须为0或1")
    @Max(value = 1, message = "状态值无效，必须为0或1")
    private Integer status;
}
