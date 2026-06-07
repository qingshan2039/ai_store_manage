package com.aistore.module.packaginglevel.converter;

import com.aistore.module.packaginglevel.dto.CreatePackagingLevelRequest;
import com.aistore.module.packaginglevel.dto.UpdatePackagingLevelRequest;
import com.aistore.module.packaginglevel.entity.PackagingLevel;
import com.aistore.module.packaginglevel.vo.PackagingLevelSummaryVO;
import com.aistore.module.packaginglevel.vo.PackagingLevelVO;
import org.springframework.stereotype.Component;

/** 包装层级对象转换器；skuCode/skuName 由 Service 关联查询后传入 */
@Component
public class PackagingLevelConverter {

    public PackagingLevel toEntity(CreatePackagingLevelRequest r) {
        return PackagingLevel.builder()
                .skuId(r.getSkuId())
                .levelName(r.getLevelName())
                .levelSeq(r.getLevelSeq())
                .unitCode(r.getUnitCode())
                .length(r.getLength()).width(r.getWidth()).height(r.getHeight())
                .netWeight(r.getNetWeight()).grossWeight(r.getGrossWeight())
                .isBaseUnit(r.getIsBaseUnit() != null ? r.getIsBaseUnit() : 0)
                .isSellable(r.getIsSellable() != null ? r.getIsSellable() : 0)
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public PackagingLevelVO toVO(PackagingLevel e, String skuCode, String skuName) {
        return PackagingLevelVO.builder()
                .id(e.getId()).skuId(e.getSkuId()).skuCode(skuCode).skuName(skuName)
                .levelName(e.getLevelName()).levelSeq(e.getLevelSeq()).unitCode(e.getUnitCode())
                .length(e.getLength()).width(e.getWidth()).height(e.getHeight())
                .netWeight(e.getNetWeight()).grossWeight(e.getGrossWeight())
                .isBaseUnit(e.getIsBaseUnit()).isSellable(e.getIsSellable()).status(e.getStatus())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public PackagingLevelSummaryVO toSummaryVO(PackagingLevel e, String skuName) {
        return PackagingLevelSummaryVO.builder()
                .id(e.getId()).skuId(e.getSkuId()).skuName(skuName)
                .levelName(e.getLevelName()).levelSeq(e.getLevelSeq()).unitCode(e.getUnitCode())
                .isBaseUnit(e.getIsBaseUnit()).isSellable(e.getIsSellable())
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(PackagingLevel e, UpdatePackagingLevelRequest r) {
        if (r.getLevelName() != null) e.setLevelName(r.getLevelName());
        if (r.getUnitCode() != null) e.setUnitCode(r.getUnitCode());
        if (r.getLength() != null) e.setLength(r.getLength());
        if (r.getWidth() != null) e.setWidth(r.getWidth());
        if (r.getHeight() != null) e.setHeight(r.getHeight());
        if (r.getNetWeight() != null) e.setNetWeight(r.getNetWeight());
        if (r.getGrossWeight() != null) e.setGrossWeight(r.getGrossWeight());
        if (r.getIsBaseUnit() != null) e.setIsBaseUnit(r.getIsBaseUnit());
        if (r.getIsSellable() != null) e.setIsSellable(r.getIsSellable());
    }
}
