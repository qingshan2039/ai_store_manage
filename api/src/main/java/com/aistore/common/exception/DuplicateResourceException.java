package com.aistore.common.exception;

/**
 * 资源冲突异常（HTTP 409）
 * 用于用户名或工号已存在等唯一性冲突场景
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String code, String message) {
        super(code, message);
    }

    /**
     * 快捷构造：用户名已存在
     */
    public static DuplicateResourceException duplicateUsername() {
        return new DuplicateResourceException("DUPLICATE_USERNAME", "用户名已存在");
    }

    /**
     * 快捷构造：工号已存在
     */
    public static DuplicateResourceException duplicateEmployeeNo() {
        return new DuplicateResourceException("DUPLICATE_EMPLOYEE_NO", "工号已存在");
    }

    /**
     * 快捷构造：部门名称已存在
     */
    public static DuplicateResourceException duplicateDepartmentName() {
        return new DuplicateResourceException("DUPLICATE_DEPARTMENT_NAME", "部门名称已存在");
    }

    /**
     * 快捷构造：部门编码已存在
     */
    public static DuplicateResourceException duplicateDepartmentCode() {
        return new DuplicateResourceException("DUPLICATE_DEPARTMENT_CODE", "部门编码已存在");
    }

    /**
     * 快捷构造：客户名称已存在
     */
    public static DuplicateResourceException duplicateCustomerName() {
        return new DuplicateResourceException("DUPLICATE_CUSTOMER_NAME", "客户名称已存在");
    }

    /**
     * 快捷构造：客户编码已存在
     */
    public static DuplicateResourceException duplicateCustomerCode() {
        return new DuplicateResourceException("DUPLICATE_CUSTOMER_CODE", "客户编码已存在");
    }

    public static DuplicateResourceException duplicateSupplierName() {
        return new DuplicateResourceException("DUPLICATE_SUPPLIER_NAME", "供应商名称已存在");
    }

    public static DuplicateResourceException duplicateSupplierCode() {
        return new DuplicateResourceException("DUPLICATE_SUPPLIER_CODE", "供应商编码已存在");
    }

    public static DuplicateResourceException duplicateWarehouseName() {
        return new DuplicateResourceException("DUPLICATE_WAREHOUSE_NAME", "仓库名称已存在");
    }

    public static DuplicateResourceException duplicateWarehouseCode() {
        return new DuplicateResourceException("DUPLICATE_WAREHOUSE_CODE", "仓库编码已存在");
    }

    public static DuplicateResourceException duplicateZoneCode() {
        return new DuplicateResourceException("DUPLICATE_ZONE_CODE", "同一仓库下库区编码已存在");
    }

    public static DuplicateResourceException duplicatePalletTypeName() {
        return new DuplicateResourceException("DUPLICATE_PALLET_TYPE_NAME", "托盘类型名称已存在");
    }

    public static DuplicateResourceException duplicatePalletTypeCode() {
        return new DuplicateResourceException("DUPLICATE_PALLET_TYPE_CODE", "托盘类型编码已存在");
    }

    public static DuplicateResourceException duplicateMaterialCategoryName() {
        return new DuplicateResourceException("DUPLICATE_MATERIAL_CATEGORY_NAME", "物料品类名称已存在");
    }

    public static DuplicateResourceException duplicateMaterialCategoryCode() {
        return new DuplicateResourceException("DUPLICATE_MATERIAL_CATEGORY_CODE", "物料品类编码已存在");
    }

    public static DuplicateResourceException duplicateSpuName() {
        return new DuplicateResourceException("DUPLICATE_SPU_NAME", "SPU 名称已存在");
    }

    public static DuplicateResourceException duplicateSpuCode() {
        return new DuplicateResourceException("DUPLICATE_SPU_CODE", "SPU 编码已存在");
    }

    public static DuplicateResourceException duplicateSkuCode() {
        return new DuplicateResourceException("DUPLICATE_SKU_CODE", "SKU 编码已存在");
    }

    public static DuplicateResourceException duplicateVehiclePlate() {
        return new DuplicateResourceException("DUPLICATE_VEHICLE_PLATE", "车牌号已存在");
    }

    public static DuplicateResourceException duplicateDriverCheckin() {
        return new DuplicateResourceException("DUPLICATE_DRIVER_CHECKIN", "该司机当天已有打卡记录");
    }

    public static DuplicateResourceException duplicatePackagingLevelSeq() {
        return new DuplicateResourceException("DUPLICATE_PACKAGING_LEVEL_SEQ", "同一 SKU 下该层级序号已存在");
    }

    public static DuplicateResourceException duplicatePackagingRelation() {
        return new DuplicateResourceException("DUPLICATE_PACKAGING_RELATION", "该父子层包装关系已存在");
    }

    public static DuplicateResourceException duplicateBarcode() {
        return new DuplicateResourceException("DUPLICATE_BARCODE", "条码已存在");
    }

    public static DuplicateResourceException duplicateUnitConversion() {
        return new DuplicateResourceException("DUPLICATE_UNIT_CONVERSION", "该 SKU 的该单位换算已存在");
    }

    public static DuplicateResourceException duplicateLocationCode() {
        return new DuplicateResourceException("DUPLICATE_LOCATION_CODE", "同一仓库下库位编码已存在");
    }

    public static DuplicateResourceException duplicateLpnCode() {
        return new DuplicateResourceException("DUPLICATE_LPN_CODE", "托盘号已存在");
    }
}
