package com.sparta.springauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Email
    @NotBlank
    private String email;
    // admin 인지 아닌지 확인
    // 일반사용자로 회원가입하겠다고 하면 false
    // 관리자로 회원가입하겠다고 하면 true 로 바뀐다??
    private boolean admin = false;
    private String adminToken = "";
}