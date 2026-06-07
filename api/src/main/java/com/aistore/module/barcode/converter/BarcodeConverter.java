package com.aistore.module.barcode.converter;

import com.aistore.module.barcode.dto.CreateBarcodeRequest;
import com.aistore.module.barcode.dto.UpdateBarcodeRequest;
import com.aistore.module.barcode.entity.Barcode;
import com.aistore.module.barcode.enums.BarcodeType;
import com.aistore.module.barcode.vo.BarcodeSummaryVO;
import com.aistore.module.barcode.vo.BarcodeVO;
import org.springframework.stereotype.Component;

/** 条码对象转换器；levelName 由 Service 关联查询后传入。barcode_type 实体存枚举名。 */
@Component
public class BarcodeConverter {

    public Barcode toEntity(CreateBarcodeRequest r) {
        return Barcode.builder()
                .levelId(r.getLevelId())
                .barcode(r.getBarcode())
                .barcodeType(r.getBarcodeType().name())
                .isPrimary(r.getIsPrimary() != null ? r.getIsPrimary() : 0)
                .validFrom(r.getValidFrom())
                .validTo(r.getValidTo())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public BarcodeVO toVO(Barcode e, String levelName) {
        return BarcodeVO.builder()
                .id(e.getId()).levelId(e.getLevelId()).levelName(levelName)
                .barcode(e.getBarcode()).barcodeType(parseType(e.getBarcodeType()))
                .isPrimary(e.getIsPrimary()).validFrom(e.getValidFrom()).validTo(e.getValidTo())
                .status(e.getStatus()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public BarcodeSummaryVO toSummaryVO(Barcode e, String levelName) {
        return BarcodeSummaryVO.builder()
                .id(e.getId()).levelId(e.getLevelId()).levelName(levelName)
                .barcode(e.getBarcode()).barcodeType(parseType(e.getBarcodeType()))
                .isPrimary(e.getIsPrimary()).status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(Barcode e, UpdateBarcodeRequest r) {
        if (r.getBarcodeType() != null) e.setBarcodeType(r.getBarcodeType().name());
        if (r.getIsPrimary() != null) e.setIsPrimary(r.getIsPrimary());
        if (r.getValidFrom() != null) e.setValidFrom(r.getValidFrom());
        if (r.getValidTo() != null) e.setValidTo(r.getValidTo());
    }

    private BarcodeType parseType(String v) {
        return v != null ? BarcodeType.valueOf(v) : null;
    }
}
