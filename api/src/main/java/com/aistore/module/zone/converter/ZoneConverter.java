package com.aistore.module.zone.converter;

import com.aistore.module.zone.dto.CreateZoneRequest;
import com.aistore.module.zone.dto.UpdateZoneRequest;
import com.aistore.module.zone.entity.Zone;
import com.aistore.module.zone.vo.ZoneSummaryVO;
import com.aistore.module.zone.vo.ZoneVO;
import org.springframework.stereotype.Component;

/** 库区对象转换器；warehouseName 由 Service 关联查询后传入 */
@Component
public class ZoneConverter {

    public Zone toEntity(CreateZoneRequest r) {
        return Zone.builder()
                .warehouseId(r.getWarehouseId())
                .code(r.getCode())
                .name(r.getName())
                .type(r.getType())
                .remark(r.getRemark())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public ZoneVO toVO(Zone e, String warehouseName) {
        return ZoneVO.builder()
                .id(e.getId()).warehouseId(e.getWarehouseId()).warehouseName(warehouseName)
                .code(e.getCode()).name(e.getName()).type(e.getType())
                .status(e.getStatus()).remark(e.getRemark())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public ZoneSummaryVO toSummaryVO(Zone e, String warehouseName) {
        return ZoneSummaryVO.builder()
                .id(e.getId()).warehouseId(e.getWarehouseId()).warehouseName(warehouseName)
                .code(e.getCode()).name(e.getName()).type(e.getType())
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(Zone e, UpdateZoneRequest r) {
        if (r.getName() != null) e.setName(r.getName());
        if (r.getType() != null) e.setType(r.getType());
        if (r.getRemark() != null) e.setRemark(r.getRemark());
    }
}
