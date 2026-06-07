package com.aistore.module.location.converter;

import com.aistore.module.location.dto.CreateLocationRequest;
import com.aistore.module.location.dto.UpdateLocationRequest;
import com.aistore.module.location.entity.Location;
import com.aistore.module.location.vo.LocationSummaryVO;
import com.aistore.module.location.vo.LocationVO;
import org.springframework.stereotype.Component;

/** 库位对象转换器；warehouseName/zoneName 由 Service 关联查询后传入 */
@Component
public class LocationConverter {

    public Location toEntity(CreateLocationRequest r) {
        return Location.builder()
                .warehouseId(r.getWarehouseId())
                .zoneId(r.getZoneId())
                .code(r.getCode())
                .locType(r.getLocType())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public LocationVO toVO(Location e, String warehouseName, String zoneName) {
        return LocationVO.builder()
                .id(e.getId()).warehouseId(e.getWarehouseId()).warehouseName(warehouseName)
                .zoneId(e.getZoneId()).zoneName(zoneName)
                .code(e.getCode()).locType(e.getLocType()).status(e.getStatus())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public LocationSummaryVO toSummaryVO(Location e, String warehouseName, String zoneName) {
        return LocationSummaryVO.builder()
                .id(e.getId()).warehouseId(e.getWarehouseId()).warehouseName(warehouseName).zoneName(zoneName)
                .code(e.getCode()).locType(e.getLocType()).status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(Location e, UpdateLocationRequest r) {
        if (r.getCode() != null) e.setCode(r.getCode());
        if (r.getZoneId() != null) e.setZoneId(r.getZoneId());
        if (r.getLocType() != null) e.setLocType(r.getLocType());
    }
}
