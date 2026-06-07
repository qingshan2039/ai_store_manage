package com.aistore.module.packagingrelation.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.packagingrelation.dto.CreatePackagingRelationRequest;
import com.aistore.module.packagingrelation.dto.PackagingRelationQueryParam;
import com.aistore.module.packagingrelation.dto.UpdatePackagingRelationRequest;
import com.aistore.module.packagingrelation.vo.PackagingRelationListResponse;
import com.aistore.module.packagingrelation.vo.PackagingRelationVO;

/** 包装关系服务接口 */
public interface PackagingRelationService {
    PackagingRelationVO createRelation(CreatePackagingRelationRequest request);
    PackagingRelationVO getRelationById(Long id);
    PackagingRelationListResponse listRelations(PackagingRelationQueryParam param);
    PackagingRelationVO updateRelation(Long id, UpdatePackagingRelationRequest request);
    void deleteRelation(Long id);
    PackagingRelationVO updateRelationStatus(Long id, UpdateStatusRequest request);
}
