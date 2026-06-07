package com.aistore.module.pallet.controller;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.pallet.dto.CreatePalletTypeRequest;
import com.aistore.module.pallet.dto.PalletTypeQueryParam;
import com.aistore.module.pallet.dto.UpdatePalletTypeRequest;
import com.aistore.module.pallet.service.PalletTypeService;
import com.aistore.module.pallet.vo.PalletTypeListResponse;
import com.aistore.module.pallet.vo.PalletTypeVO;
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

/** 托盘类型管理 Controller */
@RestController
@RequestMapping("/api/pallet-types")
@RequiredArgsConstructor
public class PalletTypeController {

    private final PalletTypeService palletTypeService;

    @PostMapping
    public ResponseEntity<PalletTypeVO> create(@Valid @RequestBody CreatePalletTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(palletTypeService.createPalletType(request));
    }

    @GetMapping
    public ResponseEntity<PalletTypeListResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        PalletTypeQueryParam param = PalletTypeQueryParam.builder()
                .keyword(keyword).status(status)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(palletTypeService.listPalletTypes(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PalletTypeVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(palletTypeService.getPalletTypeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PalletTypeVO> update(@PathVariable Long id, @Valid @RequestBody UpdatePalletTypeRequest request) {
        return ResponseEntity.ok(palletTypeService.updatePalletType(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        palletTypeService.deletePalletType(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PalletTypeVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(palletTypeService.updatePalletTypeStatus(id, request));
    }
}
