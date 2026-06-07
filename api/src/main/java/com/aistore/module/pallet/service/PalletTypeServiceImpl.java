package com.aistore.module.pallet.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.pallet.converter.PalletTypeConverter;
import com.aistore.module.pallet.dto.CreatePalletTypeRequest;
import com.aistore.module.pallet.dto.PalletTypeQueryParam;
import com.aistore.module.pallet.dto.UpdatePalletTypeRequest;
import com.aistore.module.pallet.entity.PalletType;
import com.aistore.module.pallet.mapper.PalletTypeMapper;
import com.aistore.module.pallet.vo.PalletTypeListResponse;
import com.aistore.module.pallet.vo.PalletTypeSummaryVO;
import com.aistore.module.pallet.vo.PalletTypeVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 托盘类型服务实现 */
@Service
@RequiredArgsConstructor
public class PalletTypeServiceImpl implements PalletTypeService {

    private final PalletTypeMapper palletTypeMapper;
    private final PalletTypeConverter palletTypeConverter;

    @Override
    @Transactional
    public PalletTypeVO createPalletType(CreatePalletTypeRequest request) {
        if (palletTypeMapper.selectCount(new LambdaQueryWrapper<PalletType>().eq(PalletType::getName, request.getName())) > 0) {
            throw DuplicateResourceException.duplicatePalletTypeName();
        }
        if (palletTypeMapper.selectCount(new LambdaQueryWrapper<PalletType>().eq(PalletType::getCode, request.getCode())) > 0) {
            throw DuplicateResourceException.duplicatePalletTypeCode();
        }
        PalletType entity = palletTypeConverter.toEntity(request);
        palletTypeMapper.insert(entity);
        return palletTypeConverter.toVO(palletTypeMapper.selectById(entity.getId()));
    }

    @Override
    public PalletTypeVO getPalletTypeById(Long id) {
        PalletType entity = palletTypeMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.palletTypeNotFound();
        }
        return palletTypeConverter.toVO(entity);
    }

    @Override
    public PalletTypeListResponse listPalletTypes(PalletTypeQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<PalletType> page = new Page<>(pageNum, pageSize);
        IPage<PalletType> result = palletTypeMapper.selectPalletTypePage(page, param.getKeyword(), param.getStatus());
        List<PalletTypeSummaryVO> items = result.getRecords().stream().map(palletTypeConverter::toSummaryVO).toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return PalletTypeListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public PalletTypeVO updatePalletType(Long id, UpdatePalletTypeRequest request) {
        PalletType entity = palletTypeMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.palletTypeNotFound();
        }
        if (request.getName() != null && !request.getName().equals(entity.getName())
                && palletTypeMapper.selectCount(new LambdaQueryWrapper<PalletType>()
                .eq(PalletType::getName, request.getName()).ne(PalletType::getId, id)) > 0) {
            throw DuplicateResourceException.duplicatePalletTypeName();
        }
        palletTypeConverter.updateEntity(entity, request);
        palletTypeMapper.updateById(entity);
        return palletTypeConverter.toVO(palletTypeMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deletePalletType(Long id) {
        if (palletTypeMapper.selectById(id) == null) {
            throw ResourceNotFoundException.palletTypeNotFound();
        }
        palletTypeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public PalletTypeVO updatePalletTypeStatus(Long id, UpdateStatusRequest request) {
        PalletType entity = palletTypeMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.palletTypeNotFound();
        }
        entity.setStatus(request.getStatus());
        palletTypeMapper.updateById(entity);
        return palletTypeConverter.toVO(palletTypeMapper.selectById(id));
    }
}
