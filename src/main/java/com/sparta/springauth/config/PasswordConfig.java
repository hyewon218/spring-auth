package com.sparta.springauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig { // Bean 으로 passwordConfig 로 저장됨

    @Bean
    public PasswordEncoder passwordEncoder() { // Bean 으로 passwordEncoder 로 저장됨
        return new BCryptPasswordEncoder();
    }
}