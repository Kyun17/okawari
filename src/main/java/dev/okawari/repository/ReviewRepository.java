package dev.okawari.repository;

import dev.okawari.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 맛집(restaurantId)에 달린 모든 리뷰를
     * 작성일(createdAt) 기준 내림차순(DESC)으로 정렬하여 조회합니다.
     *
     * (JPQL) N+1 문제를 피하기 위해 fetch join으로 User(작성자) 정보도 함께 가져옵니다.
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.restaurant.id = :restaurantId ORDER BY r.createdAt DESC")
    List<Review> findByRestaurantIdWithUser(@Param("restaurantId") Long restaurantId);

    /**
     * (신규) 리뷰 수정/삭제 시, 본인 확인을 위해 User 정보와 함께 리뷰를 조회합니다.
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.id = :reviewId")
    Optional<Review> findByIdWithUser(@Param("reviewId") Long reviewId);

    /**
     * 특정 맛집(restaurantId)에 해당하는 리뷰 갯수 집계
     */
    long countByRestaurantId(Long restaurantId);

    /**
     *  특정 맛집(restaurantId)에 해당하는 리뷰 평점 집계
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Double findAvgRatingByRestaurantId(@Param("restaurantId") Long restaurantId);
}