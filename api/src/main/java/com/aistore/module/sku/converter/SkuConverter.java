package com.aistore.module.sku.converter;

import com.aistore.module.sku.dto.CreateSkuRequest;
import com.aistore.module.sku.dto.UpdateSkuRequest;
import com.aistore.module.sku.entity.Sku;
import com.aistore.module.sku.enums.ItemType;
import com.aistore.module.sku.vo.SkuSummaryVO;
import com.aistore.module.sku.vo.SkuVO;
import org.springframework.stereotype.Component;

/** SKU 对象转换器；spuCode/spuName 由 Service 关联查询后传入。item_type 实体存枚举名。 */
@Component
public class SkuConverter {

    public Sku toEntity(CreateSkuRequest r) {
        return Sku.builder()
                .spuId(r.getSpuId())
                .skuCode(r.getSkuCode())
                .skuName(r.getSkuName())
                .itemType(r.getItemType().name())
                .lengthMm(r.getLengthMm())
                .widthMm(r.getWidthMm())
                .thicknessMm(r.getThicknessMm())
                .rollLengthM(r.getRollLengthM())
                .color(r.getColor())
                .gsm(r.getGsm())
                .spec(r.getSpec())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public SkuVO toVO(Sku e, String spuCode, String spuName) {
        return SkuVO.builder()
                .id(e.getId()).spuId(e.getSpuId()).spuCode(spuCode).spuName(spuName)
                .skuCode(e.getSkuCode()).skuName(e.getSkuName())
                .itemType(parseItemType(e.getItemType()))
                .lengthMm(e.getLengthMm()).widthMm(e.getWidthMm())
                .thicknessMm(e.getThicknessMm()).rollLengthM(e.getRollLengthM())
                .color(e.getColor()).gsm(e.getGsm()).spec(e.getSpec())
                .status(e.getStatus()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public SkuSummaryVO toSummaryVO(Sku e, String spuName) {
        return SkuSummaryVO.builder()
                .id(e.getId()).spuId(e.getSpuId()).spuName(spuName)
                .skuCode(e.getSkuCode()).skuName(e.getSkuName())
                .itemType(parseItemType(e.getItemType()))
                .lengthMm(e.getLengthMm()).widthMm(e.getWidthMm())
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(Sku e, UpdateSkuRequest r) {
        if (r.getSkuName() != null) e.setSkuName(r.getSkuName());
        if (r.getItemType() != null) e.setItemType(r.getItemType().name());
        if (r.getLengthMm() != null) e.setLengthMm(r.getLengthMm());
        if (r.getWidthMm() != null) e.setWidthMm(r.getWidthMm());
        if (r.getThicknessMm() != null) e.setThicknessMm(r.getThicknessMm());
        if (r.getRollLengthM() != null) e.setRollLengthM(r.getRollLengthM());
        if (r.getColor() != null) e.setColor(r.getColor());
        if (r.getGsm() != null) e.setGsm(r.getGsm());
        if (r.getSpec() != null) e.setSpec(r.getSpec());
    }

    private ItemType parseItemType(String v) {
        return v != null ? ItemType.valueOf(v) : null;
    }
}
