package com.doublez.system.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDTO {
    @Schema(description = "用户账号")
    String userAccount;
    @Schema(description = "用户密码")
    String password;
}
