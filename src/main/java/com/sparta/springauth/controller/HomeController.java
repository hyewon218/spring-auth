package com.sparta.springauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
// main 페이지에 가기 위해 만들어 놓은 Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // 구분할 수 있는 key 값, 실제 넣어줄 Value 값
        model.addAttribute("username", "username");
        return "index";
    }
}