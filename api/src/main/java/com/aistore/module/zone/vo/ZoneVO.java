package com.aistore.module.zone.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 库区详情响应 VO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneVO {
    private Long id;
    private Long warehouseId;
    /** 所属仓库名称（关联查询） */
    private String warehouseName;
    private String code;
    private String name;
    private String type;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
