package com.sparta.springauth.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api")
public class AuthController {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @GetMapping("/create-cookie")
    public String createCookie(HttpServletResponse res) {
        // cookieValue : 저장할 value
        addCookie("Robbie Auth", res);

        return "createCookie";
    }

    @GetMapping("/get-cookie")
    // HttpServletRequest? 에 들어있는 쿠키 중에서 Authorization 이라는 이름으로 된 쿠키를 @CookieValue 을 통해 가져온다.
    // @CookieValue 에 우리가 가지고 오고 싶은 쿠키의 이름을 넣으면 변수에 해당하는 쿠키의 값이 들어가게 됨. -> Robbie Auth
    public String getCookie(@CookieValue(AUTHORIZATION_HEADER) String value) {
        System.out.println("value = " + value);

        return "getCookie : " + value;
    }

    // 세션 만들기
    @GetMapping("/create-session")
    // HttpServletRequest 파라미터로 받아오기
    // HttpServletRequest : Servlet 에서 요청이 들어왔을 때 Request 객체를 만들어 준다.
    public String createSession(HttpServletRequest req) {
        // getSession(true) : 세션이 존재할 경우 세션 반환, 없을 경우 새로운 세션을 생성한 후 반환
        //  세션이 존재한다면 Request 객체에 담겨서 넘어옴? -> 그 세션을 다시 반환한다.
        HttpSession session = req.getSession(true);

        // 세션에 저장될 정보 Name - Value 를 넣어준다.
        // 유일무이한 ID 를 만들어서 반환을 함
        session.setAttribute(AUTHORIZATION_HEADER, "Robbie Auth");

        return "createSession";
    }

    @GetMapping("/get-session")
    public String getSession(HttpServletRequest req) {
        // getSession(false) : 세션이 존재할 경우 세션 반환, 없을 경우 null 반환
        HttpSession session = req.getSession(false);

        // 가져온 세션에 저장된 Value 를 Name 을 사용하여 가져온다.
        // getAttribute 가 Object 로 반환을 해서 (String) 으로 캐스팅
        String value = (String) session.getAttribute(AUTHORIZATION_HEADER);
        System.out.println("value = " + value);

        return "getSession : " + value;
    }

    // 쿠키 저장 메서드
    public static void addCookie(String cookieValue, HttpServletResponse res) {
        try {
            // Cookie Value 에는 공백이 불가능해서 encoding 진행
            cookieValue = URLEncoder.encode(cookieValue, "utf-8").replaceAll("\\+", "%20");

            // 인코딩한 cookieValue 를 Cookie class 에 생성자 파라미터로 담는다.
            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue); // Name-Value
            cookie.setPath("/");
            cookie.setMaxAge(30 * 60);

            // Response 객체에 Cookie 추가
            // 다 만들어서 HttpServletResponse 객체에 담아준다. -> 클라이언트로 자연스럽게 반환이 된다.
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
