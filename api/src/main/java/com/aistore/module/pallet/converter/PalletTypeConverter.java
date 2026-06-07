package com.aistore.module.pallet.converter;

import com.aistore.module.pallet.dto.CreatePalletTypeRequest;
import com.aistore.module.pallet.dto.UpdatePalletTypeRequest;
import com.aistore.module.pallet.entity.PalletType;
import com.aistore.module.pallet.vo.PalletTypeSummaryVO;
import com.aistore.module.pallet.vo.PalletTypeVO;
import org.springframework.stereotype.Component;

/** 托盘类型对象转换器 */
@Component
public class PalletTypeConverter {

    public PalletType toEntity(CreatePalletTypeRequest r) {
        return PalletType.builder()
                .code(r.getCode())
                .name(r.getName())
                .length(r.getLength())
                .width(r.getWidth())
                .tareWeight(r.getTareWeight())
                .maxLoad(r.getMaxLoad())
                .maxStack(r.getMaxStack())
                .remark(r.getRemark())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public PalletTypeVO toVO(PalletType e) {
        return PalletTypeVO.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName())
                .length(e.getLength()).width(e.getWidth())
                .tareWeight(e.getTareWeight()).maxLoad(e.getMaxLoad()).maxStack(e.getMaxStack())
                .remark(e.getRemark()).status(e.getStatus())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public PalletTypeSummaryVO toSummaryVO(PalletType e) {
        return PalletTypeSummaryVO.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName())
                .length(e.getLength()).width(e.getWidth())
                .maxLoad(e.getMaxLoad()).maxStack(e.getMaxStack())
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(PalletType e, UpdatePalletTypeRequest r) {
        if (r.getName() != null) e.setName(r.getName());
        if (r.getLength() != null) e.setLength(r.getLength());
        if (r.getWidth() != null) e.setWidth(r.getWidth());
        if (r.getTareWeight() != null) e.setTareWeight(r.getTareWeight());
        if (r.getMaxLoad() != null) e.setMaxLoad(r.getMaxLoad());
        if (r.getMaxStack() != null) e.setMaxStack(r.getMaxStack());
        if (r.getRemark() != null) e.setRemark(r.getRemark());
    }
}
