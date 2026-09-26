
package com.example.bbs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * セキュリティ設定クラス
 * SecurityConfig
 */
@Configuration 
public class SecurityConfig {

    /**
     * セキュリティフィルタチェーンの設定
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/auth/register", "/auth/login", "/contact/**").permitAll() // /registerと/loginは認証不要
                .anyRequest().authenticated() // その他のリクエストは認証が必要
            )
            .formLogin(form -> form
                .loginPage("/auth/login") // カスタムログインページのURL
                .defaultSuccessUrl("/posts", true) // ログイン成功後のリダイレクト先
                .permitAll() // ログインページは認証不要
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout") // カスタムログアウトURL
                .logoutSuccessUrl("/auth/login?logout") // カスタムログアウトURL
                .invalidateHttpSession(true) // ログアウト時にセッションを無効化
                .deleteCookies("JSESSIONID") // ログアウト時にクッキーを削除
                .permitAll() // ログアウトは認証不要
            );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
