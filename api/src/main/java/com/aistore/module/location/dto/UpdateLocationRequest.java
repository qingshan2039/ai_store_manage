package com.aistore.module.location.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 更新库位请求 DTO（warehouse_id/code 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationRequest {

    private Long zoneId;

    @Size(max = 32, message = "库位类型长度不能超过32个字符")
    private String locType;
}
