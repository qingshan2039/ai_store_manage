package com.aistore.module.category.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.category.converter.MaterialCategoryConverter;
import com.aistore.module.category.dto.CreateMaterialCategoryRequest;
import com.aistore.module.category.dto.MaterialCategoryQueryParam;
import com.aistore.module.category.dto.UpdateMaterialCategoryRequest;
import com.aistore.module.category.entity.MaterialCategory;
import com.aistore.module.category.mapper.MaterialCategoryMapper;
import com.aistore.module.category.vo.MaterialCategoryListResponse;
import com.aistore.module.category.vo.MaterialCategorySummaryVO;
import com.aistore.module.category.vo.MaterialCategoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 物料品类服务实现 */
@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    private final MaterialCategoryMapper categoryMapper;
    private final MaterialCategoryConverter categoryConverter;

    @Override
    @Transactional
    public MaterialCategoryVO createCategory(CreateMaterialCategoryRequest request) {
        if (categoryMapper.selectCount(new LambdaQueryWrapper<MaterialCategory>().eq(MaterialCategory::getName, request.getName())) > 0) {
            throw DuplicateResourceException.duplicateMaterialCategoryName();
        }
        if (categoryMapper.selectCount(new LambdaQueryWrapper<MaterialCategory>().eq(MaterialCategory::getCode, request.getCode())) > 0) {
            throw DuplicateResourceException.duplicateMaterialCategoryCode();
        }
        MaterialCategory entity = categoryConverter.toEntity(request);
        categoryMapper.insert(entity);
        return categoryConverter.toVO(categoryMapper.selectById(entity.getId()));
    }

    @Override
    public MaterialCategoryVO getCategoryById(Long id) {
        MaterialCategory entity = categoryMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.materialCategoryNotFound();
        }
        return categoryConverter.toVO(entity);
    }

    @Override
    public MaterialCategoryListResponse listCategories(MaterialCategoryQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<MaterialCategory> page = new Page<>(pageNum, pageSize);
        IPage<MaterialCategory> result = categoryMapper.selectCategoryPage(page, param.getKeyword(), param.getStatus());
        List<MaterialCategorySummaryVO> items = result.getRecords().stream().map(categoryConverter::toSummaryVO).toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return MaterialCategoryListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public MaterialCategoryVO updateCategory(Long id, UpdateMaterialCategoryRequest request) {
        MaterialCategory entity = categoryMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.materialCategoryNotFound();
        }
        if (request.getName() != null && !request.getName().equals(entity.getName())
                && categoryMapper.selectCount(new LambdaQueryWrapper<MaterialCategory>()
                .eq(MaterialCategory::getName, request.getName()).ne(MaterialCategory::getId, id)) > 0) {
            throw DuplicateResourceException.duplicateMaterialCategoryName();
        }
        categoryConverter.updateEntity(entity, request);
        categoryMapper.updateById(entity);
        return categoryConverter.toVO(categoryMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw ResourceNotFoundException.materialCategoryNotFound();
        }
        categoryMapper.deleteById(id);
    }

    @Override
    @Transactional
    public MaterialCategoryVO updateCategoryStatus(Long id, UpdateStatusRequest request) {
        MaterialCategory entity = categoryMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.materialCategoryNotFound();
        }
        entity.setStatus(request.getStatus());
        categoryMapper.updateById(entity);
        return categoryConverter.toVO(categoryMapper.selectById(id));
    }
}
