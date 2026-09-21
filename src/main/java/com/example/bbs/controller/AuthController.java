package com.example.bbs.controller;

import com.example.bbs.model.User;
import com.example.bbs.service.CustomUserDetailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final CustomUserDetailService userDetailService;

    public AuthController(CustomUserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    // ログインページの表示
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // ユーザー登録ページの表示
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    // ユーザー登録処理
    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        userDetailService.registerUser(user);
        
        return "redirect:/auth/login";
    }
}
