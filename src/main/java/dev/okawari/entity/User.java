package dev.okawari.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERS") // Oracle DB에 생성한 테이블 이름
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB의 Auto Increment(GENERATED AS IDENTITY)와 매핑
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * JPA 엔티티가 처음 저장(persist)될 때 실행됩니다.
     * 회원가입 시 현재 시간을 자동으로 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}