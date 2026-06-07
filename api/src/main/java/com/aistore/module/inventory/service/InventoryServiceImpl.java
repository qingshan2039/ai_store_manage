package com.aistore.module.inventory.service;

import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.inventory.converter.InventoryConverter;
import com.aistore.module.inventory.dto.CreateInventoryRequest;
import com.aistore.module.inventory.dto.InventoryQueryParam;
import com.aistore.module.inventory.dto.UpdateInventoryRequest;
import com.aistore.module.inventory.entity.Inventory;
import com.aistore.module.inventory.mapper.InventoryMapper;
import com.aistore.module.inventory.vo.InventoryListResponse;
import com.aistore.module.inventory.vo.InventoryPalletVO;
import com.aistore.module.inventory.vo.InventorySummaryItemVO;
import com.aistore.module.inventory.vo.InventorySummaryVO;
import com.aistore.module.inventory.vo.InventoryVO;
import com.aistore.module.location.entity.Location;
import com.aistore.module.location.mapper.LocationMapper;
import com.aistore.module.lpn.entity.Lpn;
import com.aistore.module.lpn.mapper.LpnMapper;
import com.aistore.module.pallet.entity.PalletType;
import com.aistore.module.pallet.mapper.PalletTypeMapper;
import com.aistore.module.packaginglevel.entity.PackagingLevel;
import com.aistore.module.packaginglevel.mapper.PackagingLevelMapper;
import com.aistore.module.packagingrelation.entity.PackagingRelation;
import com.aistore.module.packagingrelation.mapper.PackagingRelationMapper;
import com.aistore.module.sku.entity.Sku;
import com.aistore.module.sku.mapper.SkuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** 库存服务实现（含库存数量/托盘数量/整托尾托统计）。 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final SkuMapper skuMapper;
    private final LpnMapper lpnMapper;
    private final PalletTypeMapper palletTypeMapper;
    private final LocationMapper locationMapper;
    private final PackagingLevelMapper levelMapper;
    private final PackagingRelationMapper relationMapper;
    private final InventoryConverter inventoryConverter;

    @Override
    @Transactional
    public InventoryVO createInventory(CreateInventoryRequest request) {
        if (skuMapper.selectById(request.getSkuId()) == null) {
            throw ResourceNotFoundException.skuNotFound();
        }
        Inventory entity = inventoryConverter.toEntity(request);
        inventoryMapper.insert(entity);
        return getInventoryById(entity.getId());
    }

    @Override
    public InventoryVO getInventoryById(Long id) {
        Inventory e = inventoryMapper.selectById(id);
        if (e == null) {
            throw ResourceNotFoundException.inventoryNotFound();
        }
        Sku sku = skuMapper.selectById(e.getSkuId());
        return inventoryConverter.toVO(e,
                sku != null ? sku.getSkuCode() : null, sku != null ? sku.getSkuName() : null,
                lpnCode(e.getLpnId()), locationCode(e.getLocationId()));
    }

    @Override
    public InventoryListResponse listInventory(InventoryQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Inventory> page = new Page<>(pageNum, pageSize);
        IPage<Inventory> result = inventoryMapper.selectInventoryPage(page, param.getSkuId(), param.getLpnId(), param.getLocationId());

        List<Inventory> records = result.getRecords();
        Map<Long, String> skuNames = skuNames(records.stream().map(Inventory::getSkuId).distinct().toList());
        List<Long> lpnIds = records.stream().map(Inventory::getLpnId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> lpnCodes = lpnCodes(lpnIds);
        Map<Long, String> lpnPalletTypes = lpnPalletTypeNames(lpnIds);
        Map<Long, String> locCodes = locationCodes(records.stream().map(Inventory::getLocationId).filter(Objects::nonNull).distinct().toList());

        List<InventorySummaryItemVO> items = records.stream()
                .map(r -> inventoryConverter.toSummaryItemVO(r, skuNames.get(r.getSkuId()),
                        r.getLpnId() != null ? lpnCodes.get(r.getLpnId()) : null,
                        r.getLpnId() != null ? lpnPalletTypes.get(r.getLpnId()) : null,
                        r.getLocationId() != null ? locCodes.get(r.getLocationId()) : null))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return InventoryListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public InventoryVO updateInventory(Long id, UpdateInventoryRequest request) {
        Inventory e = inventoryMapper.selectById(id);
        if (e == null) {
            throw ResourceNotFoundException.inventoryNotFound();
        }
        inventoryConverter.updateEntity(e, request);
        inventoryMapper.updateById(e);
        return getInventoryById(id);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        if (inventoryMapper.selectById(id) == null) {
            throw ResourceNotFoundException.inventoryNotFound();
        }
        inventoryMapper.deleteById(id);
    }

    @Override
    public InventorySummaryVO getSummary(Long skuId, Long warehouseId) {
        Sku sku = skuMapper.selectById(skuId);
        if (sku == null) {
            throw ResourceNotFoundException.skuNotFound();
        }
        List<Inventory> rows = inventoryMapper.selectList(new LambdaQueryWrapper<Inventory>().eq(Inventory::getSkuId, skuId));

        // 可选按仓库过滤（经所在托盘的仓库）
        if (warehouseId != null) {
            Set<Long> lpnIds = rows.stream().map(Inventory::getLpnId).filter(Objects::nonNull).collect(Collectors.toSet());
            Map<Long, Long> lpnWh = lpnIds.isEmpty() ? Map.of()
                    : lpnMapper.selectBatchIds(lpnIds).stream().collect(Collectors.toMap(Lpn::getId, Lpn::getWarehouseId));
            rows = rows.stream().filter(r -> r.getLpnId() != null && warehouseId.equals(lpnWh.get(r.getLpnId()))).toList();
        }

        BigDecimal total = rows.stream().map(r -> nz(r.getQtyOnHand())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal reserved = rows.stream().map(r -> nz(r.getQtyReserved())).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 按托盘汇总（托盘数量 = COUNT(DISTINCT lpn_id)）
        Map<Long, BigDecimal> byLpn = new LinkedHashMap<>();
        for (Inventory r : rows) {
            if (r.getLpnId() != null) {
                byLpn.merge(r.getLpnId(), nz(r.getQtyOnHand()), BigDecimal::add);
            }
        }
        BigDecimal std = standardPalletQty(skuId);
        Map<Long, String> lpnCodes = byLpn.isEmpty() ? Map.of()
                : lpnMapper.selectBatchIds(byLpn.keySet()).stream().collect(Collectors.toMap(Lpn::getId, Lpn::getLpnCode));

        List<InventoryPalletVO> pallets = byLpn.entrySet().stream()
                .map(en -> InventoryPalletVO.builder()
                        .lpnId(en.getKey()).lpnCode(lpnCodes.get(en.getKey())).qty(en.getValue())
                        .fullPallet(std != null ? en.getValue().compareTo(std) >= 0 : null)
                        .build())
                .toList();

        return InventorySummaryVO.builder()
                .skuId(skuId).skuName(sku.getSkuName())
                .totalQty(total).totalReserved(reserved).totalAvailable(total.subtract(reserved))
                .palletCount(byLpn.size()).recordCount(rows.size()).standardPalletQty(std).pallets(pallets)
                .build();
    }

    /** 标准每托数：从顶层（托）沿 is_fixed_qty=1 关系逐级乘 child_qty，至基本单位或链末。 */
    private BigDecimal standardPalletQty(Long skuId) {
        List<PackagingLevel> levels = levelMapper.selectList(new LambdaQueryWrapper<PackagingLevel>().eq(PackagingLevel::getSkuId, skuId));
        if (levels.isEmpty()) {
            return null;
        }
        PackagingLevel top = levels.stream().max(Comparator.comparingInt(PackagingLevel::getLevelSeq)).orElse(null);
        Long baseId = levels.stream().filter(l -> Integer.valueOf(1).equals(l.getIsBaseUnit())).map(PackagingLevel::getId).findFirst()
                .orElseGet(() -> levels.stream().min(Comparator.comparingInt(PackagingLevel::getLevelSeq)).map(PackagingLevel::getId).orElse(null));
        if (top == null) {
            return null;
        }
        BigDecimal product = BigDecimal.ONE;
        Long current = top.getId();
        Set<Long> visited = new HashSet<>();
        boolean stepped = false;
        for (int i = 0; i <= levels.size(); i++) {
            if (current == null || current.equals(baseId) || !visited.add(current)) {
                break;
            }
            PackagingRelation rel = relationMapper.selectList(new LambdaQueryWrapper<PackagingRelation>()
                    .eq(PackagingRelation::getParentLevelId, current)
                    .eq(PackagingRelation::getIsFixedQty, 1)).stream().findFirst().orElse(null);
            if (rel == null || rel.getChildQty() == null) {
                break;
            }
            product = product.multiply(rel.getChildQty());
            current = rel.getChildLevelId();
            stepped = true;
        }
        return stepped ? product : null;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private String lpnCode(Long id) {
        if (id == null) return null;
        Lpn l = lpnMapper.selectById(id);
        return l != null ? l.getLpnCode() : null;
    }

    private String locationCode(Long id) {
        if (id == null) return null;
        Location l = locationMapper.selectById(id);
        return l != null ? l.getCode() : null;
    }

    private Map<Long, String> skuNames(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return skuMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Sku::getId, Sku::getSkuName));
    }

    private Map<Long, String> lpnCodes(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return lpnMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Lpn::getId, Lpn::getLpnCode));
    }

    /** lpnId → 托盘类型名（经 LPN.palletTypeId → PalletType.name）。 */
    private Map<Long, String> lpnPalletTypeNames(List<Long> lpnIds) {
        if (lpnIds.isEmpty()) return Map.of();
        List<Lpn> lpns = lpnMapper.selectBatchIds(lpnIds);
        List<Long> ptIds = lpns.stream().map(Lpn::getPalletTypeId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> ptNames = ptIds.isEmpty() ? Map.of()
                : palletTypeMapper.selectBatchIds(ptIds).stream().collect(Collectors.toMap(PalletType::getId, PalletType::getName));
        return lpns.stream()
                .filter(l -> l.getPalletTypeId() != null && ptNames.containsKey(l.getPalletTypeId()))
                .collect(Collectors.toMap(Lpn::getId, l -> ptNames.get(l.getPalletTypeId())));
    }

    private Map<Long, String> locationCodes(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return locationMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Location::getId, Location::getCode));
    }
}
