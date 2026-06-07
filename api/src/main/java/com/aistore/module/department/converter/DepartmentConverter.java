package com.aistore.module.department.converter;

import com.aistore.module.department.dto.CreateDepartmentRequest;
import com.aistore.module.department.dto.UpdateDepartmentRequest;
import com.aistore.module.department.entity.SysDepartment;
import com.aistore.module.department.enums.DepartmentType;
import com.aistore.module.department.vo.DepartmentSummaryVO;
import com.aistore.module.department.vo.DepartmentVO;
import org.springframework.stereotype.Component;

/**
 * 部门模块对象转换器
 * 负责 Entity ↔ VO / DTO 之间的转换
 * 注意：实体 type 以枚举名（String）存储，VO 以 DepartmentType 枚举暴露
 */
@Component
public class DepartmentConverter {

    /**
     * CreateDepartmentRequest → SysDepartment 实体
     */
    public SysDepartment toEntity(CreateDepartmentRequest request) {
        return SysDepartment.builder()
                .name(request.getName())
                .code(request.getCode())
                .type(request.getType() != null ? request.getType().name() : null)
                .sort(request.getSort() != null ? request.getSort() : 0)
                .remark(request.getRemark())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .deleted(0)
                .build();
    }

    /**
     * SysDepartment 实体 → DepartmentVO（部门详情响应）
     */
    public DepartmentVO toDepartmentVO(SysDepartment entity) {
        return DepartmentVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .type(parseType(entity.getType()))
                .status(entity.getStatus())
                .sort(entity.getSort())
                .remark(entity.getRemark())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    /**
     * SysDepartment 实体 → DepartmentSummaryVO（列表项响应）
     */
    public DepartmentSummaryVO toDepartmentSummaryVO(SysDepartment entity) {
        return DepartmentSummaryVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .type(parseType(entity.getType()))
                .status(entity.getStatus())
                .sort(entity.getSort())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * 使用 UpdateDepartmentRequest 中的非 null 字段更新 SysDepartment 实体
     */
    public void updateEntity(SysDepartment entity, UpdateDepartmentRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getType() != null) {
            entity.setType(request.getType().name());
        }
        if (request.getSort() != null) {
            entity.setSort(request.getSort());
        }
        if (request.getRemark() != null) {
            entity.setRemark(request.getRemark());
        }
    }

    /**
     * 枚举名（String）→ DepartmentType 枚举
     */
    private DepartmentType parseType(String type) {
        return type != null ? DepartmentType.valueOf(type) : null;
    }
}
