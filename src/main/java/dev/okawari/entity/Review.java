package dev.okawari.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "REVIEWS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자가 필요합니다.
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "rating", nullable = false)
    private int rating;

    // CLOB 타입(대용량 텍스트)으로 매핑합니다.
    @Column(name = "content", columnDefinition = "CLOB", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- 연관 관계 ---
    // N:1 (Review:User), LAZY=지연 로딩
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 이 리뷰를 쓴 작성자

    // N:1 (Review:Restaurant), LAZY=지연 로딩
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant; // 이 리뷰가 달린 맛집

    // --- 생성자 (Builder 패턴 대신 간단한 정적 팩토리 메서드) ---
    public static Review createReview(User user, Restaurant restaurant, int rating, String content) {
        Review review = new Review();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setRating(rating);
        review.setContent(content);
        return review;
    }

    // --- 헬퍼 메서드 ---
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}