package com.aistore.module.warehouse.controller;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.warehouse.dto.CreateWarehouseRequest;
import com.aistore.module.warehouse.dto.UpdateWarehouseRequest;
import com.aistore.module.warehouse.dto.WarehouseQueryParam;
import com.aistore.module.warehouse.enums.WarehouseType;
import com.aistore.module.warehouse.service.WarehouseService;
import com.aistore.module.warehouse.vo.WarehouseListResponse;
import com.aistore.module.warehouse.vo.WarehouseVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 仓库管理 Controller */
@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseVO> create(@Valid @RequestBody CreateWarehouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.createWarehouse(request));
    }

    @GetMapping
    public ResponseEntity<WarehouseListResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) WarehouseType type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        WarehouseQueryParam param = WarehouseQueryParam.builder()
                .keyword(keyword).type(type).status(status)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(warehouseService.listWarehouses(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseVO> update(@PathVariable Long id, @Valid @RequestBody UpdateWarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WarehouseVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(warehouseService.updateWarehouseStatus(id, request));
    }
}
