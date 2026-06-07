package com.aistore.common.exception;

/**
 * 资源未找到异常（HTTP 404）
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String code, String message) {
        super(code, message);
    }

    /**
     * 快捷构造：用户不存在
     */
    public static ResourceNotFoundException userNotFound() {
        return new ResourceNotFoundException("USER_NOT_FOUND", "用户不存在");
    }

    /**
     * 快捷构造：部门不存在
     */
    public static ResourceNotFoundException departmentNotFound() {
        return new ResourceNotFoundException("DEPARTMENT_NOT_FOUND", "部门不存在");
    }

    /**
     * 快捷构造：顾客不存在
     */
    public static ResourceNotFoundException customerNotFound() {
        return new ResourceNotFoundException("CUSTOMER_NOT_FOUND", "顾客不存在");
    }

    public static ResourceNotFoundException supplierNotFound() {
        return new ResourceNotFoundException("SUPPLIER_NOT_FOUND", "供应商不存在");
    }

    public static ResourceNotFoundException warehouseNotFound() {
        return new ResourceNotFoundException("WAREHOUSE_NOT_FOUND", "仓库不存在");
    }

    public static ResourceNotFoundException zoneNotFound() {
        return new ResourceNotFoundException("ZONE_NOT_FOUND", "库区不存在");
    }

    public static ResourceNotFoundException palletTypeNotFound() {
        return new ResourceNotFoundException("PALLET_TYPE_NOT_FOUND", "托盘类型不存在");
    }

    public static ResourceNotFoundException materialCategoryNotFound() {
        return new ResourceNotFoundException("MATERIAL_CATEGORY_NOT_FOUND", "物料品类不存在");
    }

    public static ResourceNotFoundException spuNotFound() {
        return new ResourceNotFoundException("SPU_NOT_FOUND", "SPU 不存在");
    }

    public static ResourceNotFoundException skuNotFound() {
        return new ResourceNotFoundException("SKU_NOT_FOUND", "SKU 不存在");
    }

    public static ResourceNotFoundException packagingLevelNotFound() {
        return new ResourceNotFoundException("PACKAGING_LEVEL_NOT_FOUND", "包装层级不存在");
    }

    public static ResourceNotFoundException packagingRelationNotFound() {
        return new ResourceNotFoundException("PACKAGING_RELATION_NOT_FOUND", "包装关系不存在");
    }

    public static ResourceNotFoundException barcodeNotFound() {
        return new ResourceNotFoundException("BARCODE_NOT_FOUND", "条码不存在");
    }

    public static ResourceNotFoundException unitConversionNotFound() {
        return new ResourceNotFoundException("UNIT_CONVERSION_NOT_FOUND", "计量换算不存在");
    }

    public static ResourceNotFoundException itemImageNotFound() {
        return new ResourceNotFoundException("ITEM_IMAGE_NOT_FOUND", "物料图片不存在");
    }

    public static ResourceNotFoundException locationNotFound() {
        return new ResourceNotFoundException("LOCATION_NOT_FOUND", "库位不存在");
    }

    public static ResourceNotFoundException lpnNotFound() {
        return new ResourceNotFoundException("LPN_NOT_FOUND", "托盘实例不存在");
    }

    public static ResourceNotFoundException inventoryNotFound() {
        return new ResourceNotFoundException("INVENTORY_NOT_FOUND", "库存记录不存在");
    }
}
