package com.aistore.module.location.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.location.dto.CreateLocationRequest;
import com.aistore.module.location.dto.LocationQueryParam;
import com.aistore.module.location.dto.UpdateLocationRequest;
import com.aistore.module.location.vo.LocationListResponse;
import com.aistore.module.location.vo.LocationVO;

/** 库位服务接口 */
public interface LocationService {
    LocationVO createLocation(CreateLocationRequest request);
    LocationVO getLocationById(Long id);
    LocationListResponse listLocations(LocationQueryParam param);
    LocationVO updateLocation(Long id, UpdateLocationRequest request);
    void deleteLocation(Long id);
    LocationVO updateLocationStatus(Long id, UpdateStatusRequest request);
}
