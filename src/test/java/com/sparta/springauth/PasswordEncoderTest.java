package com.sparta.springauth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    // PasswordEncoder(인터페이스, 다형성의 원리, 타입만 이걸로 받아줌) BCryptPasswordEncoder 구현체가 passwordEncoder 로 들어옴
    PasswordEncoder passwordEncoder; // passwordEncoder bean으로 만들어줬으니 주입 받아 와서 사용

    @Test
    @DisplayName("수동 등록한 passwordEncoder를 주입 받아와 문자열 암호화")
    void test1() {
        String password = "Robbie's password";

        // 암호화
        String encodePassword = passwordEncoder.encode(password);
        System.out.println("encodePassword = " + encodePassword);

        String inputPassword = "Robbie";

        // 복호화를 통해 암호화된 비밀번호와 비교
        boolean matches = passwordEncoder.matches(inputPassword, encodePassword);
        System.out.println("matches = " + matches); // 암호화할 때 사용된 값과 다른 문자열과 비교했기 때문에 false
    }
}
