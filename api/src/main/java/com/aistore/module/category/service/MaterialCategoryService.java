package com.aistore.module.category.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.category.dto.CreateMaterialCategoryRequest;
import com.aistore.module.category.dto.MaterialCategoryQueryParam;
import com.aistore.module.category.dto.UpdateMaterialCategoryRequest;
import com.aistore.module.category.vo.MaterialCategoryListResponse;
import com.aistore.module.category.vo.MaterialCategoryVO;

/** 物料品类服务接口 */
public interface MaterialCategoryService {
    MaterialCategoryVO createCategory(CreateMaterialCategoryRequest request);
    MaterialCategoryVO getCategoryById(Long id);
    MaterialCategoryListResponse listCategories(MaterialCategoryQueryParam param);
    MaterialCategoryVO updateCategory(Long id, UpdateMaterialCategoryRequest request);
    void deleteCategory(Long id);
    MaterialCategoryVO updateCategoryStatus(Long id, UpdateStatusRequest request);
}
