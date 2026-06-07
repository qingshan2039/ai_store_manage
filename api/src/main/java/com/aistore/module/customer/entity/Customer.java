package com.aistore.module.customer.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * 顾客（客户公司）实体
 * 对应数据库表 customer
 * 收/发货地址为一对多，存于 customer_ship_address 子表（见 {@link CustomerShipAddress}）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("customer")
public class Customer {

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户编码（创建后不可修改，全局唯一）
     */
    private String code;

    /**
     * 客户公司名称（全局唯一）
     */
    private String name;

    /**
     * 客户公司地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：0=禁用，1=启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 创建人 ID
     */
    private Long createdBy;

    /**
     * 更新人 ID
     */
    private Long updatedBy;

    /**
     * 逻辑删除：0=未删除，1=已删除
     */
    @TableLogic
    private Integer deleted;
}
