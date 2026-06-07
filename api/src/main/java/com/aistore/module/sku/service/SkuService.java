package com.aistore.module.sku.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.sku.dto.CreateSkuRequest;
import com.aistore.module.sku.dto.SkuQueryParam;
import com.aistore.module.sku.dto.UpdateSkuRequest;
import com.aistore.module.sku.vo.SkuListResponse;
import com.aistore.module.sku.vo.SkuVO;

/** SKU 服务接口 */
public interface SkuService {
    SkuVO createSku(CreateSkuRequest request);
    SkuVO getSkuById(Long id);
    SkuListResponse listSkus(SkuQueryParam param);
    SkuVO updateSku(Long id, UpdateSkuRequest request);
    void deleteSku(Long id);
    SkuVO updateSkuStatus(Long id, UpdateStatusRequest request);
}
