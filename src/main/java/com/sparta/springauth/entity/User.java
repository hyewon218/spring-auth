package com.sparta.springauth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동증가 auto impliment
    private Long id;

    // unique = true : username 을 고유한 값으로 받는다.(중복x)
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    // @Enumerated : EnumType 을 DB 컬럼에 저장할 때 사용하는 애너테이션
    @Enumerated(value = EnumType.STRING) // Enum의 이름을 DB에 그대로 저장
    private UserRoleEnum role;

    public User(String username, String password, String email, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}