package com.doublez.system.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptUtils {
    /**
     * ⽣成加密后密⽂
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后密⽂
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String
            encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("123456"));
        System.out.println(BCryptUtils.matchesPassword("123456", "$2a$10$p8WA5rFSO.rxB6uUlYH.quGYsFDjZUYqzNEQYJpPmh94gcjvultJu"));
    }
}
