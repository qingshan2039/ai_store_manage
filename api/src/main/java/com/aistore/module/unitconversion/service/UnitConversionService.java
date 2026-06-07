package com.aistore.module.unitconversion.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.unitconversion.dto.CreateUnitConversionRequest;
import com.aistore.module.unitconversion.dto.UnitConversionQueryParam;
import com.aistore.module.unitconversion.dto.UpdateUnitConversionRequest;
import com.aistore.module.unitconversion.vo.UnitConversionListResponse;
import com.aistore.module.unitconversion.vo.UnitConversionVO;

/** 计量换算服务接口 */
public interface UnitConversionService {
    UnitConversionVO createConversion(CreateUnitConversionRequest request);
    UnitConversionVO getConversionById(Long id);
    UnitConversionListResponse listConversions(UnitConversionQueryParam param);
    UnitConversionVO updateConversion(Long id, UpdateUnitConversionRequest request);
    void deleteConversion(Long id);
    UnitConversionVO updateConversionStatus(Long id, UpdateStatusRequest request);
}
