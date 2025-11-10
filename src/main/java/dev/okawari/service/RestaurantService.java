package dev.okawari.service;

import dev.okawari.dto.ReviewDTO;
import dev.okawari.dto.UserDTO;
import dev.okawari.entity.Restaurant;
import dev.okawari.entity.Review;
import dev.okawari.repository.RestaurantRepository;
import dev.okawari.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 (성능 향상)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository; // (신규) 리뷰 리포지토리 주입

    /**
     * 모든 맛집 목록을 조회합니다.
     * @return 맛집 엔티티 리스트
     */
    public List<Restaurant> findAllRestaurants(String category) {
        // 카테고리가 "전체"이거나 비어있으면, 모든 맛집을 반환합니다.
        if (category == null || category.isEmpty() || category.equals("전체")) {
            return restaurantRepository.findAll();
        } else {
            // 특정 카테고리가 있으면, 해당 카테고리의 맛집만 반환합니다.
            return restaurantRepository.findByCategory(category);
        }
    }

    /**
     * (신규) 상세 페이지용: ID로 특정 맛집 1개 조회
     */
    public Restaurant findRestaurantById(Long id) {
        // ID로 맛집을 찾지 못하면 예외 발생
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("맛집 정보를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * (신규) 상세 페이지용: 특정 맛집의 모든 리뷰 목록을 DTO로 변환
     */
    public List<ReviewDTO.Response> findReviewsForDisplay(Long restaurantId, UserDTO.SessionUser sessionUser) {

        // 1. DB에서 해당 맛집의 모든 리뷰를 (작성자 정보 포함) 가져옴
        List<Review> reviews = reviewRepository.findByRestaurantIdWithUser(restaurantId);

        // 2. 리뷰 목록을 DTO로 변환 (스트림 사용)
        return reviews.stream()
                .map(review -> {
                    // 3. (중요) 이 리뷰가 현재 로그인한 사용자의 것인지 확인
                    boolean isOwner = (sessionUser != null &&
                            review.getUser().getId().equals(sessionUser.getUserId()));

                    // 4. Response DTO 생성
                    return new ReviewDTO.Response(review, isOwner);
                })
                .collect(Collectors.toList());
    }

    /**
     * (신규) 상세 페이지용: 특정 맛집의 리뷰 개수 및 평균 평점을 계산합니다.
     */
    public Map<String, Object> reviewsRating (Long restaurantId) {
        // 1. 해당 맛집의 전체 리뷰 개수 조회
        long count = reviewRepository.countByRestaurantId(restaurantId);

        // 2. 평균 평점 조회 (null 반환 가능)
        Double avg = reviewRepository.findAvgRatingByRestaurantId(restaurantId);

        // 3. 평균이 null일 경우 0.0으로 처리, 소수 1자리 반올림
        double rounded = Math.round((avg != null ? avg : 0.0) * 10.0) / 10.0;

        // 4. 결과를 Map에 담아 반환
        Map<String, Object> result = new HashMap<>();
        result.put("reviewCount", count);
        result.put("avgRating", rounded);
        return result;
    }

    /**
     * (신규) 맛집 이름으로 검색합니다.
     * 입력한 키워드가 이름에 포함된 모든 맛집을 조회하며,
     * 검색어가 없을 경우 빈 맛집 목록을 반환합니다.
     */
    public List<Restaurant> searchByName(String keyword) {

        // 1. 검색어가 비어 있거나 공백만 있는 경우 → 빈 리스트 반환
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        // 2. 검색어가 존재하는 경우 → 이름에 검색어가 포함된 맛집 조회 (대소문자 구분 X)
        return restaurantRepository.findByNameContainingIgnoreCase(keyword.trim());
    }

}