package com.doublez.friend.service;

import jakarta.validation.constraints.Email;

public interface IUserService {
    boolean logout(String token);

//    String codeLogin(String phone, String code);

    boolean sendCode(@Email String email);
}
