package com.aistore.module.category.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 创建物料品类请求 DTO（对齐 CreateMaterialCategoryRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMaterialCategoryRequest {

    @NotBlank(message = "品类编码不能为空")
    @Size(min = 2, max = 32, message = "品类编码长度必须在2-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "品类编码只能包含字母、数字和连字符")
    private String code;

    @NotBlank(message = "品类名称不能为空")
    @Size(min = 1, max = 64, message = "品类名称长度必须在1-64个字符之间")
    private String name;

    private Integer sortOrder;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
