package com.doublez.friend.domain.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String code;

    @Email
    private String email;
}
