package com.aistore.module.category.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 更新物料品类请求 DTO（code 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMaterialCategoryRequest {

    @Size(min = 1, max = 64, message = "品类名称长度必须在1-64个字符之间")
    private String name;

    private Integer sortOrder;
}
