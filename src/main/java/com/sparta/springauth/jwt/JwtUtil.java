package com.sparta.springauth.jwt;

import com.sparta.springauth.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization"; // Cookie 의 Name 값
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    // application.properties 에 있는 값을 가져오는 방법 {key값}
    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        // secretKey 값이 Base64 로 Encoding 한 값이기 때문에 secretKey 를 사용하려면 Decoding 을 해줘야 한다.
        // byte 배열 타입으로 Decoding 한 값을 반환
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT 생성 (토큰 생성)
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX + // Bearer 과 공백 추가로 붙여줌
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        // 나중에 권한을 가져오고 싶다면 Key 값으로 claim 에서 꺼내서 사용할 수 있다.
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        // 생성되는 시점 기준으로 60분을 처리하기 위해
                        // date.getTime() : 현재 시간 + TOKEN_TIME : 60분
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }
    // 생성된 JWT 를 Cookie 에 저장
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            // Cookie Value 에는 공백이 불가능해서 encoding 진행
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            // Cookie 만들기
            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            // 다 만들어서 HttpServletResponse 객체에 담아준다. -> 클라이언트로 자연스럽게 반환이 된다.
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }
    // 받아온 Cookie 의 Value 인 JWT 토큰 substring
    // Bearer 과 공백 추가로 붙여 줬던 것 떼어내기 위해
    public String substringToken(String tokenValue) {
        // 공백, null 을 확인하고 startsWith 을 사용하여 토큰의 시작값이 Bearer 이 맞는지 확인
        // 토큰은 무조건 Bearer 로 시작하기 때문에
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            // Bearer 에 공백까지 잘라줌 -> 순수한 Token 값만 나옴
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }
    // JWT 검증(토큰 검증)
    public boolean validateToken(String token) {
        try {
            // Token 의 위변조가 있는지, 만료가 되지 않았는지 등등에 검증을 추가로 할 수 있다.
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }
    // JWT(토큰) 에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        // getBody() : Body 부분에 들어있는 Claims(데이터들이 들어있는 집합) 를 가지고 올 수 있다.
        // JWT : Claim 기반 Web Token
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // HttpServletRequest 에서 Cookie Value : JWT 가져오기
    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}