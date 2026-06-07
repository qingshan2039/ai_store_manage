package com.aistore.module.packagingrelation.controller;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.packagingrelation.dto.CreatePackagingRelationRequest;
import com.aistore.module.packagingrelation.dto.PackagingRelationQueryParam;
import com.aistore.module.packagingrelation.dto.UpdatePackagingRelationRequest;
import com.aistore.module.packagingrelation.service.PackagingRelationService;
import com.aistore.module.packagingrelation.vo.PackagingRelationListResponse;
import com.aistore.module.packagingrelation.vo.PackagingRelationVO;
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

/** 包装关系管理 Controller */
@RestController
@RequestMapping("/api/packaging-relations")
@RequiredArgsConstructor
public class PackagingRelationController {

    private final PackagingRelationService relationService;

    @PostMapping
    public ResponseEntity<PackagingRelationVO> create(@Valid @RequestBody CreatePackagingRelationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(relationService.createRelation(request));
    }

    @GetMapping
    public ResponseEntity<PackagingRelationListResponse> list(
            @RequestParam(required = false) Long parentLevelId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        PackagingRelationQueryParam param = PackagingRelationQueryParam.builder()
                .parentLevelId(parentLevelId).status(status)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(relationService.listRelations(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackagingRelationVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(relationService.getRelationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PackagingRelationVO> update(@PathVariable Long id, @Valid @RequestBody UpdatePackagingRelationRequest request) {
        return ResponseEntity.ok(relationService.updateRelation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        relationService.deleteRelation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PackagingRelationVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(relationService.updateRelationStatus(id, request));
    }
}
