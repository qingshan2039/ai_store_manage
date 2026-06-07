package com.aistore.module.checkin.service;

import com.aistore.module.checkin.dto.CreateDriverCheckinRequest;
import com.aistore.module.checkin.dto.DriverCheckinQueryParam;
import com.aistore.module.checkin.dto.UpdateDriverCheckinRequest;
import com.aistore.module.checkin.vo.DriverCheckinListResponse;
import com.aistore.module.checkin.vo.DriverCheckinVO;

/** 司机打卡服务 */
public interface DriverCheckinService {
    DriverCheckinVO createDriverCheckin(CreateDriverCheckinRequest request);

    DriverCheckinVO getDriverCheckinById(Long id);

    DriverCheckinListResponse listDriverCheckins(DriverCheckinQueryParam param);

    DriverCheckinVO updateDriverCheckin(Long id, UpdateDriverCheckinRequest request);

    void deleteDriverCheckin(Long id);
}
