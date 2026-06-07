package com.aistore.module.warehouse.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.warehouse.dto.CreateWarehouseRequest;
import com.aistore.module.warehouse.dto.UpdateWarehouseRequest;
import com.aistore.module.warehouse.dto.WarehouseQueryParam;
import com.aistore.module.warehouse.vo.WarehouseListResponse;
import com.aistore.module.warehouse.vo.WarehouseVO;

/** 仓库服务接口 */
public interface WarehouseService {
    WarehouseVO createWarehouse(CreateWarehouseRequest request);
    WarehouseVO getWarehouseById(Long id);
    WarehouseListResponse listWarehouses(WarehouseQueryParam param);
    WarehouseVO updateWarehouse(Long id, UpdateWarehouseRequest request);
    void deleteWarehouse(Long id);
    WarehouseVO updateWarehouseStatus(Long id, UpdateStatusRequest request);
}
