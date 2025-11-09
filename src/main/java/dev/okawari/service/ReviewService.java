package dev.okawari.service;

import dev.okawari.dto.ReviewDTO;
import dev.okawari.entity.Restaurant;
import dev.okawari.entity.Review;
import dev.okawari.entity.User;
import dev.okawari.repository.RestaurantRepository;
import dev.okawari.repository.ReviewRepository;
import dev.okawari.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional // CUD 작업이 있으므로 클래스 레벨에 @Transactional 적용 (readOnly=false)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    /**
     * 리뷰 생성
     */
    public void createReview(ReviewDTO.CreateRequest request, Long restaurantId, Long userId) {
        // 1. 리뷰를 쓸 사용자(User)와 대상 맛집(Restaurant) 엔티티를 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("맛집 정보를 찾을 수 없습니다. ID: " + restaurantId));

        // 2. Review 엔티티 생성 (정적 팩토리 메서드 사용)
        Review review = Review.createReview(user, restaurant, request.getRating(), request.getContent());

        // 3. DB에 저장
        reviewRepository.save(review);
    }

    /**
     * 리뷰 수정
     */
    public void updateReview(ReviewDTO.UpdateRequest request, Long reviewId, Long userId) {
        // 1. 수정할 리뷰를 (작성자 정보 포함) 조회
        Review review = reviewRepository.findByIdWithUser(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + reviewId));

        // 2. (보안) 본인 확인: 리뷰 작성자 ID와 현재 로그인한 사용자 ID가 같은지 확인
        checkReviewOwner(review, userId);

        // 3. 내용 수정 (JPA의 Dirty Checking 활용)
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        // @PreUpdate가 updatedAt을 자동 갱신. (save 호출 안해도 트랜잭션 종료 시 자동 반영됨)
    }

    /**
     * 리뷰 삭제
     */
    public void deleteReview(Long reviewId, Long userId) {
        // 1. 삭제할 리뷰를 (작성자 정보 포함) 조회
        Review review = reviewRepository.findByIdWithUser(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + reviewId));

        // 2. (보안) 본인 확인
        checkReviewOwner(review, userId);

        // 3. DB에서 삭제
        reviewRepository.delete(review);
    }

    /**
     * (공통) 리뷰 소유권 검사 메서드
     */
    private void checkReviewOwner(Review review, Long userId) {
        if (!review.getUser().getId().equals(userId)) {
            // (보안) 본인이 아닐 경우, 강력한 예외를 발생시켜 작업을 중단합니다.
            throw new SecurityException("이 리뷰를 수정/삭제할 권한이 없습니다.");
        }
    }
}