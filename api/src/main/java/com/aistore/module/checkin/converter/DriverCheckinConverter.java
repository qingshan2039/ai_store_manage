package com.aistore.module.checkin.converter;

import com.aistore.module.checkin.dto.CreateDriverCheckinRequest;
import com.aistore.module.checkin.dto.UpdateDriverCheckinRequest;
import com.aistore.module.checkin.entity.DriverCheckin;
import com.aistore.module.checkin.enums.CheckinStatus;
import com.aistore.module.checkin.vo.DriverCheckinSummaryVO;
import com.aistore.module.checkin.vo.DriverCheckinVO;
import org.springframework.stereotype.Component;

/** 打卡记录对象转换器；司机/跟车员/车牌显示名由 Service 关联查询后传入 */
@Component
public class DriverCheckinConverter {

    public DriverCheckin toEntity(CreateDriverCheckinRequest r) {
        return DriverCheckin.builder()
                .driverUserId(r.getDriverUserId()).driverOther(r.getDriverOther())
                .vehicleId(r.getVehicleId())
                .escortUserId(r.getEscortUserId()).escortOther(r.getEscortOther())
                .checkinDate(r.getCheckinDate())
                .clockInAt(r.getClockInAt()).clockOutAt(r.getClockOutAt())
                .checkinStatus(r.getCheckinStatus() != null ? r.getCheckinStatus().name() : CheckinStatus.NORMAL.name())
                .remark(r.getRemark())
                .deleted(0)
                .build();
    }

    public DriverCheckinVO toVO(DriverCheckin e, String driverName, String vehiclePlateNo, String escortName) {
        return DriverCheckinVO.builder()
                .id(e.getId())
                .driverUserId(e.getDriverUserId()).driverOther(e.getDriverOther()).driverName(driverName)
                .vehicleId(e.getVehicleId()).vehiclePlateNo(vehiclePlateNo)
                .escortUserId(e.getEscortUserId()).escortOther(e.getEscortOther()).escortName(escortName)
                .checkinDate(e.getCheckinDate()).clockInAt(e.getClockInAt()).clockOutAt(e.getClockOutAt())
                .checkinStatus(e.getCheckinStatus()).remark(e.getRemark())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public DriverCheckinSummaryVO toSummaryVO(DriverCheckin e, String driverName, String vehiclePlateNo, String escortName) {
        return DriverCheckinSummaryVO.builder()
                .id(e.getId())
                .driverUserId(e.getDriverUserId()).driverName(driverName)
                .vehicleId(e.getVehicleId()).vehiclePlateNo(vehiclePlateNo)
                .escortName(escortName)
                .checkinDate(e.getCheckinDate()).clockInAt(e.getClockInAt()).clockOutAt(e.getClockOutAt())
                .checkinStatus(e.getCheckinStatus())
                .build();
    }

    /** 更新：司机/跟车员/车辆/时间整体覆盖（实体 IGNORED 策略允许写 null），日期/状态/备注非 null 才改 */
    public void updateEntity(DriverCheckin e, UpdateDriverCheckinRequest r) {
        e.setDriverUserId(r.getDriverUserId());
        e.setDriverOther(r.getDriverOther());
        e.setVehicleId(r.getVehicleId());
        e.setEscortUserId(r.getEscortUserId());
        e.setEscortOther(r.getEscortOther());
        if (r.getCheckinDate() != null) e.setCheckinDate(r.getCheckinDate());
        e.setClockInAt(r.getClockInAt());
        e.setClockOutAt(r.getClockOutAt());
        if (r.getCheckinStatus() != null) e.setCheckinStatus(r.getCheckinStatus().name());
        if (r.getRemark() != null) e.setRemark(r.getRemark());
    }
}
