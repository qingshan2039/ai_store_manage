package com.aistore.module.unitconversion.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.sku.entity.Sku;
import com.aistore.module.sku.mapper.SkuMapper;
import com.aistore.module.unitconversion.converter.UnitConversionConverter;
import com.aistore.module.unitconversion.dto.CreateUnitConversionRequest;
import com.aistore.module.unitconversion.dto.UnitConversionQueryParam;
import com.aistore.module.unitconversion.dto.UpdateUnitConversionRequest;
import com.aistore.module.unitconversion.entity.UnitConversion;
import com.aistore.module.unitconversion.mapper.UnitConversionMapper;
import com.aistore.module.unitconversion.vo.UnitConversionListResponse;
import com.aistore.module.unitconversion.vo.UnitConversionSummaryVO;
import com.aistore.module.unitconversion.vo.UnitConversionVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 计量换算服务实现 */
@Service
@RequiredArgsConstructor
public class UnitConversionServiceImpl implements UnitConversionService {

    private final UnitConversionMapper conversionMapper;
    private final SkuMapper skuMapper;
    private final UnitConversionConverter conversionConverter;

    @Override
    @Transactional
    public UnitConversionVO createConversion(CreateUnitConversionRequest request) {
        Sku sku = skuMapper.selectById(request.getSkuId());
        if (sku == null) {
            throw ResourceNotFoundException.skuNotFound();
        }
        if (conversionMapper.selectCount(new LambdaQueryWrapper<UnitConversion>()
                .eq(UnitConversion::getSkuId, request.getSkuId())
                .eq(UnitConversion::getFromUnit, request.getFromUnit())
                .eq(UnitConversion::getToUnit, request.getToUnit())) > 0) {
            throw DuplicateResourceException.duplicateUnitConversion();
        }
        UnitConversion entity = conversionConverter.toEntity(request);
        conversionMapper.insert(entity);
        return conversionConverter.toVO(conversionMapper.selectById(entity.getId()), sku.getSkuCode(), sku.getSkuName());
    }

    @Override
    public UnitConversionVO getConversionById(Long id) {
        UnitConversion entity = conversionMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.unitConversionNotFound();
        }
        Sku sku = skuMapper.selectById(entity.getSkuId());
        return conversionConverter.toVO(entity, sku != null ? sku.getSkuCode() : null, sku != null ? sku.getSkuName() : null);
    }

    @Override
    public UnitConversionListResponse listConversions(UnitConversionQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<UnitConversion> page = new Page<>(pageNum, pageSize);
        IPage<UnitConversion> result = conversionMapper.selectConversionPage(page, param.getSkuId(), param.getStatus());

        List<UnitConversion> records = result.getRecords();
        Map<Long, String> skuNames = skuNames(records.stream().map(UnitConversion::getSkuId).distinct().toList());

        List<UnitConversionSummaryVO> items = records.stream()
                .map(c -> conversionConverter.toSummaryVO(c, skuNames.get(c.getSkuId())))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return UnitConversionListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public UnitConversionVO updateConversion(Long id, UpdateUnitConversionRequest request) {
        UnitConversion entity = conversionMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.unitConversionNotFound();
        }
        conversionConverter.updateEntity(entity, request);
        conversionMapper.updateById(entity);
        return getConversionById(id);
    }

    @Override
    @Transactional
    public void deleteConversion(Long id) {
        if (conversionMapper.selectById(id) == null) {
            throw ResourceNotFoundException.unitConversionNotFound();
        }
        conversionMapper.deleteById(id);
    }

    @Override
    @Transactional
    public UnitConversionVO updateConversionStatus(Long id, UpdateStatusRequest request) {
        UnitConversion entity = conversionMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.unitConversionNotFound();
        }
        entity.setStatus(request.getStatus());
        conversionMapper.updateById(entity);
        return getConversionById(id);
    }

    private Map<Long, String> skuNames(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return skuMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Sku::getId, Sku::getSkuName));
    }
}
