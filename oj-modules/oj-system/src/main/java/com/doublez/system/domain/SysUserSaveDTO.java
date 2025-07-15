package com.doublez.system.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserSaveDTO {
    @Schema(description = "用户账号")
    private String userAccount;
    @Schema(description = "密码")
    private String password;
}
