package com.aistore.module.spu.controller;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.spu.dto.CreateSpuRequest;
import com.aistore.module.spu.dto.SpuQueryParam;
import com.aistore.module.spu.dto.UpdateSpuRequest;
import com.aistore.module.spu.service.SpuService;
import com.aistore.module.spu.vo.SpuListResponse;
import com.aistore.module.spu.vo.SpuVO;
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

/** SPU 管理 Controller */
@RestController
@RequestMapping("/api/spus")
@RequiredArgsConstructor
public class SpuController {

    private final SpuService spuService;

    @PostMapping
    public ResponseEntity<SpuVO> create(@Valid @RequestBody CreateSpuRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spuService.createSpu(request));
    }

    @GetMapping
    public ResponseEntity<SpuListResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        SpuQueryParam param = SpuQueryParam.builder()
                .keyword(keyword).categoryCode(categoryCode).status(status)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(spuService.listSpus(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpuVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(spuService.getSpuById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpuVO> update(@PathVariable Long id, @Valid @RequestBody UpdateSpuRequest request) {
        return ResponseEntity.ok(spuService.updateSpu(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        spuService.deleteSpu(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SpuVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(spuService.updateSpuStatus(id, request));
    }
}
