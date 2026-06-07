package com.aistore.module.warehouse.dto;

import com.aistore.module.warehouse.enums.WarehouseType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 更新仓库请求 DTO（code 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWarehouseRequest {

    @Size(min = 2, max = 128, message = "仓库名称长度必须在2-128个字符之间")
    private String name;

    private WarehouseType type;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
