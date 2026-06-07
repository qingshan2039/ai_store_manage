package com.aistore.module.spu.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 更新 SPU 请求 DTO（spu_code 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSpuRequest {

    @Size(min = 1, max = 128, message = "SPU 名称长度必须在1-128个字符之间")
    private String spuName;

    @Size(min = 1, max = 32, message = "品类编码长度必须在1-32个字符之间")
    private String categoryCode;

    @Size(max = 64, message = "品牌长度不能超过64个字符")
    private String brand;

    @Size(min = 1, max = 16, message = "基本单位长度必须在1-16个字符之间")
    private String baseUnit;
}
