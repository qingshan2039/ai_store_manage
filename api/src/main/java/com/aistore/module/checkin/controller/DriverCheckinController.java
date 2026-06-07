package com.aistore.module.checkin.controller;

import com.aistore.module.checkin.dto.CreateDriverCheckinRequest;
import com.aistore.module.checkin.dto.DriverCheckinQueryParam;
import com.aistore.module.checkin.dto.UpdateDriverCheckinRequest;
import com.aistore.module.checkin.enums.CheckinStatus;
import com.aistore.module.checkin.service.DriverCheckinService;
import com.aistore.module.checkin.vo.DriverCheckinListResponse;
import com.aistore.module.checkin.vo.DriverCheckinVO;
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

/** 司机打卡管理 Controller（流水，无状态切换） */
@RestController
@RequestMapping("/api/driver-checkins")
@RequiredArgsConstructor
public class DriverCheckinController {

    private final DriverCheckinService driverCheckinService;

    @PostMapping
    public ResponseEntity<DriverCheckinVO> create(@Valid @RequestBody CreateDriverCheckinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverCheckinService.createDriverCheckin(request));
    }

    @GetMapping
    public ResponseEntity<DriverCheckinListResponse> list(
            @RequestParam(required = false) Long driverUserId,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) CheckinStatus checkinStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkinDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkinDateEnd,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        DriverCheckinQueryParam param = DriverCheckinQueryParam.builder()
                .driverUserId(driverUserId).vehicleId(vehicleId).checkinStatus(checkinStatus)
                .checkinDateStart(checkinDateStart).checkinDateEnd(checkinDateEnd)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(driverCheckinService.listDriverCheckins(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverCheckinVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(driverCheckinService.getDriverCheckinById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverCheckinVO> update(@PathVariable Long id, @Valid @RequestBody UpdateDriverCheckinRequest request) {
        return ResponseEntity.ok(driverCheckinService.updateDriverCheckin(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverCheckinService.deleteDriverCheckin(id);
        return ResponseEntity.noContent().build();
    }
}
