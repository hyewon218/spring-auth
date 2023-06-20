package com.sparta.springauth.controller;

import com.sparta.springauth.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
// main 페이지에 가기 위해 만들어 놓은 Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 구분할 수 있는 key 값, 실제 넣어줄 Value 값
        model.addAttribute("username", userDetails.getUsername()); // 실제로 로그인을 한 user 의 이름을 가져온다.(동적)
        return "index";
    }
}
