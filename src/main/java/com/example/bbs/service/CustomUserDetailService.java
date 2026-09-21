package com.example.bbs.service;

import com.example.bbs.model.User;
import com.example.bbs.repository.UserRepository;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service 
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * コンストラクタ
     * @param userRepository
     * @param passwordEncoder
     */
    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * ユーザー名でユーザーを検索し、UserDetailsを返す
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), 
            user.getPassword(), 
            new ArrayList<>());
    }

    /**
     * ユーザーを登録する
     * @param user
     */
    public void registerUser(User user) {
        // パスワードをエンコードして保存
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // ユーザーを保存
        userRepository.save(user);
    }
}
