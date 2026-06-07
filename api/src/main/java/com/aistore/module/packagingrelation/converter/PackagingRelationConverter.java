package com.aistore.module.packagingrelation.converter;

import com.aistore.module.packagingrelation.dto.CreatePackagingRelationRequest;
import com.aistore.module.packagingrelation.dto.UpdatePackagingRelationRequest;
import com.aistore.module.packagingrelation.entity.PackagingRelation;
import com.aistore.module.packagingrelation.vo.PackagingRelationSummaryVO;
import com.aistore.module.packagingrelation.vo.PackagingRelationVO;
import org.springframework.stereotype.Component;

/** 包装关系对象转换器；父/子层名称由 Service 关联查询后传入 */
@Component
public class PackagingRelationConverter {

    public PackagingRelation toEntity(CreatePackagingRelationRequest r) {
        return PackagingRelation.builder()
                .parentLevelId(r.getParentLevelId())
                .childLevelId(r.getChildLevelId())
                .childQty(r.getChildQty())
                .isFixedQty(r.getIsFixedQty() != null ? r.getIsFixedQty() : 1)
                .tareWeight(r.getTareWeight())
                .status(r.getStatus() != null ? r.getStatus() : 1)
                .deleted(0)
                .build();
    }

    public PackagingRelationVO toVO(PackagingRelation e, String parentName, String childName) {
        return PackagingRelationVO.builder()
                .id(e.getId())
                .parentLevelId(e.getParentLevelId()).parentLevelName(parentName)
                .childLevelId(e.getChildLevelId()).childLevelName(childName)
                .childQty(e.getChildQty()).isFixedQty(e.getIsFixedQty()).tareWeight(e.getTareWeight())
                .status(e.getStatus()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public PackagingRelationSummaryVO toSummaryVO(PackagingRelation e, String parentName, String childName) {
        return PackagingRelationSummaryVO.builder()
                .id(e.getId())
                .parentLevelId(e.getParentLevelId()).parentLevelName(parentName)
                .childLevelId(e.getChildLevelId()).childLevelName(childName)
                .childQty(e.getChildQty()).isFixedQty(e.getIsFixedQty())
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(PackagingRelation e, UpdatePackagingRelationRequest r) {
        if (r.getChildQty() != null) e.setChildQty(r.getChildQty());
        if (r.getIsFixedQty() != null) e.setIsFixedQty(r.getIsFixedQty());
        if (r.getTareWeight() != null) e.setTareWeight(r.getTareWeight());
    }
}
