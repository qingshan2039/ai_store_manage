package com.aistore.module.zone.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.zone.dto.CreateZoneRequest;
import com.aistore.module.zone.dto.UpdateZoneRequest;
import com.aistore.module.zone.dto.ZoneQueryParam;
import com.aistore.module.zone.vo.ZoneListResponse;
import com.aistore.module.zone.vo.ZoneVO;

/** 库区服务接口 */
public interface ZoneService {
    ZoneVO createZone(CreateZoneRequest request);
    ZoneVO getZoneById(Long id);
    ZoneListResponse listZones(ZoneQueryParam param);
    ZoneVO updateZone(Long id, UpdateZoneRequest request);
    void deleteZone(Long id);
    ZoneVO updateZoneStatus(Long id, UpdateStatusRequest request);
}
