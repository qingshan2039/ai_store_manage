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
}
