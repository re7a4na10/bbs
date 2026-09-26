package com.example.bbs.controller;

import com.example.bbs.model.ContactData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.bbs.service.EmailService;

@Controller 
@RequestMapping("/contact")
@SessionAttributes("contactData")
public class ContactController {

    private EmailService emailService;

    @Value("${app.mail.enabled}")
    private String isMailEnabled;

    /**
     * コンストラクタ
     * @param emailService
     */
    public ContactController(EmailService emailService){
        this.emailService = emailService;
    }

    /**
     * お問い合わせ送信フォーム
     * @param model
     * @return
     */
    @GetMapping
    public String contactForm(Model model) {
        if (!model.containsAttribute("contactData")){
            model.addAttribute("contactData", new ContactData());
        }

        // メール機能（有効/無効）
        model.addAttribute("isMailEnabled", isMailEnabled);
        return "contact/form";
    }

    /**
     * お問い合わせ確認画面
     * @param name
     * @param email
     * @param message
     * @param model
     * @return
     */
    @PostMapping("/confirm")
    public String comfirmContact(
        @RequestParam("name") String name,
        @RequestParam("email") String email,
        @RequestParam("message") String message,
        Model model
    ) {
        model.addAttribute("contactData", new ContactData(name, email, message));
        
        return "contact/confirm";
    }

    /**
     * お問い合わせ送信
     * @param name
     * @param email
     * @param message
     * @return
     */
    @PostMapping("/submit")
    public String submitContact(
        @RequestParam("name") String name,
        @RequestParam("email") String email,
        @RequestParam("message") String message
    ) {
        // ユーザーに送信するメール内容
        String userSubject = "お問い合わせありがとうございます。";
        String userBody = String.format(
            "お問い合わせありがとうございます。\n以下の内容で受け付けました。\n\n[お問い合わせ内容]\n%s", message);

        // 運営者に送信するメール内容
        String adminSubject = "新しいお問い合わせが届きました。";
        String adminBody = String.format(
            "新しいお問い合わせが届きました。\n\n[お名前]：%s\n[メールアドレス]：%s\n[お問い合わせ内容]\n%s", name, email, message);

        // ユーザー宛てにメール送信
        emailService.sendUserEmail(email, userSubject, userBody);
        // 運営者宛てにメール送信
        emailService.sendAdminEmail(adminSubject, adminBody);
        
        return "redirect:/contact/complete";
    }

    /**
     * お問い合わせ完了画面
     * @return
     */
    @GetMapping("/complete")
    public String completeForm() {
        return "contact/complete";
    }
}
