package com.aistore.module.itemimage.entity;

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

/** 物料图片实体（表 item_image，spu/sku/level 三者可空） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("item_image")
public class ItemImage {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long spuId;
    private Long skuId;
    private Long levelId;
    private String imageUrl;
    private String imageType;
    private Integer sortOrder;
    private Integer isPrimary;
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
