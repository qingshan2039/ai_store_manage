package com.aistore.module.packaginglevel.controller;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.packaginglevel.dto.CreatePackagingLevelRequest;
import com.aistore.module.packaginglevel.dto.PackagingLevelQueryParam;
import com.aistore.module.packaginglevel.dto.UpdatePackagingLevelRequest;
import com.aistore.module.packaginglevel.service.PackagingLevelService;
import com.aistore.module.packaginglevel.vo.PackagingLevelListResponse;
import com.aistore.module.packaginglevel.vo.PackagingLevelVO;
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

/** 包装层级管理 Controller */
@RestController
@RequestMapping("/api/packaging-levels")
@RequiredArgsConstructor
public class PackagingLevelController {

    private final PackagingLevelService levelService;

    @PostMapping
    public ResponseEntity<PackagingLevelVO> create(@Valid @RequestBody CreatePackagingLevelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(levelService.createLevel(request));
    }

    @GetMapping
    public ResponseEntity<PackagingLevelListResponse> list(
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        PackagingLevelQueryParam param = PackagingLevelQueryParam.builder()
                .skuId(skuId).status(status)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(levelService.listLevels(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackagingLevelVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(levelService.getLevelById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PackagingLevelVO> update(@PathVariable Long id, @Valid @RequestBody UpdatePackagingLevelRequest request) {
        return ResponseEntity.ok(levelService.updateLevel(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        levelService.deleteLevel(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PackagingLevelVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(levelService.updateLevelStatus(id, request));
    }
}
