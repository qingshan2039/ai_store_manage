package com.aistore.module.location.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.location.converter.LocationConverter;
import com.aistore.module.location.dto.CreateLocationRequest;
import com.aistore.module.location.dto.LocationQueryParam;
import com.aistore.module.location.dto.UpdateLocationRequest;
import com.aistore.module.location.entity.Location;
import com.aistore.module.location.mapper.LocationMapper;
import com.aistore.module.location.vo.LocationListResponse;
import com.aistore.module.location.vo.LocationSummaryVO;
import com.aistore.module.location.vo.LocationVO;
import com.aistore.module.warehouse.entity.Warehouse;
import com.aistore.module.warehouse.mapper.WarehouseMapper;
import com.aistore.module.zone.entity.Zone;
import com.aistore.module.zone.mapper.ZoneMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 库位服务实现 */
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationMapper locationMapper;
    private final WarehouseMapper warehouseMapper;
    private final ZoneMapper zoneMapper;
    private final LocationConverter locationConverter;

    @Override
    @Transactional
    public LocationVO createLocation(CreateLocationRequest request) {
        if (warehouseMapper.selectById(request.getWarehouseId()) == null) {
            throw ResourceNotFoundException.warehouseNotFound();
        }
        if (request.getZoneId() != null && zoneMapper.selectById(request.getZoneId()) == null) {
            throw ResourceNotFoundException.zoneNotFound();
        }
        if (locationMapper.selectCount(new LambdaQueryWrapper<Location>()
                .eq(Location::getWarehouseId, request.getWarehouseId())
                .eq(Location::getCode, request.getCode())) > 0) {
            throw DuplicateResourceException.duplicateLocationCode();
        }
        Location entity = locationConverter.toEntity(request);
        locationMapper.insert(entity);
        return getLocationById(entity.getId());
    }

    @Override
    public LocationVO getLocationById(Long id) {
        Location entity = locationMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.locationNotFound();
        }
        return locationConverter.toVO(entity, warehouseName(entity.getWarehouseId()), zoneName(entity.getZoneId()));
    }

    @Override
    public LocationListResponse listLocations(LocationQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Location> page = new Page<>(pageNum, pageSize);
        IPage<Location> result = locationMapper.selectLocationPage(page, param.getKeyword(), param.getWarehouseId(), param.getZoneId(), param.getStatus());

        List<Location> records = result.getRecords();
        Map<Long, String> whNames = warehouseNames(records.stream().map(Location::getWarehouseId).distinct().toList());
        Map<Long, String> zNames = zoneNames(records.stream().map(Location::getZoneId).filter(java.util.Objects::nonNull).distinct().toList());

        List<LocationSummaryVO> items = records.stream()
                .map(l -> locationConverter.toSummaryVO(l, whNames.get(l.getWarehouseId()), l.getZoneId() != null ? zNames.get(l.getZoneId()) : null))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return LocationListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public LocationVO updateLocation(Long id, UpdateLocationRequest request) {
        Location entity = locationMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.locationNotFound();
        }
        if (request.getZoneId() != null && zoneMapper.selectById(request.getZoneId()) == null) {
            throw ResourceNotFoundException.zoneNotFound();
        }
        // 编码可改：同一仓库内唯一
        if (request.getCode() != null && !request.getCode().equals(entity.getCode())
                && locationMapper.selectCount(new LambdaQueryWrapper<Location>()
                .eq(Location::getWarehouseId, entity.getWarehouseId())
                .eq(Location::getCode, request.getCode())
                .ne(Location::getId, id)) > 0) {
            throw DuplicateResourceException.duplicateLocationCode();
        }
        locationConverter.updateEntity(entity, request);
        locationMapper.updateById(entity);
        return getLocationById(id);
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        if (locationMapper.selectById(id) == null) {
            throw ResourceNotFoundException.locationNotFound();
        }
        locationMapper.deleteById(id);
    }

    @Override
    @Transactional
    public LocationVO updateLocationStatus(Long id, UpdateStatusRequest request) {
        Location entity = locationMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.locationNotFound();
        }
        entity.setStatus(request.getStatus());
        locationMapper.updateById(entity);
        return getLocationById(id);
    }

    private String warehouseName(Long id) {
        if (id == null) return null;
        Warehouse w = warehouseMapper.selectById(id);
        return w != null ? w.getName() : null;
    }

    private String zoneName(Long id) {
        if (id == null) return null;
        Zone z = zoneMapper.selectById(id);
        return z != null ? z.getName() : null;
    }

    private Map<Long, String> warehouseNames(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return warehouseMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getName));
    }

    private Map<Long, String> zoneNames(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return zoneMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Zone::getId, Zone::getName));
    }
}
