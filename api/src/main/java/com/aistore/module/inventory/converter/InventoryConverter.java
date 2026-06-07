package com.aistore.module.inventory.converter;

import com.aistore.module.inventory.dto.CreateInventoryRequest;
import com.aistore.module.inventory.dto.UpdateInventoryRequest;
import com.aistore.module.inventory.entity.Inventory;
import com.aistore.module.inventory.vo.InventorySummaryItemVO;
import com.aistore.module.inventory.vo.InventoryVO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** 库存对象转换器；关联名由 Service 传入。qtyAvailable = qtyOnHand - qtyReserved。 */
@Component
public class InventoryConverter {

    public Inventory toEntity(CreateInventoryRequest r) {
        return Inventory.builder()
                .skuId(r.getSkuId())
                .lpnId(r.getLpnId())
                .locationId(r.getLocationId())
                .lotNo(r.getLotNo())
                .mfgDate(r.getMfgDate())
                .expDate(r.getExpDate())
                .qtyOnHand(r.getQtyOnHand())
                .qtyReserved(r.getQtyReserved() != null ? r.getQtyReserved() : BigDecimal.ZERO)
                .deleted(0)
                .build();
    }

    public InventoryVO toVO(Inventory e, String skuCode, String skuName, String lpnCode, String locationCode) {
        return InventoryVO.builder()
                .id(e.getId()).skuId(e.getSkuId()).skuCode(skuCode).skuName(skuName)
                .lpnId(e.getLpnId()).lpnCode(lpnCode).locationId(e.getLocationId()).locationCode(locationCode)
                .lotNo(e.getLotNo()).mfgDate(e.getMfgDate()).expDate(e.getExpDate())
                .qtyOnHand(e.getQtyOnHand()).qtyReserved(e.getQtyReserved()).qtyAvailable(available(e))
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .build();
    }

    public InventorySummaryItemVO toSummaryItemVO(Inventory e, String skuName, String lpnCode, String palletTypeName, String locationCode) {
        return InventorySummaryItemVO.builder()
                .id(e.getId()).skuId(e.getSkuId()).skuName(skuName).lpnCode(lpnCode).palletTypeName(palletTypeName).locationCode(locationCode)
                .lotNo(e.getLotNo()).qtyOnHand(e.getQtyOnHand()).qtyReserved(e.getQtyReserved()).qtyAvailable(available(e))
                .createdAt(e.getCreatedAt())
                .build();
    }

    public void updateEntity(Inventory e, UpdateInventoryRequest r) {
        if (r.getLpnId() != null) e.setLpnId(r.getLpnId());
        if (r.getLocationId() != null) e.setLocationId(r.getLocationId());
        if (r.getLotNo() != null) e.setLotNo(r.getLotNo());
        if (r.getMfgDate() != null) e.setMfgDate(r.getMfgDate());
        if (r.getExpDate() != null) e.setExpDate(r.getExpDate());
        if (r.getQtyOnHand() != null) e.setQtyOnHand(r.getQtyOnHand());
        if (r.getQtyReserved() != null) e.setQtyReserved(r.getQtyReserved());
    }

    private BigDecimal available(Inventory e) {
        BigDecimal onHand = e.getQtyOnHand() != null ? e.getQtyOnHand() : BigDecimal.ZERO;
        BigDecimal reserved = e.getQtyReserved() != null ? e.getQtyReserved() : BigDecimal.ZERO;
        return onHand.subtract(reserved);
    }
}
