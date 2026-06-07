package com.aistore.module.inventory.controller;

import com.aistore.module.inventory.dto.CreateInventoryRequest;
import com.aistore.module.inventory.dto.InventoryQueryParam;
import com.aistore.module.inventory.dto.UpdateInventoryRequest;
import com.aistore.module.inventory.service.InventoryService;
import com.aistore.module.inventory.vo.InventoryListResponse;
import com.aistore.module.inventory.vo.InventorySummaryVO;
import com.aistore.module.inventory.vo.InventoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 库存管理 Controller */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryVO> create(@Valid @RequestBody CreateInventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(request));
    }

    @GetMapping
    public ResponseEntity<InventoryListResponse> list(
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) Long lpnId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        InventoryQueryParam param = InventoryQueryParam.builder()
                .skuId(skuId).lpnId(lpnId).locationId(locationId)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(inventoryService.listInventory(param));
    }

    /** 库存统计：库存数量 + 托盘数量 + 整托/尾托（需求②） */
    @GetMapping("/summary")
    public ResponseEntity<InventorySummaryVO> summary(
            @RequestParam Long skuId,
            @RequestParam(required = false) Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getSummary(skuId, warehouseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryVO> update(@PathVariable Long id, @Valid @RequestBody UpdateInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
