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
}
