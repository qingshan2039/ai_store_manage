package com.aistore.module.vehicle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 创建车辆请求 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehicleRequest {

    @NotBlank(message = "车牌号不能为空")
    @Size(min = 1, max = 32, message = "车牌号长度必须在1-32个字符之间")
    private String plateNo;

    /** 常态化司机用户ID（与 defaultDriverOther 二选一） */
    private Long defaultDriverUserId;

    @Size(max = 64, message = "常态化司机替补名长度不能超过64个字符")
    private String defaultDriverOther;

    /** 常态化跟车员用户ID（与 defaultEscortOther 二选一） */
    private Long defaultEscortUserId;

    @Size(max = 64, message = "常态化跟车员替补名长度不能超过64个字符")
    private String defaultEscortOther;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
