package com.aistore.module.sku.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.sku.converter.SkuConverter;
import com.aistore.module.sku.dto.CreateSkuRequest;
import com.aistore.module.sku.dto.SkuQueryParam;
import com.aistore.module.sku.dto.UpdateSkuRequest;
import com.aistore.module.sku.entity.Sku;
import com.aistore.module.sku.mapper.SkuMapper;
import com.aistore.module.sku.vo.SkuListResponse;
import com.aistore.module.sku.vo.SkuSummaryVO;
import com.aistore.module.sku.vo.SkuVO;
import com.aistore.module.spu.entity.Spu;
import com.aistore.module.spu.mapper.SpuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** SKU 服务实现 */
@Service
@RequiredArgsConstructor
public class SkuServiceImpl implements SkuService {

    private final SkuMapper skuMapper;
    private final SpuMapper spuMapper;
    private final SkuConverter skuConverter;

    @Override
    @Transactional
    public SkuVO createSku(CreateSkuRequest request) {
        if (skuMapper.selectCount(new LambdaQueryWrapper<Sku>().eq(Sku::getSkuCode, request.getSkuCode())) > 0) {
            throw DuplicateResourceException.duplicateSkuCode();
        }
        Spu spu = spuMapper.selectById(request.getSpuId());
        if (spu == null) {
            throw ResourceNotFoundException.spuNotFound();
        }
        Sku entity = skuConverter.toEntity(request);
        skuMapper.insert(entity);
        Sku saved = skuMapper.selectById(entity.getId());
        return skuConverter.toVO(saved, spu.getSpuCode(), spu.getSpuName());
    }

    @Override
    public SkuVO getSkuById(Long id) {
        Sku entity = skuMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.skuNotFound();
        }
        Spu spu = spuMapper.selectById(entity.getSpuId());
        return skuConverter.toVO(entity, spu != null ? spu.getSpuCode() : null, spu != null ? spu.getSpuName() : null);
    }

    @Override
    public SkuListResponse listSkus(SkuQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Sku> page = new Page<>(pageNum, pageSize);
        IPage<Sku> result = skuMapper.selectSkuPage(page, param.getKeyword(), param.getSpuId(), param.getItemType(), param.getStatus());

        List<Sku> records = result.getRecords();
        Map<Long, String> spuNames = spuNames(records.stream().map(Sku::getSpuId).distinct().toList());

        List<SkuSummaryVO> items = records.stream()
                .map(s -> skuConverter.toSummaryVO(s, spuNames.get(s.getSpuId())))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return SkuListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public SkuVO updateSku(Long id, UpdateSkuRequest request) {
        Sku entity = skuMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.skuNotFound();
        }
        skuConverter.updateEntity(entity, request);
        skuMapper.updateById(entity);
        return toVoWithSpu(skuMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteSku(Long id) {
        if (skuMapper.selectById(id) == null) {
            throw ResourceNotFoundException.skuNotFound();
        }
        skuMapper.deleteById(id);
    }

    @Override
    @Transactional
    public SkuVO updateSkuStatus(Long id, UpdateStatusRequest request) {
        Sku entity = skuMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.skuNotFound();
        }
        entity.setStatus(request.getStatus());
        skuMapper.updateById(entity);
        return toVoWithSpu(skuMapper.selectById(id));
    }

    private SkuVO toVoWithSpu(Sku entity) {
        Spu spu = spuMapper.selectById(entity.getSpuId());
        return skuConverter.toVO(entity, spu != null ? spu.getSpuCode() : null, spu != null ? spu.getSpuName() : null);
    }

    private Map<Long, String> spuNames(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return spuMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Spu::getId, Spu::getSpuName));
    }
}
