package com.aistore.module.checkin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 打卡记录列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverCheckinListResponse {
    private List<DriverCheckinSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
