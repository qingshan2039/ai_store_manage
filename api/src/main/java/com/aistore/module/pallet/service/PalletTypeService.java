package com.aistore.module.pallet.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.pallet.dto.CreatePalletTypeRequest;
import com.aistore.module.pallet.dto.PalletTypeQueryParam;
import com.aistore.module.pallet.dto.UpdatePalletTypeRequest;
import com.aistore.module.pallet.vo.PalletTypeListResponse;
import com.aistore.module.pallet.vo.PalletTypeVO;

/** 托盘类型服务接口 */
public interface PalletTypeService {
    PalletTypeVO createPalletType(CreatePalletTypeRequest request);
    PalletTypeVO getPalletTypeById(Long id);
    PalletTypeListResponse listPalletTypes(PalletTypeQueryParam param);
    PalletTypeVO updatePalletType(Long id, UpdatePalletTypeRequest request);
    void deletePalletType(Long id);
    PalletTypeVO updatePalletTypeStatus(Long id, UpdateStatusRequest request);
}
