package com.aistore.module.customer.converter;

import com.aistore.module.customer.dto.CreateCustomerRequest;
import com.aistore.module.customer.dto.ShipAddressInput;
import com.aistore.module.customer.dto.UpdateCustomerRequest;
import com.aistore.module.customer.entity.Customer;
import com.aistore.module.customer.entity.CustomerShipAddress;
import com.aistore.module.customer.vo.CustomerSummaryVO;
import com.aistore.module.customer.vo.CustomerVO;
import com.aistore.module.customer.vo.ShipAddressVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 顾客模块对象转换器
 * 收/发货地址为一对多，VO 构造时传入对应的子表记录列表。
 */
@Component
public class CustomerConverter {

    /**
     * CreateCustomerRequest → Customer 实体（不含送货地址，地址在子表）
     */
    public Customer toEntity(CreateCustomerRequest request) {
        return Customer.builder()
                .code(request.getCode())
                .name(request.getName())
                .address(request.getAddress())
                .contact(request.getContact())
                .phone(request.getPhone())
                .email(request.getEmail())
                .remark(request.getRemark())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .deleted(0)
                .build();
    }

    /**
     * ShipAddressInput 列表 → 子表实体列表（绑定 customerId）
     */
    public List<CustomerShipAddress> toShipAddressEntities(Long customerId, List<ShipAddressInput> inputs) {
        if (inputs == null) {
            return List.of();
        }
        return inputs.stream()
                .map(in -> CustomerShipAddress.builder()
                        .customerId(customerId)
                        .address(in.getAddress())
                        .remark(in.getRemark())
                        .build())
                .toList();
    }

    /**
     * Customer 实体 + 送货地址 → CustomerVO（详情响应）
     */
    public CustomerVO toCustomerVO(Customer entity, List<CustomerShipAddress> addresses) {
        return CustomerVO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .address(entity.getAddress())
                .shipAddresses(toShipAddressVOList(addresses))
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
     * Customer 实体 + 送货地址 → CustomerSummaryVO（列表项）
     */
    public CustomerSummaryVO toCustomerSummaryVO(Customer entity, List<CustomerShipAddress> addresses) {
        return CustomerSummaryVO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .address(entity.getAddress())
                .shipAddresses(toShipAddressVOList(addresses))
                .contact(entity.getContact())
                .phone(entity.getPhone())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private List<ShipAddressVO> toShipAddressVOList(List<CustomerShipAddress> addresses) {
        if (addresses == null) {
            return List.of();
        }
        return addresses.stream()
                .map(a -> ShipAddressVO.builder()
                        .id(a.getId())
                        .address(a.getAddress())
                        .remark(a.getRemark())
                        .build())
                .toList();
    }

    /**
     * 使用 UpdateCustomerRequest 中的非 null 标量字段更新 Customer 实体
     * （送货地址列表由 Service 单独处理）
     */
    public void updateEntity(Customer entity, UpdateCustomerRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getAddress() != null) {
            entity.setAddress(request.getAddress());
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
