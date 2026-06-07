package com.aistore.module.vehicle.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.vehicle.dto.CreateVehicleRequest;
import com.aistore.module.vehicle.dto.UpdateVehicleRequest;
import com.aistore.module.vehicle.dto.VehicleQueryParam;
import com.aistore.module.vehicle.vo.VehicleListResponse;
import com.aistore.module.vehicle.vo.VehicleVO;

/** 车辆服务 */
public interface VehicleService {
    VehicleVO createVehicle(CreateVehicleRequest request);

    VehicleVO getVehicleById(Long id);

    VehicleListResponse listVehicles(VehicleQueryParam param);

    VehicleVO updateVehicle(Long id, UpdateVehicleRequest request);

    void deleteVehicle(Long id);

    VehicleVO updateVehicleStatus(Long id, UpdateStatusRequest request);
}
