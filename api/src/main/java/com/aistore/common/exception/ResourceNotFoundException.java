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

    public static ResourceNotFoundException vehicleNotFound() {
        return new ResourceNotFoundException("VEHICLE_NOT_FOUND", "车辆不存在");
    }

    public static ResourceNotFoundException fuelRecordNotFound() {
        return new ResourceNotFoundException("FUEL_RECORD_NOT_FOUND", "打油记录不存在");
    }

    public static ResourceNotFoundException driverCheckinNotFound() {
        return new ResourceNotFoundException("DRIVER_CHECKIN_NOT_FOUND", "打卡记录不存在");
    }
}
