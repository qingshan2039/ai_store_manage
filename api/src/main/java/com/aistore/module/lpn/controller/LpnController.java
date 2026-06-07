package com.aistore.module.lpn.controller;

import com.aistore.module.lpn.dto.CreateLpnRequest;
import com.aistore.module.lpn.dto.LpnQueryParam;
import com.aistore.module.lpn.dto.UpdateLpnRequest;
import com.aistore.module.lpn.dto.UpdateLpnStatusRequest;
import com.aistore.module.lpn.enums.LpnStatus;
import com.aistore.module.lpn.service.LpnService;
import com.aistore.module.lpn.vo.LpnListResponse;
import com.aistore.module.lpn.vo.LpnVO;
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

/** 托盘实例管理 Controller */
@RestController
@RequestMapping("/api/lpns")
@RequiredArgsConstructor
public class LpnController {

    private final LpnService lpnService;

    @PostMapping
    public ResponseEntity<LpnVO> create(@Valid @RequestBody CreateLpnRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lpnService.createLpn(request));
    }

    @GetMapping
    public ResponseEntity<LpnListResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) LpnStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        LpnQueryParam param = LpnQueryParam.builder()
                .keyword(keyword).warehouseId(warehouseId)
                .status(status != null ? status.name() : null)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(lpnService.listLpns(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LpnVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lpnService.getLpnById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LpnVO> update(@PathVariable Long id, @Valid @RequestBody UpdateLpnRequest request) {
        return ResponseEntity.ok(lpnService.updateLpn(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lpnService.deleteLpn(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LpnVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateLpnStatusRequest request) {
        return ResponseEntity.ok(lpnService.updateLpnStatus(id, request));
    }
}
