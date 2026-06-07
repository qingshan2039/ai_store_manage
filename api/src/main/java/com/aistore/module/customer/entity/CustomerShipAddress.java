package com.aistore.module.customer.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 顾客送货地址实体（一个客户可有多个收/发货地址）
 * 对应数据库表 customer_ship_address
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("customer_ship_address")
public class CustomerShipAddress {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属客户 ID
     */
    private Long customerId;

    /**
     * 收/发货地址
     */
    private String address;

    /**
     * 送货地址备注（如客户报错地址后填写的修正说明）
     */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
