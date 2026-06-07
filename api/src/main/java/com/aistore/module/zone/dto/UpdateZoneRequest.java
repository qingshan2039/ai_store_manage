package com.aistore.module.zone.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 更新库区请求 DTO（warehouseId/code 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateZoneRequest {

    @Size(min = 1, max = 64, message = "库区名称长度必须在1-64个字符之间")
    private String name;

    @Size(max = 32, message = "类型长度不能超过32个字符")
    private String type;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
