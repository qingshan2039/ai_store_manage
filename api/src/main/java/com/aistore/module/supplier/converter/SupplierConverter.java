package com.aistore.module.supplier.converter;

import com.aistore.module.supplier.dto.CreateSupplierRequest;
import com.aistore.module.supplier.dto.UpdateSupplierRequest;
import com.aistore.module.supplier.entity.Supplier;
import com.aistore.module.supplier.vo.SupplierSummaryVO;
import com.aistore.module.supplier.vo.SupplierVO;
import org.springframework.stereotype.Component;

/** 供应商对象转换器 */
@Component
public class SupplierConverter {

    public Supplier toEntity(CreateSupplierRequest request) {
        return Supplier.builder()
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

    public SupplierVO toVO(Supplier e) {
        return SupplierVO.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).address(e.getAddress())
                .contact(e.getContact()).phone(e.getPhone()).email(e.getEmail()).remark(e.getRemark())
                .status(e.getStatus()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public SupplierSummaryVO toSummaryVO(Supplier e) {
        return SupplierSummaryVO.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).address(e.getAddress())
                .contact(e.getContact()).phone(e.getPhone()).status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(Supplier e, UpdateSupplierRequest r) {
        if (r.getName() != null) e.setName(r.getName());
        if (r.getAddress() != null) e.setAddress(r.getAddress());
        if (r.getContact() != null) e.setContact(r.getContact());
        if (r.getPhone() != null) e.setPhone(r.getPhone());
        if (r.getEmail() != null) e.setEmail(r.getEmail());
        if (r.getRemark() != null) e.setRemark(r.getRemark());
    }
}
