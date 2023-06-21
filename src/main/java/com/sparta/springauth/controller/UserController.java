package com.sparta.springauth.controller;

import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
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
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        // getFieldErrors() : 오류가 난 field 들을 하나씩 가지고 올 수 있다.
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0) {
            // for 문을 돌리면서 List 안에 들어 있던 fieldError 를 하나씩 뽑아온다.
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                // getField() : 어떤 오류가 발생하는지 그 해당하는 Field 를 가지고 올 수 있다.
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            // if 문 안에 들어왔다는 거는 오류가 하나 이상 발생을 했다라는 의미니 다시 회원가입하라는 의미로
            return "redirect:/api/user/signup";
        }
        // userService 에 받아온 request 전달
        userService.signup(requestDto);

        // 회원가입이 완료 되면 로그인페이지로 반환
        return "redirect:/api/user/login-page";
    }
}