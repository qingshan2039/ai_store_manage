package com.aistore.module.warehouse.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.warehouse.converter.WarehouseConverter;
import com.aistore.module.warehouse.dto.CreateWarehouseRequest;
import com.aistore.module.warehouse.dto.UpdateWarehouseRequest;
import com.aistore.module.warehouse.dto.WarehouseQueryParam;
import com.aistore.module.warehouse.entity.Warehouse;
import com.aistore.module.warehouse.mapper.WarehouseMapper;
import com.aistore.module.warehouse.vo.WarehouseListResponse;
import com.aistore.module.warehouse.vo.WarehouseSummaryVO;
import com.aistore.module.warehouse.vo.WarehouseVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 仓库服务实现 */
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final WarehouseConverter warehouseConverter;

    @Override
    @Transactional
    public WarehouseVO createWarehouse(CreateWarehouseRequest request) {
        if (warehouseMapper.selectCount(new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getName, request.getName())) > 0) {
            throw DuplicateResourceException.duplicateWarehouseName();
        }
        if (warehouseMapper.selectCount(new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getCode, request.getCode())) > 0) {
            throw DuplicateResourceException.duplicateWarehouseCode();
        }
        Warehouse entity = warehouseConverter.toEntity(request);
        warehouseMapper.insert(entity);
        return warehouseConverter.toVO(warehouseMapper.selectById(entity.getId()));
    }

    @Override
    public WarehouseVO getWarehouseById(Long id) {
        Warehouse entity = warehouseMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.warehouseNotFound();
        }
        return warehouseConverter.toVO(entity);
    }

    @Override
    public WarehouseListResponse listWarehouses(WarehouseQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Warehouse> page = new Page<>(pageNum, pageSize);
        String type = param.getType() != null ? param.getType().name() : null;
        IPage<Warehouse> result = warehouseMapper.selectWarehousePage(page, param.getKeyword(), type, param.getStatus());
        List<WarehouseSummaryVO> items = result.getRecords().stream().map(warehouseConverter::toSummaryVO).toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return WarehouseListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public WarehouseVO updateWarehouse(Long id, UpdateWarehouseRequest request) {
        Warehouse entity = warehouseMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.warehouseNotFound();
        }
        if (request.getName() != null && !request.getName().equals(entity.getName())
                && warehouseMapper.selectCount(new LambdaQueryWrapper<Warehouse>()
                .eq(Warehouse::getName, request.getName()).ne(Warehouse::getId, id)) > 0) {
            throw DuplicateResourceException.duplicateWarehouseName();
        }
        warehouseConverter.updateEntity(entity, request);
        warehouseMapper.updateById(entity);
        return warehouseConverter.toVO(warehouseMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        if (warehouseMapper.selectById(id) == null) {
            throw ResourceNotFoundException.warehouseNotFound();
        }
        warehouseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public WarehouseVO updateWarehouseStatus(Long id, UpdateStatusRequest request) {
        Warehouse entity = warehouseMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.warehouseNotFound();
        }
        entity.setStatus(request.getStatus());
        warehouseMapper.updateById(entity);
        return warehouseConverter.toVO(warehouseMapper.selectById(id));
    }
}
