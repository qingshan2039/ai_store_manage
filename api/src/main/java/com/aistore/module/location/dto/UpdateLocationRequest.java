package com.aistore.module.location.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 更新库位请求 DTO（warehouse_id 不可改；code 可改，仓库内唯一；状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationRequest {

    @Size(min = 1, max = 32, message = "库位编码长度必须在1-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "库位编码只能包含字母、数字和连字符")
    private String code;

    private Long zoneId;

    @Size(max = 32, message = "库位类型长度不能超过32个字符")
    private String locType;
}
