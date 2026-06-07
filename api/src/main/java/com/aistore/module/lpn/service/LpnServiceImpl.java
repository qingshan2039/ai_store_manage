package com.aistore.module.lpn.service;

import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.location.entity.Location;
import com.aistore.module.location.mapper.LocationMapper;
import com.aistore.module.lpn.converter.LpnConverter;
import com.aistore.module.lpn.dto.CreateLpnRequest;
import com.aistore.module.lpn.dto.LpnQueryParam;
import com.aistore.module.lpn.dto.UpdateLpnRequest;
import com.aistore.module.lpn.dto.UpdateLpnStatusRequest;
import com.aistore.module.lpn.entity.Lpn;
import com.aistore.module.lpn.mapper.LpnMapper;
import com.aistore.module.lpn.vo.LpnListResponse;
import com.aistore.module.lpn.vo.LpnSummaryVO;
import com.aistore.module.lpn.vo.LpnVO;
import com.aistore.module.pallet.entity.PalletType;
import com.aistore.module.pallet.mapper.PalletTypeMapper;
import com.aistore.module.warehouse.entity.Warehouse;
import com.aistore.module.warehouse.mapper.WarehouseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 托盘实例服务实现 */
@Service
@RequiredArgsConstructor
public class LpnServiceImpl implements LpnService {

    private final LpnMapper lpnMapper;
    private final PalletTypeMapper palletTypeMapper;
    private final WarehouseMapper warehouseMapper;
    private final LocationMapper locationMapper;
    private final LpnConverter lpnConverter;

    @Override
    @Transactional
    public LpnVO createLpn(CreateLpnRequest request) {
        if (palletTypeMapper.selectById(request.getPalletTypeId()) == null) {
            throw ResourceNotFoundException.palletTypeNotFound();
        }
        if (warehouseMapper.selectById(request.getWarehouseId()) == null) {
            throw ResourceNotFoundException.warehouseNotFound();
        }
        if (request.getLocationId() != null && locationMapper.selectById(request.getLocationId()) == null) {
            throw ResourceNotFoundException.locationNotFound();
        }
        if (lpnMapper.selectCount(new LambdaQueryWrapper<Lpn>().eq(Lpn::getLpnCode, request.getLpnCode())) > 0) {
            throw DuplicateResourceException.duplicateLpnCode();
        }
        Lpn entity = lpnConverter.toEntity(request);
        lpnMapper.insert(entity);
        return getLpnById(entity.getId());
    }

    @Override
    public LpnVO getLpnById(Long id) {
        Lpn entity = lpnMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.lpnNotFound();
        }
        return lpnConverter.toVO(entity, palletTypeName(entity.getPalletTypeId()), warehouseName(entity.getWarehouseId()), locationCode(entity.getLocationId()));
    }

    @Override
    public LpnListResponse listLpns(LpnQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Lpn> page = new Page<>(pageNum, pageSize);
        IPage<Lpn> result = lpnMapper.selectLpnPage(page, param.getKeyword(), param.getWarehouseId(), param.getStatus());

        List<Lpn> records = result.getRecords();
        Map<Long, String> ptNames = palletTypeNames(records.stream().map(Lpn::getPalletTypeId).distinct().toList());
        Map<Long, String> whNames = warehouseNames(records.stream().map(Lpn::getWarehouseId).distinct().toList());
        Map<Long, String> locCodes = locationCodes(records.stream().map(Lpn::getLocationId).filter(java.util.Objects::nonNull).distinct().toList());

        List<LpnSummaryVO> items = records.stream()
                .map(l -> lpnConverter.toSummaryVO(l, ptNames.get(l.getPalletTypeId()), whNames.get(l.getWarehouseId()),
                        l.getLocationId() != null ? locCodes.get(l.getLocationId()) : null))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return LpnListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public LpnVO updateLpn(Long id, UpdateLpnRequest request) {
        Lpn entity = lpnMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.lpnNotFound();
        }
        if (request.getLocationId() != null && locationMapper.selectById(request.getLocationId()) == null) {
            throw ResourceNotFoundException.locationNotFound();
        }
        lpnConverter.updateEntity(entity, request);
        lpnMapper.updateById(entity);
        return getLpnById(id);
    }

    @Override
    @Transactional
    public void deleteLpn(Long id) {
        if (lpnMapper.selectById(id) == null) {
            throw ResourceNotFoundException.lpnNotFound();
        }
        lpnMapper.deleteById(id);
    }

    @Override
    @Transactional
    public LpnVO updateLpnStatus(Long id, UpdateLpnStatusRequest request) {
        Lpn entity = lpnMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.lpnNotFound();
        }
        entity.setStatus(request.getStatus().name());
        lpnMapper.updateById(entity);
        return getLpnById(id);
    }

    private String palletTypeName(Long id) {
        if (id == null) return null;
        PalletType p = palletTypeMapper.selectById(id);
        return p != null ? p.getName() : null;
    }

    private String warehouseName(Long id) {
        if (id == null) return null;
        Warehouse w = warehouseMapper.selectById(id);
        return w != null ? w.getName() : null;
    }

    private String locationCode(Long id) {
        if (id == null) return null;
        Location l = locationMapper.selectById(id);
        return l != null ? l.getCode() : null;
    }

    private Map<Long, String> palletTypeNames(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return palletTypeMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(PalletType::getId, PalletType::getName));
    }

    private Map<Long, String> warehouseNames(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return warehouseMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getName));
    }

    private Map<Long, String> locationCodes(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return locationMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Location::getId, Location::getCode));
    }
}
