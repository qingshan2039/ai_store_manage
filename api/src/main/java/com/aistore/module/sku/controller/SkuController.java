package com.aistore.module.sku.controller;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.sku.dto.CreateSkuRequest;
import com.aistore.module.sku.dto.SkuQueryParam;
import com.aistore.module.sku.dto.UpdateSkuRequest;
import com.aistore.module.sku.enums.ItemType;
import com.aistore.module.sku.service.SkuService;
import com.aistore.module.sku.vo.SkuListResponse;
import com.aistore.module.sku.vo.SkuVO;
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

/** SKU 管理 Controller */
@RestController
@RequestMapping("/api/skus")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @PostMapping
    public ResponseEntity<SkuVO> create(@Valid @RequestBody CreateSkuRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skuService.createSku(request));
    }

    @GetMapping
    public ResponseEntity<SkuListResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long spuId,
            @RequestParam(required = false) ItemType itemType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        SkuQueryParam param = SkuQueryParam.builder()
                .keyword(keyword).spuId(spuId)
                .itemType(itemType != null ? itemType.name() : null).status(status)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(skuService.listSkus(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkuVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(skuService.getSkuById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkuVO> update(@PathVariable Long id, @Valid @RequestBody UpdateSkuRequest request) {
        return ResponseEntity.ok(skuService.updateSku(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skuService.deleteSku(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SkuVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(skuService.updateSkuStatus(id, request));
    }
}
