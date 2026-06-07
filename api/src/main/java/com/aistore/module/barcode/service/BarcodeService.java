package com.aistore.module.barcode.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.barcode.dto.BarcodeQueryParam;
import com.aistore.module.barcode.dto.CreateBarcodeRequest;
import com.aistore.module.barcode.dto.UpdateBarcodeRequest;
import com.aistore.module.barcode.vo.BarcodeListResponse;
import com.aistore.module.barcode.vo.BarcodeVO;

/** 条码服务接口 */
public interface BarcodeService {
    BarcodeVO createBarcode(CreateBarcodeRequest request);
    BarcodeVO getBarcodeById(Long id);
    BarcodeListResponse listBarcodes(BarcodeQueryParam param);
    BarcodeVO updateBarcode(Long id, UpdateBarcodeRequest request);
    void deleteBarcode(Long id);
    BarcodeVO updateBarcodeStatus(Long id, UpdateStatusRequest request);
}
