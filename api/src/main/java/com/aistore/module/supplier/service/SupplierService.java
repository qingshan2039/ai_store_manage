package com.aistore.module.supplier.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.supplier.dto.CreateSupplierRequest;
import com.aistore.module.supplier.dto.SupplierQueryParam;
import com.aistore.module.supplier.dto.UpdateSupplierRequest;
import com.aistore.module.supplier.vo.SupplierListResponse;
import com.aistore.module.supplier.vo.SupplierVO;

/** 供应商服务接口 */
public interface SupplierService {
    SupplierVO createSupplier(CreateSupplierRequest request);
    SupplierVO getSupplierById(Long id);
    SupplierListResponse listSuppliers(SupplierQueryParam param);
    SupplierVO updateSupplier(Long id, UpdateSupplierRequest request);
    void deleteSupplier(Long id);
    SupplierVO updateSupplierStatus(Long id, UpdateStatusRequest request);
}
