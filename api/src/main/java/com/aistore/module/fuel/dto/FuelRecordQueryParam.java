package com.aistore.module.fuel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** 打油记录列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelRecordQueryParam {

    private Long vehicleId;

    private LocalDate fuelDateStart;

    private LocalDate fuelDateEnd;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer pageSize = 20;
}
