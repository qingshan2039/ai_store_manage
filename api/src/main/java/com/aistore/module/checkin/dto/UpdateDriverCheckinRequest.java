package com.aistore.module.checkin.dto;

import com.aistore.module.checkin.enums.CheckinStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 更新打卡记录请求 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverCheckinRequest {

    private Long driverUserId;

    @Size(max = 64, message = "司机替补名长度不能超过64个字符")
    private String driverOther;

    private Long vehicleId;

    private Long escortUserId;

    @Size(max = 64, message = "跟车员替补名长度不能超过64个字符")
    private String escortOther;

    private LocalDate checkinDate;

    private LocalDateTime clockInAt;

    private LocalDateTime clockOutAt;

    private CheckinStatus checkinStatus;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
