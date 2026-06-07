package com.aistore.module.lpn.converter;

import com.aistore.module.lpn.dto.CreateLpnRequest;
import com.aistore.module.lpn.dto.UpdateLpnRequest;
import com.aistore.module.lpn.entity.Lpn;
import com.aistore.module.lpn.enums.LpnStatus;
import com.aistore.module.lpn.vo.LpnSummaryVO;
import com.aistore.module.lpn.vo.LpnVO;
import org.springframework.stereotype.Component;

/** 托盘实例对象转换器；关联名由 Service 传入。status 实体存枚举名。 */
@Component
public class LpnConverter {

    public Lpn toEntity(CreateLpnRequest r) {
        return Lpn.builder()
                .lpnCode(r.getLpnCode())
                .palletTypeId(r.getPalletTypeId())
                .warehouseId(r.getWarehouseId())
                .locationId(r.getLocationId())
                .status(r.getStatus() != null ? r.getStatus().name() : LpnStatus.IN_STOCK.name())
                .grossWeight(r.getGrossWeight())
                .deleted(0)
                .build();
    }

    public LpnVO toVO(Lpn e, String palletTypeName, String warehouseName, String locationCode) {
        return LpnVO.builder()
                .id(e.getId()).lpnCode(e.getLpnCode())
                .palletTypeId(e.getPalletTypeId()).palletTypeName(palletTypeName)
                .warehouseId(e.getWarehouseId()).warehouseName(warehouseName)
                .locationId(e.getLocationId()).locationCode(locationCode)
                .status(parseStatus(e.getStatus())).grossWeight(e.getGrossWeight())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public LpnSummaryVO toSummaryVO(Lpn e, String palletTypeName, String warehouseName, String locationCode) {
        return LpnSummaryVO.builder()
                .id(e.getId()).lpnCode(e.getLpnCode())
                .palletTypeName(palletTypeName).warehouseName(warehouseName).locationCode(locationCode)
                .status(parseStatus(e.getStatus())).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(Lpn e, UpdateLpnRequest r) {
        if (r.getLocationId() != null) e.setLocationId(r.getLocationId());
        if (r.getGrossWeight() != null) e.setGrossWeight(r.getGrossWeight());
    }

    private LpnStatus parseStatus(String v) {
        return v != null ? LpnStatus.valueOf(v) : null;
    }
}
