package com.sparta.springauth.controller;

import com.sparta.springauth.entity.User;
import com.sparta.springauth.entity.UserRoleEnum;
import com.sparta.springauth.security.UserDetailsImpl;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ProductController {

    @GetMapping("/products")
    public String getProducts(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        // Authentication 의 Principal 부분에 userDetails 들어감
        // 그 userDetails 에 들어가 있는 userDetails 를 가지고 오는 것
        User user = userDetails.getUser();
        System.out.println("user.getUsername() = " + user.getUsername());
        System.out.println("user.getEmail() = " + user.getEmail());

        return "redirect:/";
    }

    @Secured(UserRoleEnum.Authority.ADMIN) // 관리자용
    @GetMapping("/products/secured")
    public String getProductsByAdmin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
        // 권한이 여러개이므로 forEach 문 돌려서 하나씩 뽑아서 찍는다.
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            System.out.println("authority.getAuthority() = " + authority.getAuthority());
        }
        return "redirect:/";
    }
}
