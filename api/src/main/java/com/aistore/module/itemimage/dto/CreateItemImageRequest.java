package com.aistore.module.itemimage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 创建物料图片请求 DTO（对齐 CreateItemImageRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemImageRequest {

    private Long spuId;
    private Long skuId;
    private Long levelId;

    @NotBlank(message = "图片地址不能为空")
    @Size(min = 1, max = 512, message = "图片地址长度必须在1-512个字符之间")
    private String imageUrl;

    @Size(max = 32, message = "图片类型长度不能超过32个字符")
    private String imageType;

    private Integer sortOrder;

    @Min(value = 0, message = "取值无效")
    @Max(value = 1, message = "取值无效")
    private Integer isPrimary;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
