package com.aistore.module.checkin.dto;

import com.aistore.module.checkin.enums.CheckinStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 创建打卡记录请求 DTO（司机/跟车员均可选 sys_user 或 OTHER 替补） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDriverCheckinRequest {

    /** 司机用户ID（与 driverOther 二选一） */
    private Long driverUserId;

    @Size(max = 64, message = "司机替补名长度不能超过64个字符")
    private String driverOther;

    private Long vehicleId;

    /** 跟车员用户ID（与 escortOther 二选一） */
    private Long escortUserId;

    @Size(max = 64, message = "跟车员替补名长度不能超过64个字符")
    private String escortOther;

    @NotNull(message = "打卡日期不能为空")
    private LocalDate checkinDate;

    private LocalDateTime clockInAt;

    private LocalDateTime clockOutAt;

    private CheckinStatus checkinStatus;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
