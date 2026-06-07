package com.aistore.module.lpn.dto;

import com.aistore.module.lpn.enums.LpnStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 托盘状态变更请求 DTO（在库/在途/空置） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLpnStatusRequest {

    @NotNull(message = "状态不能为空")
    private LpnStatus status;
}
