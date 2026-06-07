package com.aistore.module.checkin.dto;

import com.aistore.module.checkin.enums.CheckinStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** 打卡记录列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverCheckinQueryParam {

    private Long driverUserId;

    private Long vehicleId;

    private CheckinStatus checkinStatus;

    private LocalDate checkinDateStart;

    private LocalDate checkinDateEnd;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer pageSize = 20;
}
