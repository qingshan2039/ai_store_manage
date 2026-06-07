package com.aistore.module.fuel.controller;

import com.aistore.module.fuel.dto.CreateFuelRecordRequest;
import com.aistore.module.fuel.dto.FuelRecordQueryParam;
import com.aistore.module.fuel.dto.UpdateFuelRecordRequest;
import com.aistore.module.fuel.service.FuelRecordService;
import com.aistore.module.fuel.vo.FuelRecordListResponse;
import com.aistore.module.fuel.vo.FuelRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;

/** 打油记录管理 Controller（流水，无状态切换） */
@RestController
@RequestMapping("/api/fuel-records")
@RequiredArgsConstructor
public class FuelRecordController {

    private final FuelRecordService fuelRecordService;

    @PostMapping
    public ResponseEntity<FuelRecordVO> create(@Valid @RequestBody CreateFuelRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelRecordService.createFuelRecord(request));
    }

    @GetMapping
    public ResponseEntity<FuelRecordListResponse> list(
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fuelDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fuelDateEnd,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        FuelRecordQueryParam param = FuelRecordQueryParam.builder()
                .vehicleId(vehicleId).fuelDateStart(fuelDateStart).fuelDateEnd(fuelDateEnd)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(fuelRecordService.listFuelRecords(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuelRecordVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelRecordService.getFuelRecordById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuelRecordVO> update(@PathVariable Long id, @Valid @RequestBody UpdateFuelRecordRequest request) {
        return ResponseEntity.ok(fuelRecordService.updateFuelRecord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fuelRecordService.deleteFuelRecord(id);
        return ResponseEntity.noContent().build();
    }
}
