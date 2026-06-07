package com.aistore.module.spu.converter;

import com.aistore.module.spu.dto.CreateSpuRequest;
import com.aistore.module.spu.dto.UpdateSpuRequest;
import com.aistore.module.spu.entity.Spu;
import com.aistore.module.spu.vo.SpuSummaryVO;
import com.aistore.module.spu.vo.SpuVO;
import org.springframework.stereotype.Component;

/** SPU 对象转换器；categoryName 由 Service 关联查询后传入 */
@Component
public class SpuConverter {

    public Spu toEntity(CreateSpuRequest r) {
        return Spu.builder()
                .spuCode(r.getSpuCode())
                .spuName(r.getSpuName())
                .categoryCode(r.getCategoryCode())
                .brand(r.getBrand())
                .baseUnit(r.getBaseUnit())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public SpuVO toVO(Spu e, String categoryName) {
        return SpuVO.builder()
                .id(e.getId()).spuCode(e.getSpuCode()).spuName(e.getSpuName())
                .categoryCode(e.getCategoryCode()).categoryName(categoryName)
                .brand(e.getBrand()).baseUnit(e.getBaseUnit()).status(e.getStatus())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public SpuSummaryVO toSummaryVO(Spu e, String categoryName) {
        return SpuSummaryVO.builder()
                .id(e.getId()).spuCode(e.getSpuCode()).spuName(e.getSpuName())
                .categoryCode(e.getCategoryCode()).categoryName(categoryName)
                .baseUnit(e.getBaseUnit()).status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(Spu e, UpdateSpuRequest r) {
        if (r.getSpuName() != null) e.setSpuName(r.getSpuName());
        if (r.getCategoryCode() != null) e.setCategoryCode(r.getCategoryCode());
        if (r.getBrand() != null) e.setBrand(r.getBrand());
        if (r.getBaseUnit() != null) e.setBaseUnit(r.getBaseUnit());
    }
}
