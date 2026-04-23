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
}
