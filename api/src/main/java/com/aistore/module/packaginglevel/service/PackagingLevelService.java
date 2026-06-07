package com.aistore.module.packaginglevel.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.packaginglevel.dto.CreatePackagingLevelRequest;
import com.aistore.module.packaginglevel.dto.PackagingLevelQueryParam;
import com.aistore.module.packaginglevel.dto.UpdatePackagingLevelRequest;
import com.aistore.module.packaginglevel.vo.PackagingLevelListResponse;
import com.aistore.module.packaginglevel.vo.PackagingLevelVO;

/** 包装层级服务接口 */
public interface PackagingLevelService {
    PackagingLevelVO createLevel(CreatePackagingLevelRequest request);
    PackagingLevelVO getLevelById(Long id);
    PackagingLevelListResponse listLevels(PackagingLevelQueryParam param);
    PackagingLevelVO updateLevel(Long id, UpdatePackagingLevelRequest request);
    void deleteLevel(Long id);
    PackagingLevelVO updateLevelStatus(Long id, UpdateStatusRequest request);
}
