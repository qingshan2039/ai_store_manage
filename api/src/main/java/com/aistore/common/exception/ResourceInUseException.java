package com.aistore.common.exception;

/**
 * 资源被占用异常（HTTP 409）
 * 用于删除被其他资源引用的实体等场景，如部门下仍有用户
 */
public class ResourceInUseException extends BusinessException {

    public ResourceInUseException(String code, String message) {
        super(code, message);
    }

    /**
     * 快捷构造：部门下仍有用户，无法删除
     */
    public static ResourceInUseException departmentInUse() {
        return new ResourceInUseException("DEPARTMENT_IN_USE", "该部门下仍有用户，无法删除");
    }
}
