package com.aistore.module.vehicle.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 更新车辆请求 DTO（常态司机/跟车员整体覆盖，可清空以切换 用户 ↔ OTHER） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequest {

    @Size(min = 1, max = 32, message = "车牌号长度必须在1-32个字符之间")
    private String plateNo;

    private Long defaultDriverUserId;

    @Size(max = 64, message = "常态化司机替补名长度不能超过64个字符")
    private String defaultDriverOther;

    private Long defaultEscortUserId;

    @Size(max = 64, message = "常态化跟车员替补名长度不能超过64个字符")
    private String defaultEscortOther;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
