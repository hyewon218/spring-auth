package com.sparta.springauth.controller;

import com.sparta.springauth.dto.LoginRequestDto;
import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class UserController {

    // UserService 로 회원가입할 때 사용할 데이터를 전달하기 위해
    private final UserService userService;
    // 생성자로 UserService 주입하기
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    // 회원가입 API
    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    public String signup(SignupRequestDto requestDto) {
        // userService 에 받아온 request 전달
        userService.signup(requestDto);

        // 회원가입이 완료 되면 로그인페이지로 반환
        return "redirect:/api/user/login-page";
    }

    // 로그인 API
    @PostMapping("/user/login")
    public String login(LoginRequestDto requestDto, HttpServletResponse res){
        // 로그인할 때 검증하라고 받아온 데이터 requestDto 보내주고
        // 검증 다 끝나면 JWTTokenCookie 에 넣고 또 그 Cookie 담으라고
        // 받아온 Response 객체도 보내준다.
        try {
            userService.login(requestDto, res);
        } catch (Exception e) {
            // 오류가 발생하면 로그인 페이지로 반환
            // 클라이언트의 요고 사항에 의해서 ?error 추가
            return "redirect:/api/user/login-page?error";
        }

        // 로그인에 성공을 한다면 main 으로
        return "redirect:/";
    }
}