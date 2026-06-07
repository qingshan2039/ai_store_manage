package com.aistore.module.inventory.service;

import com.aistore.module.inventory.dto.CreateInventoryRequest;
import com.aistore.module.inventory.dto.InventoryQueryParam;
import com.aistore.module.inventory.dto.UpdateInventoryRequest;
import com.aistore.module.inventory.vo.InventoryListResponse;
import com.aistore.module.inventory.vo.InventorySummaryVO;
import com.aistore.module.inventory.vo.InventoryVO;

/** 库存服务接口 */
public interface InventoryService {
    InventoryVO createInventory(CreateInventoryRequest request);
    InventoryVO getInventoryById(Long id);
    InventoryListResponse listInventory(InventoryQueryParam param);
    InventoryVO updateInventory(Long id, UpdateInventoryRequest request);
    void deleteInventory(Long id);

    /** 库存统计：库存数量 + 托盘数量 + 整托/尾托（需求②） */
    InventorySummaryVO getSummary(Long skuId, Long warehouseId);
}
