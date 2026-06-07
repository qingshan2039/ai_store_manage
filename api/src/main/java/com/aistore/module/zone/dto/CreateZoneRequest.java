package com.aistore.module.zone.dto;

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

/** 创建库区请求 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateZoneRequest {

    @NotNull(message = "所属仓库不能为空")
    private Long warehouseId;

    @NotBlank(message = "库区编码不能为空")
    @Size(min = 1, max = 32, message = "库区编码长度必须在1-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "库区编码只能包含字母、数字和连字符")
    private String code;

    @NotBlank(message = "库区名称不能为空")
    @Size(min = 1, max = 64, message = "库区名称长度必须在1-64个字符之间")
    private String name;

    @Size(max = 32, message = "类型长度不能超过32个字符")
    private String type;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
