package com.aistore.module.itemimage.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 物料图片列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemImageListResponse {
    private List<ItemImageSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
