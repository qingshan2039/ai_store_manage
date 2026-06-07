package com.aistore.module.packaginglevel.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.packaginglevel.converter.PackagingLevelConverter;
import com.aistore.module.packaginglevel.dto.CreatePackagingLevelRequest;
import com.aistore.module.packaginglevel.dto.PackagingLevelQueryParam;
import com.aistore.module.packaginglevel.dto.UpdatePackagingLevelRequest;
import com.aistore.module.packaginglevel.entity.PackagingLevel;
import com.aistore.module.packaginglevel.mapper.PackagingLevelMapper;
import com.aistore.module.packaginglevel.vo.PackagingLevelListResponse;
import com.aistore.module.packaginglevel.vo.PackagingLevelSummaryVO;
import com.aistore.module.packaginglevel.vo.PackagingLevelVO;
import com.aistore.module.sku.entity.Sku;
import com.aistore.module.sku.mapper.SkuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 包装层级服务实现 */
@Service
@RequiredArgsConstructor
public class PackagingLevelServiceImpl implements PackagingLevelService {

    private final PackagingLevelMapper levelMapper;
    private final SkuMapper skuMapper;
    private final PackagingLevelConverter levelConverter;

    @Override
    @Transactional
    public PackagingLevelVO createLevel(CreatePackagingLevelRequest request) {
        Sku sku = skuMapper.selectById(request.getSkuId());
        if (sku == null) {
            throw ResourceNotFoundException.skuNotFound();
        }
        if (levelMapper.selectCount(new LambdaQueryWrapper<PackagingLevel>()
                .eq(PackagingLevel::getSkuId, request.getSkuId())
                .eq(PackagingLevel::getLevelSeq, request.getLevelSeq())) > 0) {
            throw DuplicateResourceException.duplicatePackagingLevelSeq();
        }
        PackagingLevel entity = levelConverter.toEntity(request);
        levelMapper.insert(entity);
        return levelConverter.toVO(levelMapper.selectById(entity.getId()), sku.getSkuCode(), sku.getSkuName());
    }

    @Override
    public PackagingLevelVO getLevelById(Long id) {
        PackagingLevel entity = levelMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.packagingLevelNotFound();
        }
        Sku sku = skuMapper.selectById(entity.getSkuId());
        return levelConverter.toVO(entity, sku != null ? sku.getSkuCode() : null, sku != null ? sku.getSkuName() : null);
    }

    @Override
    public PackagingLevelListResponse listLevels(PackagingLevelQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<PackagingLevel> page = new Page<>(pageNum, pageSize);
        IPage<PackagingLevel> result = levelMapper.selectLevelPage(page, param.getSkuId(), param.getStatus());

        List<PackagingLevel> records = result.getRecords();
        Map<Long, String> skuNames = skuNames(records.stream().map(PackagingLevel::getSkuId).distinct().toList());

        List<PackagingLevelSummaryVO> items = records.stream()
                .map(l -> levelConverter.toSummaryVO(l, skuNames.get(l.getSkuId())))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return PackagingLevelListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public PackagingLevelVO updateLevel(Long id, UpdatePackagingLevelRequest request) {
        PackagingLevel entity = levelMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.packagingLevelNotFound();
        }
        levelConverter.updateEntity(entity, request);
        levelMapper.updateById(entity);
        return getLevelById(id);
    }

    @Override
    @Transactional
    public void deleteLevel(Long id) {
        if (levelMapper.selectById(id) == null) {
            throw ResourceNotFoundException.packagingLevelNotFound();
        }
        levelMapper.deleteById(id);
    }

    @Override
    @Transactional
    public PackagingLevelVO updateLevelStatus(Long id, UpdateStatusRequest request) {
        PackagingLevel entity = levelMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.packagingLevelNotFound();
        }
        entity.setStatus(request.getStatus());
        levelMapper.updateById(entity);
        return getLevelById(id);
    }

    private Map<Long, String> skuNames(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return skuMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Sku::getId, Sku::getSkuName));
    }
}
