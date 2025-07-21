package com.doublez.common.message.service;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender mailSender;

    public void sendSimpleMail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("doublez's oj平台验证码");
        message.setText("您好，您的注册验证码是: " + code +
                "，有效期10分钟，请勿泄露给他人。");
        mailSender.send(message);
    }
}
