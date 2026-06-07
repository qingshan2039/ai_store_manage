package com.aistore.module.unitconversion.converter;

import com.aistore.module.unitconversion.dto.CreateUnitConversionRequest;
import com.aistore.module.unitconversion.dto.UpdateUnitConversionRequest;
import com.aistore.module.unitconversion.entity.UnitConversion;
import com.aistore.module.unitconversion.vo.UnitConversionSummaryVO;
import com.aistore.module.unitconversion.vo.UnitConversionVO;
import org.springframework.stereotype.Component;

/** 计量换算对象转换器；skuCode/skuName 由 Service 关联查询后传入 */
@Component
public class UnitConversionConverter {

    public UnitConversion toEntity(CreateUnitConversionRequest r) {
        return UnitConversion.builder()
                .skuId(r.getSkuId())
                .fromUnit(r.getFromUnit())
                .toUnit(r.getToUnit())
                .factor(r.getFactor())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public UnitConversionVO toVO(UnitConversion e, String skuCode, String skuName) {
        return UnitConversionVO.builder()
                .id(e.getId()).skuId(e.getSkuId()).skuCode(skuCode).skuName(skuName)
                .fromUnit(e.getFromUnit()).toUnit(e.getToUnit()).factor(e.getFactor())
                .status(e.getStatus()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public UnitConversionSummaryVO toSummaryVO(UnitConversion e, String skuName) {
        return UnitConversionSummaryVO.builder()
                .id(e.getId()).skuId(e.getSkuId()).skuName(skuName)
                .fromUnit(e.getFromUnit()).toUnit(e.getToUnit()).factor(e.getFactor())
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(UnitConversion e, UpdateUnitConversionRequest r) {
        if (r.getFactor() != null) e.setFactor(r.getFactor());
    }
}
