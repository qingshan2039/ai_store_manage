package com.aistore.module.spu.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 创建 SPU 请求 DTO（对齐 CreateSpuRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpuRequest {

    @NotBlank(message = "SPU 编码不能为空")
    @Size(min = 2, max = 32, message = "SPU 编码长度必须在2-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "SPU 编码只能包含字母、数字和连字符")
    private String spuCode;

    @NotBlank(message = "SPU 名称不能为空")
    @Size(min = 1, max = 128, message = "SPU 名称长度必须在1-128个字符之间")
    private String spuName;

    @NotBlank(message = "所属品类不能为空")
    @Size(min = 1, max = 32, message = "品类编码长度必须在1-32个字符之间")
    private String categoryCode;

    @Size(max = 64, message = "品牌长度不能超过64个字符")
    private String brand;

    @NotBlank(message = "基本单位不能为空")
    @Size(min = 1, max = 16, message = "基本单位长度必须在1-16个字符之间")
    private String baseUnit;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
