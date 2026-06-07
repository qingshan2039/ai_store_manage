package com.aistore.module.location.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 创建库位请求 DTO（对齐 CreateLocationRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLocationRequest {

    @NotNull(message = "所属仓库不能为空")
    private Long warehouseId;

    private Long zoneId;

    @NotBlank(message = "库位编码不能为空")
    @Size(min = 1, max = 32, message = "库位编码长度必须在1-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "库位编码只能包含字母、数字和连字符")
    private String code;

    @Size(max = 32, message = "库位类型长度不能超过32个字符")
    private String locType;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
