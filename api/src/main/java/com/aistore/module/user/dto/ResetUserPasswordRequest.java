package com.aistore.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重置用户密码请求 DTO
 * 严格对齐 OpenAPI 契约 ResetUserPasswordRequest Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetUserPasswordRequest {

    /**
     * 新密码（8~32位，至少含字母和数字）
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度必须在8-32个字符之间")
    private String newPassword;
}
