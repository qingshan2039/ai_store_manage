package com.aistore.module.vehicle.converter;

import com.aistore.module.vehicle.dto.CreateVehicleRequest;
import com.aistore.module.vehicle.dto.UpdateVehicleRequest;
import com.aistore.module.vehicle.entity.Vehicle;
import com.aistore.module.vehicle.vo.VehicleSummaryVO;
import com.aistore.module.vehicle.vo.VehicleVO;
import org.springframework.stereotype.Component;

/** 车辆对象转换器；常态司机/跟车员显示名由 Service 关联查询后传入 */
@Component
public class VehicleConverter {

    public Vehicle toEntity(CreateVehicleRequest r) {
        return Vehicle.builder()
                .plateNo(r.getPlateNo())
                .defaultDriverUserId(r.getDefaultDriverUserId())
                .defaultDriverOther(r.getDefaultDriverOther())
                .defaultEscortUserId(r.getDefaultEscortUserId())
                .defaultEscortOther(r.getDefaultEscortOther())
                .remark(r.getRemark())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public VehicleVO toVO(Vehicle e, String driverName, String escortName) {
        return VehicleVO.builder()
                .id(e.getId()).plateNo(e.getPlateNo())
                .defaultDriverUserId(e.getDefaultDriverUserId()).defaultDriverOther(e.getDefaultDriverOther())
                .defaultDriverName(driverName)
                .defaultEscortUserId(e.getDefaultEscortUserId()).defaultEscortOther(e.getDefaultEscortOther())
                .defaultEscortName(escortName)
                .remark(e.getRemark()).status(e.getStatus())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public VehicleSummaryVO toSummaryVO(Vehicle e, String driverName, String escortName) {
        return VehicleSummaryVO.builder()
                .id(e.getId()).plateNo(e.getPlateNo())
                .defaultDriverName(driverName).defaultEscortName(escortName)
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    /** 常态班组整体覆盖（实体字段 updateStrategy=IGNORED，允许写 null 以切换 用户 ↔ OTHER） */
    public void updateEntity(Vehicle e, UpdateVehicleRequest r) {
        if (r.getPlateNo() != null) e.setPlateNo(r.getPlateNo());
        e.setDefaultDriverUserId(r.getDefaultDriverUserId());
        e.setDefaultDriverOther(r.getDefaultDriverOther());
        e.setDefaultEscortUserId(r.getDefaultEscortUserId());
        e.setDefaultEscortOther(r.getDefaultEscortOther());
        if (r.getRemark() != null) e.setRemark(r.getRemark());
    }
}
