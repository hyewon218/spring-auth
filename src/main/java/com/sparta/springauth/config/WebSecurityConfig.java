package com.sparta.springauth.config;

import com.sparta.springauth.jwt.JwtAuthorizationFilter;
import com.sparta.springauth.jwt.JwtAuthenticationFilter;
import com.sparta.springauth.jwt.JwtUtil;
import com.sparta.springauth.security.UserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    // 생성자로 주입받아 옴
    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    // AuthenticationManager 만들고 등록하는 메서드
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        // getAuthenticationManager()로 AuthenticationManager 가 생성됨
        return configuration.getAuthenticationManager();
    }

    @Bean
    // 인증 Filter 등록하는 메서드
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        // authenticationManager 가 세팅이 된다.
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        // 세팅이 된 객체를 반환한다.
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        http.formLogin((formLogin) ->
                formLogin
                        .loginPage("/api/user/login-page").permitAll()
        );

        // 필터 관리
        // jwtAuthorizationFilter() 는 JwtAuthenticationFilter 앞에 둔다.
        // 인가하는 필터를 앞에다 둔 이유는? 로그인 하기 전에 먼저 인가를 하고, 인가가 진행되지 않았으면 로그인을 진행하고
        // Token 검증이 완료가 되고 인증 처리가 되면 그 뒤에 Filter 는 문제없이 실행이 됨
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        // UsernamePasswordAuthenticationFilter 가 수행되기 전에 jwtAuthenticationFilter() 를 먼저 수행하겠다.
        // 로그인하는 필터를 두번째로 둠
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 접근 불가 페이지
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling
                        // 거부당했을 때 보내는 페이지
                        .accessDeniedPage("/forbidden.html")
        );

        return http.build();
    }
}