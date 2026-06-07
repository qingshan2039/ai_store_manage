package com.aistore.module.category.controller;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.category.dto.CreateMaterialCategoryRequest;
import com.aistore.module.category.dto.MaterialCategoryQueryParam;
import com.aistore.module.category.dto.UpdateMaterialCategoryRequest;
import com.aistore.module.category.service.MaterialCategoryService;
import com.aistore.module.category.vo.MaterialCategoryListResponse;
import com.aistore.module.category.vo.MaterialCategoryVO;
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

/** 物料品类管理 Controller */
@RestController
@RequestMapping("/api/material-categories")
@RequiredArgsConstructor
public class MaterialCategoryController {

    private final MaterialCategoryService categoryService;

    @PostMapping
    public ResponseEntity<MaterialCategoryVO> create(@Valid @RequestBody CreateMaterialCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @GetMapping
    public ResponseEntity<MaterialCategoryListResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        MaterialCategoryQueryParam param = MaterialCategoryQueryParam.builder()
                .keyword(keyword).status(status)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(categoryService.listCategories(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialCategoryVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialCategoryVO> update(@PathVariable Long id, @Valid @RequestBody UpdateMaterialCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MaterialCategoryVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(categoryService.updateCategoryStatus(id, request));
    }
}
