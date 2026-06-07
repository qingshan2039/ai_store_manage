package com.aistore.module.fuel.service;

import com.aistore.module.fuel.dto.CreateFuelRecordRequest;
import com.aistore.module.fuel.dto.FuelRecordQueryParam;
import com.aistore.module.fuel.dto.UpdateFuelRecordRequest;
import com.aistore.module.fuel.vo.FuelRecordListResponse;
import com.aistore.module.fuel.vo.FuelRecordVO;

/** 打油记录服务 */
public interface FuelRecordService {
    FuelRecordVO createFuelRecord(CreateFuelRecordRequest request);

    FuelRecordVO getFuelRecordById(Long id);

    FuelRecordListResponse listFuelRecords(FuelRecordQueryParam param);

    FuelRecordVO updateFuelRecord(Long id, UpdateFuelRecordRequest request);

    void deleteFuelRecord(Long id);
}
