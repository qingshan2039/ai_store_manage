package com.aistore.module.packagingrelation.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.packaginglevel.entity.PackagingLevel;
import com.aistore.module.packaginglevel.mapper.PackagingLevelMapper;
import com.aistore.module.packagingrelation.converter.PackagingRelationConverter;
import com.aistore.module.packagingrelation.dto.CreatePackagingRelationRequest;
import com.aistore.module.packagingrelation.dto.PackagingRelationQueryParam;
import com.aistore.module.packagingrelation.dto.UpdatePackagingRelationRequest;
import com.aistore.module.packagingrelation.entity.PackagingRelation;
import com.aistore.module.packagingrelation.mapper.PackagingRelationMapper;
import com.aistore.module.packagingrelation.vo.PackagingRelationListResponse;
import com.aistore.module.packagingrelation.vo.PackagingRelationSummaryVO;
import com.aistore.module.packagingrelation.vo.PackagingRelationVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 包装关系服务实现 */
@Service
@RequiredArgsConstructor
public class PackagingRelationServiceImpl implements PackagingRelationService {

    private final PackagingRelationMapper relationMapper;
    private final PackagingLevelMapper levelMapper;
    private final PackagingRelationConverter relationConverter;

    @Override
    @Transactional
    public PackagingRelationVO createRelation(CreatePackagingRelationRequest request) {
        if (levelMapper.selectById(request.getParentLevelId()) == null
                || levelMapper.selectById(request.getChildLevelId()) == null) {
            throw ResourceNotFoundException.packagingLevelNotFound();
        }
        if (relationMapper.selectCount(new LambdaQueryWrapper<PackagingRelation>()
                .eq(PackagingRelation::getParentLevelId, request.getParentLevelId())
                .eq(PackagingRelation::getChildLevelId, request.getChildLevelId())) > 0) {
            throw DuplicateResourceException.duplicatePackagingRelation();
        }
        PackagingRelation entity = relationConverter.toEntity(request);
        relationMapper.insert(entity);
        return getRelationById(entity.getId());
    }

    @Override
    public PackagingRelationVO getRelationById(Long id) {
        PackagingRelation entity = relationMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.packagingRelationNotFound();
        }
        Map<Long, String> names = levelNames(List.of(entity.getParentLevelId(), entity.getChildLevelId()));
        return relationConverter.toVO(entity, names.get(entity.getParentLevelId()), names.get(entity.getChildLevelId()));
    }

    @Override
    public PackagingRelationListResponse listRelations(PackagingRelationQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<PackagingRelation> page = new Page<>(pageNum, pageSize);
        IPage<PackagingRelation> result = relationMapper.selectRelationPage(page, param.getParentLevelId(), param.getStatus());

        List<PackagingRelation> records = result.getRecords();
        List<Long> levelIds = records.stream()
                .flatMap(r -> Stream.of(r.getParentLevelId(), r.getChildLevelId()))
                .distinct().toList();
        Map<Long, String> names = levelNames(levelIds);

        List<PackagingRelationSummaryVO> items = records.stream()
                .map(r -> relationConverter.toSummaryVO(r, names.get(r.getParentLevelId()), names.get(r.getChildLevelId())))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return PackagingRelationListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public PackagingRelationVO updateRelation(Long id, UpdatePackagingRelationRequest request) {
        PackagingRelation entity = relationMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.packagingRelationNotFound();
        }
        relationConverter.updateEntity(entity, request);
        relationMapper.updateById(entity);
        return getRelationById(id);
    }

    @Override
    @Transactional
    public void deleteRelation(Long id) {
        if (relationMapper.selectById(id) == null) {
            throw ResourceNotFoundException.packagingRelationNotFound();
        }
        relationMapper.deleteById(id);
    }

    @Override
    @Transactional
    public PackagingRelationVO updateRelationStatus(Long id, UpdateStatusRequest request) {
        PackagingRelation entity = relationMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.packagingRelationNotFound();
        }
        entity.setStatus(request.getStatus());
        relationMapper.updateById(entity);
        return getRelationById(id);
    }

    private Map<Long, String> levelNames(List<Long> ids) {
        List<Long> nonNull = new ArrayList<>(ids.stream().filter(java.util.Objects::nonNull).distinct().toList());
        if (nonNull.isEmpty()) {
            return Map.of();
        }
        return levelMapper.selectBatchIds(nonNull).stream()
                .collect(Collectors.toMap(PackagingLevel::getId, PackagingLevel::getLevelName));
    }
}
