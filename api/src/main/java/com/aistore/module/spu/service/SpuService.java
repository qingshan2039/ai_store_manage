package com.aistore.module.spu.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.spu.dto.CreateSpuRequest;
import com.aistore.module.spu.dto.SpuQueryParam;
import com.aistore.module.spu.dto.UpdateSpuRequest;
import com.aistore.module.spu.vo.SpuListResponse;
import com.aistore.module.spu.vo.SpuVO;

/** SPU 服务接口 */
public interface SpuService {
    SpuVO createSpu(CreateSpuRequest request);
    SpuVO getSpuById(Long id);
    SpuListResponse listSpus(SpuQueryParam param);
    SpuVO updateSpu(Long id, UpdateSpuRequest request);
    void deleteSpu(Long id);
    SpuVO updateSpuStatus(Long id, UpdateStatusRequest request);
}
