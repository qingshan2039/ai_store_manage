package com.aistore.module.unitconversion.controller;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.unitconversion.dto.CreateUnitConversionRequest;
import com.aistore.module.unitconversion.dto.UnitConversionQueryParam;
import com.aistore.module.unitconversion.dto.UpdateUnitConversionRequest;
import com.aistore.module.unitconversion.service.UnitConversionService;
import com.aistore.module.unitconversion.vo.UnitConversionListResponse;
import com.aistore.module.unitconversion.vo.UnitConversionVO;
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

/** 计量换算管理 Controller */
@RestController
@RequestMapping("/api/unit-conversions")
@RequiredArgsConstructor
public class UnitConversionController {

    private final UnitConversionService conversionService;

    @PostMapping
    public ResponseEntity<UnitConversionVO> create(@Valid @RequestBody CreateUnitConversionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conversionService.createConversion(request));
    }

    @GetMapping
    public ResponseEntity<UnitConversionListResponse> list(
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        UnitConversionQueryParam param = UnitConversionQueryParam.builder()
                .skuId(skuId).status(status)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(conversionService.listConversions(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitConversionVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(conversionService.getConversionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitConversionVO> update(@PathVariable Long id, @Valid @RequestBody UpdateUnitConversionRequest request) {
        return ResponseEntity.ok(conversionService.updateConversion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        conversionService.deleteConversion(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UnitConversionVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(conversionService.updateConversionStatus(id, request));
    }
}
