package com.aistore.module.zone.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.warehouse.entity.Warehouse;
import com.aistore.module.warehouse.mapper.WarehouseMapper;
import com.aistore.module.zone.converter.ZoneConverter;
import com.aistore.module.zone.dto.CreateZoneRequest;
import com.aistore.module.zone.dto.UpdateZoneRequest;
import com.aistore.module.zone.dto.ZoneQueryParam;
import com.aistore.module.zone.entity.Zone;
import com.aistore.module.zone.mapper.ZoneMapper;
import com.aistore.module.zone.vo.ZoneListResponse;
import com.aistore.module.zone.vo.ZoneSummaryVO;
import com.aistore.module.zone.vo.ZoneVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 库区服务实现 */
@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneMapper zoneMapper;
    private final WarehouseMapper warehouseMapper;
    private final ZoneConverter zoneConverter;

    @Override
    @Transactional
    public ZoneVO createZone(CreateZoneRequest request) {
        // 同一仓库内编码唯一
        if (zoneMapper.selectCount(new LambdaQueryWrapper<Zone>()
                .eq(Zone::getWarehouseId, request.getWarehouseId())
                .eq(Zone::getCode, request.getCode())) > 0) {
            throw DuplicateResourceException.duplicateZoneCode();
        }
        Zone entity = zoneConverter.toEntity(request);
        zoneMapper.insert(entity);
        return zoneConverter.toVO(zoneMapper.selectById(entity.getId()), warehouseName(entity.getWarehouseId()));
    }

    @Override
    public ZoneVO getZoneById(Long id) {
        Zone entity = zoneMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.zoneNotFound();
        }
        return zoneConverter.toVO(entity, warehouseName(entity.getWarehouseId()));
    }

    @Override
    public ZoneListResponse listZones(ZoneQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Zone> page = new Page<>(pageNum, pageSize);
        IPage<Zone> result = zoneMapper.selectZonePage(page, param.getKeyword(), param.getWarehouseId(), param.getStatus());

        List<Zone> records = result.getRecords();
        Map<Long, String> whNames = warehouseNames(records.stream().map(Zone::getWarehouseId).distinct().toList());

        List<ZoneSummaryVO> items = records.stream()
                .map(z -> zoneConverter.toSummaryVO(z, whNames.get(z.getWarehouseId())))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return ZoneListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public ZoneVO updateZone(Long id, UpdateZoneRequest request) {
        Zone entity = zoneMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.zoneNotFound();
        }
        zoneConverter.updateEntity(entity, request);
        zoneMapper.updateById(entity);
        return zoneConverter.toVO(zoneMapper.selectById(id), warehouseName(entity.getWarehouseId()));
    }

    @Override
    @Transactional
    public void deleteZone(Long id) {
        if (zoneMapper.selectById(id) == null) {
            throw ResourceNotFoundException.zoneNotFound();
        }
        zoneMapper.deleteById(id);
    }

    @Override
    @Transactional
    public ZoneVO updateZoneStatus(Long id, UpdateStatusRequest request) {
        Zone entity = zoneMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.zoneNotFound();
        }
        entity.setStatus(request.getStatus());
        zoneMapper.updateById(entity);
        return zoneConverter.toVO(zoneMapper.selectById(id), warehouseName(entity.getWarehouseId()));
    }

    private String warehouseName(Long warehouseId) {
        if (warehouseId == null) {
            return null;
        }
        Warehouse w = warehouseMapper.selectById(warehouseId);
        return w != null ? w.getName() : null;
    }

    private Map<Long, String> warehouseNames(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return warehouseMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Warehouse::getId, Warehouse::getName));
    }
}
