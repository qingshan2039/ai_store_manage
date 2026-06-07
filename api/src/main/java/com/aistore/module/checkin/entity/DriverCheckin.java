package com.aistore.module.checkin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 司机每日打卡实体（表 driver_checkin）。
 * 司机/跟车员为“软引用 sys_user 或 OTHER 文本替补”二选一；车辆软引用 vehicle。
 * 可空字段标注 updateStrategy=IGNORED，使更新时可写 null（切换 用户 ↔ OTHER、清空时间等）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("driver_checkin")
public class DriverCheckin {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long driverUserId;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String driverOther;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long vehicleId;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long escortUserId;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String escortOther;

    private LocalDate checkinDate;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime clockInAt;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime clockOutAt;

    /** 出勤状态枚举名 NORMAL/LATE/ABSENT/LEAVE */
    private String checkinStatus;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    @TableLogic
    private Integer deleted;
}
