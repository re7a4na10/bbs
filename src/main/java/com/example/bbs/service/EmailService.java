package com.example.bbs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Emailサービス
 */
@Service
public class EmailService {
    private JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromAddress;

    @Value("${mail.admin}")
    private String adminAddress;

    /**
     * コンストラクタ
     * @param mailSender
     */
    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    /**
     * ユーザにメール送信
     * @param to
     * @param Subject
     * @param text
     */
    public void sendUserEmail(String to, String Subject, String text) {
        sendEmail(to, Subject, text);
    }

    /**
     * 運営者にメール送信
     * @param Subject
     * @param text
     */
    public void sendAdminEmail(String Subject, String text) {
        sendEmail(adminAddress, Subject, text);
    }

    /**
     * メール送信
     * @param to
     * @param Subject
     * @param text
     */
    public void sendEmail(String to, String Subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(Subject);
        message.setText(text);
        message.setFrom(fromAddress);
        mailSender.send(message);
    }
}
