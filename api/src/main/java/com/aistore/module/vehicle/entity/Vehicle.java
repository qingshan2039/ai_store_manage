package com.aistore.module.vehicle.entity;

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

import java.time.LocalDateTime;

/**
 * 车辆实体（表 vehicle）。
 * 常态化司机/跟车员为“软引用 sys_user 或 OTHER 文本替补”二选一；
 * 这四个字段标注 updateStrategy=IGNORED，使更新时可写入 null（支持 用户 ↔ OTHER 的切换/清空）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("vehicle")
public class Vehicle {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String plateNo;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long defaultDriverUserId;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String defaultDriverOther;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long defaultEscortUserId;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String defaultEscortOther;

    private String remark;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    @TableLogic
    private Integer deleted;
}
