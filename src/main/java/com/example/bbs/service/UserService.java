package com.example.bbs.service;

import com.example.bbs.model.User;
import com.example.bbs.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Service 
public class UserService {
    private final UserRepository userRepository;

    /**
     * コンストラクタ
     * @param userRepository
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 現在ログインしているユーザーを取得する
     * @return
     */
    public User getCurrentUser() {

        // 現在の認証情報からユーザーを取得
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // principalがUserDetailsのインスタンスであるか確認
        if(principal instanceof UserDetails) {
            // ユーザー名を取得
            String username = ((UserDetails) principal).getUsername();
            // ユーザー名でユーザーを検索して返す
            return userRepository.findByUsername(username).orElseThrow();
        }
        // principalがUserDetailsのインスタンスでない場合は例外を投げる
        throw new IllegalStateException("User not logged in");
    }
}
