package com.aistore.module.fuel.converter;

import com.aistore.module.fuel.dto.CreateFuelRecordRequest;
import com.aistore.module.fuel.dto.UpdateFuelRecordRequest;
import com.aistore.module.fuel.entity.FuelRecord;
import com.aistore.module.fuel.vo.FuelRecordSummaryVO;
import com.aistore.module.fuel.vo.FuelRecordVO;
import org.springframework.stereotype.Component;

/** 打油记录对象转换器；车牌、司机名由 Service 关联查询后传入 */
@Component
public class FuelRecordConverter {

    public FuelRecord toEntity(CreateFuelRecordRequest r) {
        return FuelRecord.builder()
                .vehicleId(r.getVehicleId())
                .driverUserId(r.getDriverUserId())
                .fuelDate(r.getFuelDate())
                .liters(r.getLiters()).amount(r.getAmount())
                .unitPrice(r.getUnitPrice()).odometer(r.getOdometer())
                .images(r.getImages())
                .remark(r.getRemark())
                .deleted(0)
                .build();
    }

    public FuelRecordVO toVO(FuelRecord e, String vehiclePlateNo, String driverName) {
        return FuelRecordVO.builder()
                .id(e.getId()).vehicleId(e.getVehicleId()).vehiclePlateNo(vehiclePlateNo)
                .driverUserId(e.getDriverUserId()).driverName(driverName)
                .fuelDate(e.getFuelDate()).liters(e.getLiters()).amount(e.getAmount())
                .unitPrice(e.getUnitPrice()).odometer(e.getOdometer())
                .images(e.getImages()).remark(e.getRemark())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public FuelRecordSummaryVO toSummaryVO(FuelRecord e, String vehiclePlateNo, String driverName) {
        return FuelRecordSummaryVO.builder()
                .id(e.getId()).vehicleId(e.getVehicleId()).vehiclePlateNo(vehiclePlateNo)
                .driverName(driverName).fuelDate(e.getFuelDate())
                .liters(e.getLiters()).amount(e.getAmount())
                .imageCount(e.getImages() != null ? e.getImages().size() : 0)
                .createdAt(e.getCreatedAt())
                .build();
    }

    /** 更新：必填项非 null 才改；可空数值/图片/司机整体覆盖（实体 IGNORED 策略允许写 null） */
    public void updateEntity(FuelRecord e, UpdateFuelRecordRequest r) {
        if (r.getVehicleId() != null) e.setVehicleId(r.getVehicleId());
        if (r.getFuelDate() != null) e.setFuelDate(r.getFuelDate());
        e.setDriverUserId(r.getDriverUserId());
        e.setLiters(r.getLiters());
        e.setAmount(r.getAmount());
        e.setUnitPrice(r.getUnitPrice());
        e.setOdometer(r.getOdometer());
        e.setImages(r.getImages());
        if (r.getRemark() != null) e.setRemark(r.getRemark());
    }
}
