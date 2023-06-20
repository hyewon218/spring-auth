package com.sparta.springauth.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Slf4j(topic = "LoggingFilter")
//@Component
@Order(1) // filter 에 순서를 지정할 수 있다.
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 전처리
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = httpServletRequest.getRequestURI();
        // 어떤 요청인지 로그 찍음
        log.info(url);

        chain.doFilter(request, response); // 다음 Filter 로 이동(AuthFilter 의 dofilter)

        // 후처리
        log.info("비즈니스 로직 완료");
    }
}