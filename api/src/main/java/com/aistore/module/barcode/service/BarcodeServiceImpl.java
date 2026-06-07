package com.aistore.module.barcode.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.barcode.converter.BarcodeConverter;
import com.aistore.module.barcode.dto.BarcodeQueryParam;
import com.aistore.module.barcode.dto.CreateBarcodeRequest;
import com.aistore.module.barcode.dto.UpdateBarcodeRequest;
import com.aistore.module.barcode.entity.Barcode;
import com.aistore.module.barcode.mapper.BarcodeMapper;
import com.aistore.module.barcode.vo.BarcodeListResponse;
import com.aistore.module.barcode.vo.BarcodeSummaryVO;
import com.aistore.module.barcode.vo.BarcodeVO;
import com.aistore.module.packaginglevel.entity.PackagingLevel;
import com.aistore.module.packaginglevel.mapper.PackagingLevelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 条码服务实现 */
@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {

    private final BarcodeMapper barcodeMapper;
    private final PackagingLevelMapper levelMapper;
    private final BarcodeConverter barcodeConverter;

    @Override
    @Transactional
    public BarcodeVO createBarcode(CreateBarcodeRequest request) {
        if (levelMapper.selectById(request.getLevelId()) == null) {
            throw ResourceNotFoundException.packagingLevelNotFound();
        }
        if (barcodeMapper.selectCount(new LambdaQueryWrapper<Barcode>().eq(Barcode::getBarcode, request.getBarcode())) > 0) {
            throw DuplicateResourceException.duplicateBarcode();
        }
        Barcode entity = barcodeConverter.toEntity(request);
        barcodeMapper.insert(entity);
        return getBarcodeById(entity.getId());
    }

    @Override
    public BarcodeVO getBarcodeById(Long id) {
        Barcode entity = barcodeMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.barcodeNotFound();
        }
        return barcodeConverter.toVO(entity, levelName(entity.getLevelId()));
    }

    @Override
    public BarcodeListResponse listBarcodes(BarcodeQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Barcode> page = new Page<>(pageNum, pageSize);
        IPage<Barcode> result = barcodeMapper.selectBarcodePage(page, param.getKeyword(), param.getLevelId(), param.getStatus());

        List<Barcode> records = result.getRecords();
        Map<Long, String> levelNames = levelNames(records.stream().map(Barcode::getLevelId).distinct().toList());

        List<BarcodeSummaryVO> items = records.stream()
                .map(b -> barcodeConverter.toSummaryVO(b, levelNames.get(b.getLevelId())))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return BarcodeListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public BarcodeVO updateBarcode(Long id, UpdateBarcodeRequest request) {
        Barcode entity = barcodeMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.barcodeNotFound();
        }
        barcodeConverter.updateEntity(entity, request);
        barcodeMapper.updateById(entity);
        return getBarcodeById(id);
    }

    @Override
    @Transactional
    public void deleteBarcode(Long id) {
        if (barcodeMapper.selectById(id) == null) {
            throw ResourceNotFoundException.barcodeNotFound();
        }
        barcodeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public BarcodeVO updateBarcodeStatus(Long id, UpdateStatusRequest request) {
        Barcode entity = barcodeMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.barcodeNotFound();
        }
        entity.setStatus(request.getStatus());
        barcodeMapper.updateById(entity);
        return getBarcodeById(id);
    }

    private String levelName(Long levelId) {
        if (levelId == null) {
            return null;
        }
        PackagingLevel l = levelMapper.selectById(levelId);
        return l != null ? l.getLevelName() : null;
    }

    private Map<Long, String> levelNames(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return levelMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(PackagingLevel::getId, PackagingLevel::getLevelName));
    }
}
