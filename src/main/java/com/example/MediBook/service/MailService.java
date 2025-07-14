package com.example.MediBook.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {
    JavaMailSender javaMailSender;

    @Async("mailExecutor")
    public void sendMail(SimpleMailMessage message) {
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println("メール送信エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
