package com.aistore.module.lpn.service;

import com.aistore.module.lpn.dto.CreateLpnRequest;
import com.aistore.module.lpn.dto.LpnQueryParam;
import com.aistore.module.lpn.dto.UpdateLpnRequest;
import com.aistore.module.lpn.dto.UpdateLpnStatusRequest;
import com.aistore.module.lpn.vo.LpnListResponse;
import com.aistore.module.lpn.vo.LpnVO;

/** 托盘实例服务接口 */
public interface LpnService {
    LpnVO createLpn(CreateLpnRequest request);
    LpnVO getLpnById(Long id);
    LpnListResponse listLpns(LpnQueryParam param);
    LpnVO updateLpn(Long id, UpdateLpnRequest request);
    void deleteLpn(Long id);
    LpnVO updateLpnStatus(Long id, UpdateLpnStatusRequest request);
}
