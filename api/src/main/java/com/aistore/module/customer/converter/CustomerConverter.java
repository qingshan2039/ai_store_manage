package com.aistore.module.customer.converter;

import com.aistore.module.customer.dto.CreateCustomerRequest;
import com.aistore.module.customer.dto.UpdateCustomerRequest;
import com.aistore.module.customer.entity.Customer;
import com.aistore.module.customer.vo.CustomerSummaryVO;
import com.aistore.module.customer.vo.CustomerVO;
import org.springframework.stereotype.Component;

/**
 * 顾客模块对象转换器
 * 负责 Entity ↔ VO / DTO 之间的转换
 */
@Component
public class CustomerConverter {

    /**
     * CreateCustomerRequest → Customer 实体
     */
    public Customer toEntity(CreateCustomerRequest request) {
        return Customer.builder()
                .code(request.getCode())
                .name(request.getName())
                .address(request.getAddress())
                .shipAddress(request.getShipAddress())
                .contact(request.getContact())
                .phone(request.getPhone())
                .email(request.getEmail())
                .remark(request.getRemark())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .deleted(0)
                .build();
    }

    /**
     * Customer 实体 → CustomerVO（顾客详情响应）
     */
    public CustomerVO toCustomerVO(Customer entity) {
        return CustomerVO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .address(entity.getAddress())
                .shipAddress(entity.getShipAddress())
                .contact(entity.getContact())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .remark(entity.getRemark())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    /**
     * Customer 实体 → CustomerSummaryVO（列表项响应）
     */
    public CustomerSummaryVO toCustomerSummaryVO(Customer entity) {
        return CustomerSummaryVO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .address(entity.getAddress())
                .shipAddress(entity.getShipAddress())
                .contact(entity.getContact())
                .phone(entity.getPhone())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * 使用 UpdateCustomerRequest 中的非 null 字段更新 Customer 实体
     */
    public void updateEntity(Customer entity, UpdateCustomerRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getAddress() != null) {
            entity.setAddress(request.getAddress());
        }
        if (request.getShipAddress() != null) {
            entity.setShipAddress(request.getShipAddress());
        }
        if (request.getContact() != null) {
            entity.setContact(request.getContact());
        }
        if (request.getPhone() != null) {
            entity.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getRemark() != null) {
            entity.setRemark(request.getRemark());
        }
    }
}
