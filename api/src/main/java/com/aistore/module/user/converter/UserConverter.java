package com.aistore.module.user.converter;

import com.aistore.module.user.dto.CreateUserRequest;
import com.aistore.module.user.dto.UpdateUserRequest;
import com.aistore.module.user.entity.SysUser;
import com.aistore.module.user.vo.UserSummaryVO;
import com.aistore.module.user.vo.UserVO;
import org.springframework.stereotype.Component;

/**
 * 用户模块对象转换器
 * 负责 Entity ↔ VO / DTO 之间的转换
 */
@Component
public class UserConverter {

    /**
     * CreateUserRequest → SysUser 实体
     * 注意：password 由 Service 层加密后设置，此处不处理
     */
    public SysUser toEntity(CreateUserRequest request) {
        return SysUser.builder()
                .employeeNo(request.getEmployeeNo())
                .username(request.getUsername())
                .name(request.getName())
                .nickname(request.getNickname())
                .gender(request.getGender() != null ? request.getGender() : 0)
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .jobTitle(request.getJobTitle())
                .departmentId(request.getDepartmentId())
                .hidePhoneNumber(request.getHidePhoneNumber() != null ? request.getHidePhoneNumber() : false)
                .hideName(request.getHideName() != null ? request.getHideName() : false)
                .remark(request.getRemark())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .deleted(0)
                .build();
    }

    /**
     * SysUser 实体 → UserVO（用户详情响应）
     * 注意：departmentName 暂时为 null，待部门模块完成后补充
     */
    public UserVO toUserVO(SysUser entity) {
        return UserVO.builder()
                .id(entity.getId())
                .employeeNo(entity.getEmployeeNo())
                .username(entity.getUsername())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .avatar(entity.getAvatar())
                .gender(entity.getGender())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .jobTitle(entity.getJobTitle())
                .departmentId(entity.getDepartmentId())
                .departmentName(null) // MVP 阶段暂无部门表，返回 null
                .hidePhoneNumber(entity.getHidePhoneNumber())
                .hideName(entity.getHideName())
                .remark(entity.getRemark())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    /**
     * SysUser 实体 → UserSummaryVO（列表项响应）
     * 注意：departmentName 暂时为 null，待部门模块完成后补充
     */
    public UserSummaryVO toUserSummaryVO(SysUser entity) {
        return UserSummaryVO.builder()
                .id(entity.getId())
                .employeeNo(entity.getEmployeeNo())
                .username(entity.getUsername())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .avatar(entity.getAvatar())
                .gender(entity.getGender())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .jobTitle(entity.getJobTitle())
                .departmentId(entity.getDepartmentId())
                .departmentName(null) // MVP 阶段暂无部门表，返回 null
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * 使用 UpdateUserRequest 中的非 null 字段更新 SysUser 实体
     * 只覆盖请求中明确传入的字段
     */
    public void updateEntity(SysUser entity, UpdateUserRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getNickname() != null) {
            entity.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            entity.setAvatar(request.getAvatar());
        }
        if (request.getGender() != null) {
            entity.setGender(request.getGender());
        }
        if (request.getPhoneNumber() != null) {
            entity.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getJobTitle() != null) {
            entity.setJobTitle(request.getJobTitle());
        }
        if (request.getDepartmentId() != null) {
            entity.setDepartmentId(request.getDepartmentId());
        }
        if (request.getHidePhoneNumber() != null) {
            entity.setHidePhoneNumber(request.getHidePhoneNumber());
        }
        if (request.getHideName() != null) {
            entity.setHideName(request.getHideName());
        }
        if (request.getRemark() != null) {
            entity.setRemark(request.getRemark());
        }
    }
}
