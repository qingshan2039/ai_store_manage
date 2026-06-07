package com.aistore.module.spu.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.category.entity.MaterialCategory;
import com.aistore.module.category.mapper.MaterialCategoryMapper;
import com.aistore.module.spu.converter.SpuConverter;
import com.aistore.module.spu.dto.CreateSpuRequest;
import com.aistore.module.spu.dto.SpuQueryParam;
import com.aistore.module.spu.dto.UpdateSpuRequest;
import com.aistore.module.spu.entity.Spu;
import com.aistore.module.spu.mapper.SpuMapper;
import com.aistore.module.spu.vo.SpuListResponse;
import com.aistore.module.spu.vo.SpuSummaryVO;
import com.aistore.module.spu.vo.SpuVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** SPU 服务实现 */
@Service
@RequiredArgsConstructor
public class SpuServiceImpl implements SpuService {

    private final SpuMapper spuMapper;
    private final MaterialCategoryMapper categoryMapper;
    private final SpuConverter spuConverter;

    @Override
    @Transactional
    public SpuVO createSpu(CreateSpuRequest request) {
        if (spuMapper.selectCount(new LambdaQueryWrapper<Spu>().eq(Spu::getSpuName, request.getSpuName())) > 0) {
            throw DuplicateResourceException.duplicateSpuName();
        }
        if (spuMapper.selectCount(new LambdaQueryWrapper<Spu>().eq(Spu::getSpuCode, request.getSpuCode())) > 0) {
            throw DuplicateResourceException.duplicateSpuCode();
        }
        validateCategory(request.getCategoryCode());
        Spu entity = spuConverter.toEntity(request);
        spuMapper.insert(entity);
        Spu saved = spuMapper.selectById(entity.getId());
        return spuConverter.toVO(saved, categoryName(saved.getCategoryCode()));
    }

    @Override
    public SpuVO getSpuById(Long id) {
        Spu entity = spuMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.spuNotFound();
        }
        return spuConverter.toVO(entity, categoryName(entity.getCategoryCode()));
    }

    @Override
    public SpuListResponse listSpus(SpuQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Spu> page = new Page<>(pageNum, pageSize);
        IPage<Spu> result = spuMapper.selectSpuPage(page, param.getKeyword(), param.getCategoryCode(), param.getStatus());

        List<Spu> records = result.getRecords();
        Map<String, String> catNames = categoryNames(records.stream().map(Spu::getCategoryCode).distinct().toList());

        List<SpuSummaryVO> items = records.stream()
                .map(s -> spuConverter.toSummaryVO(s, catNames.get(s.getCategoryCode())))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return SpuListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public SpuVO updateSpu(Long id, UpdateSpuRequest request) {
        Spu entity = spuMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.spuNotFound();
        }
        if (request.getSpuName() != null && !request.getSpuName().equals(entity.getSpuName())
                && spuMapper.selectCount(new LambdaQueryWrapper<Spu>()
                .eq(Spu::getSpuName, request.getSpuName()).ne(Spu::getId, id)) > 0) {
            throw DuplicateResourceException.duplicateSpuName();
        }
        if (request.getCategoryCode() != null) {
            validateCategory(request.getCategoryCode());
        }
        spuConverter.updateEntity(entity, request);
        spuMapper.updateById(entity);
        Spu saved = spuMapper.selectById(id);
        return spuConverter.toVO(saved, categoryName(saved.getCategoryCode()));
    }

    @Override
    @Transactional
    public void deleteSpu(Long id) {
        if (spuMapper.selectById(id) == null) {
            throw ResourceNotFoundException.spuNotFound();
        }
        spuMapper.deleteById(id);
    }

    @Override
    @Transactional
    public SpuVO updateSpuStatus(Long id, UpdateStatusRequest request) {
        Spu entity = spuMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.spuNotFound();
        }
        entity.setStatus(request.getStatus());
        spuMapper.updateById(entity);
        Spu saved = spuMapper.selectById(id);
        return spuConverter.toVO(saved, categoryName(saved.getCategoryCode()));
    }

    /** 校验品类编码存在（未找到则视为引用不存在的资源） */
    private void validateCategory(String code) {
        if (categoryMapper.selectCount(new LambdaQueryWrapper<MaterialCategory>().eq(MaterialCategory::getCode, code)) == 0) {
            throw ResourceNotFoundException.materialCategoryNotFound();
        }
    }

    private String categoryName(String code) {
        if (code == null) {
            return null;
        }
        MaterialCategory c = categoryMapper.selectOne(
                new LambdaQueryWrapper<MaterialCategory>().eq(MaterialCategory::getCode, code));
        return c != null ? c.getName() : null;
    }

    private Map<String, String> categoryNames(List<String> codes) {
        if (codes.isEmpty()) {
            return Map.of();
        }
        return categoryMapper.selectList(new LambdaQueryWrapper<MaterialCategory>().in(MaterialCategory::getCode, codes)).stream()
                .collect(Collectors.toMap(MaterialCategory::getCode, MaterialCategory::getName, (a, b) -> a));
    }
}
