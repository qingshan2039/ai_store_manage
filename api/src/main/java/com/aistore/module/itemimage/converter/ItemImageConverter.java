package com.aistore.module.itemimage.converter;

import com.aistore.module.itemimage.dto.CreateItemImageRequest;
import com.aistore.module.itemimage.dto.UpdateItemImageRequest;
import com.aistore.module.itemimage.entity.ItemImage;
import com.aistore.module.itemimage.vo.ItemImageSummaryVO;
import com.aistore.module.itemimage.vo.ItemImageVO;
import org.springframework.stereotype.Component;

/** 物料图片对象转换器 */
@Component
public class ItemImageConverter {

    public ItemImage toEntity(CreateItemImageRequest r) {
        return ItemImage.builder()
                .spuId(r.getSpuId()).skuId(r.getSkuId()).levelId(r.getLevelId())
                .imageUrl(r.getImageUrl()).imageType(r.getImageType())
                .sortOrder(r.getSortOrder() != null ? r.getSortOrder() : 0)
                .isPrimary(r.getIsPrimary() != null ? r.getIsPrimary() : 0)
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public ItemImageVO toVO(ItemImage e) {
        return ItemImageVO.builder()
                .id(e.getId()).spuId(e.getSpuId()).skuId(e.getSkuId()).levelId(e.getLevelId())
                .imageUrl(e.getImageUrl()).imageType(e.getImageType())
                .sortOrder(e.getSortOrder()).isPrimary(e.getIsPrimary()).status(e.getStatus())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public ItemImageSummaryVO toSummaryVO(ItemImage e) {
        return ItemImageSummaryVO.builder()
                .id(e.getId()).spuId(e.getSpuId()).skuId(e.getSkuId()).levelId(e.getLevelId())
                .imageUrl(e.getImageUrl()).imageType(e.getImageType())
                .sortOrder(e.getSortOrder()).isPrimary(e.getIsPrimary()).status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(ItemImage e, UpdateItemImageRequest r) {
        if (r.getImageUrl() != null) e.setImageUrl(r.getImageUrl());
        if (r.getImageType() != null) e.setImageType(r.getImageType());
        if (r.getSortOrder() != null) e.setSortOrder(r.getSortOrder());
        if (r.getIsPrimary() != null) e.setIsPrimary(r.getIsPrimary());
    }
}
