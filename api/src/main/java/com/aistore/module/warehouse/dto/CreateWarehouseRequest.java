package com.aistore.module.warehouse.dto;

import com.aistore.module.warehouse.enums.WarehouseType;
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

/** 创建仓库请求 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarehouseRequest {

    @NotBlank(message = "仓库编码不能为空")
    @Size(min = 2, max = 32, message = "仓库编码长度必须在2-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "仓库编码只能包含字母、数字和连字符")
    private String code;

    @NotBlank(message = "仓库名称不能为空")
    @Size(min = 2, max = 128, message = "仓库名称长度必须在2-128个字符之间")
    private String name;

    @NotNull(message = "仓库类型不能为空")
    private WarehouseType type;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
