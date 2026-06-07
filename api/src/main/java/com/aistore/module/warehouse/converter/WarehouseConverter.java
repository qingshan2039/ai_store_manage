package com.aistore.module.warehouse.converter;

import com.aistore.module.warehouse.dto.CreateWarehouseRequest;
import com.aistore.module.warehouse.dto.UpdateWarehouseRequest;
import com.aistore.module.warehouse.entity.Warehouse;
import com.aistore.module.warehouse.enums.WarehouseType;
import com.aistore.module.warehouse.vo.WarehouseSummaryVO;
import com.aistore.module.warehouse.vo.WarehouseVO;
import org.springframework.stereotype.Component;

/** 仓库对象转换器；type 以枚举名 String 存储 */
@Component
public class WarehouseConverter {

    public Warehouse toEntity(CreateWarehouseRequest r) {
        return Warehouse.builder()
                .code(r.getCode())
                .name(r.getName())
                .type(r.getType() != null ? r.getType().name() : null)
                .remark(r.getRemark())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public WarehouseVO toVO(Warehouse e) {
        return WarehouseVO.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).type(parseType(e.getType()))
                .status(e.getStatus()).remark(e.getRemark())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public WarehouseSummaryVO toSummaryVO(Warehouse e) {
        return WarehouseSummaryVO.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).type(parseType(e.getType()))
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(Warehouse e, UpdateWarehouseRequest r) {
        if (r.getName() != null) e.setName(r.getName());
        if (r.getType() != null) e.setType(r.getType().name());
        if (r.getRemark() != null) e.setRemark(r.getRemark());
    }

    private WarehouseType parseType(String type) {
        return type != null ? WarehouseType.valueOf(type) : null;
    }
}
