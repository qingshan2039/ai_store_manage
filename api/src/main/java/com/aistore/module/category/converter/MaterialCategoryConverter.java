package com.aistore.module.category.converter;

import com.aistore.module.category.dto.CreateMaterialCategoryRequest;
import com.aistore.module.category.dto.UpdateMaterialCategoryRequest;
import com.aistore.module.category.entity.MaterialCategory;
import com.aistore.module.category.vo.MaterialCategorySummaryVO;
import com.aistore.module.category.vo.MaterialCategoryVO;
import org.springframework.stereotype.Component;

/** 物料品类对象转换器 */
@Component
public class MaterialCategoryConverter {

    public MaterialCategory toEntity(CreateMaterialCategoryRequest r) {
        return MaterialCategory.builder()
                .code(r.getCode())
                .name(r.getName())
                .sortOrder(r.getSortOrder() != null ? r.getSortOrder() : 0)
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public MaterialCategoryVO toVO(MaterialCategory e) {
        return MaterialCategoryVO.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).sortOrder(e.getSortOrder())
                .status(e.getStatus()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public MaterialCategorySummaryVO toSummaryVO(MaterialCategory e) {
        return MaterialCategorySummaryVO.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).sortOrder(e.getSortOrder())
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(MaterialCategory e, UpdateMaterialCategoryRequest r) {
        if (r.getName() != null) e.setName(r.getName());
        if (r.getSortOrder() != null) e.setSortOrder(r.getSortOrder());
    }
}
