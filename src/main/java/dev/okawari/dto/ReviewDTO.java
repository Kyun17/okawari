package dev.okawari.dto;

import dev.okawari.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

public class ReviewDTO {

    /**
     * [Response DTO]
     * 상세 페이지(detail.html)에 리뷰 목록을 보여줄 때 사용할 DTO
     */
    @Getter
    public static class Response {
        private final Long reviewId;
        private final String nickname;
        private final int rating;
        private final String content;
        private final String createdAtFormatted;
        private final boolean isOwner; // (중요) 현재 로그인한 사용자가 이 리뷰의 주인인지 여부

        // Service에서 Review 엔티티와 isOwner 여부를 받아 DTO를 생성
        public Response(Review review, boolean isOwner) {
            this.reviewId = review.getId();
            this.nickname = review.getUser().getNickname();
            this.rating = review.getRating();
            this.content = review.getContent();
            this.createdAtFormatted = review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            this.isOwner = isOwner;
        }
    }

    /**
     * [Request DTO]
     * 리뷰 '작성' 폼 데이터를 컨트롤러로 받을 때 사용할 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        private int rating;
        private String content;
    }

    /**
     * [Request DTO]
     * 리뷰 '수정' 폼 데이터를 컨트롤러로 받을 때 사용할 DTO
     * (삭제/수정 후 원래 페이지로 돌아가기 위해 restaurantId가 필요)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        private int rating;
        private String content;
        private Long restaurantId; // (중요) 리다이렉트를 위한 맛집 ID
    }

    /**
     * [Request DTO]
     * 리뷰 '삭제' 폼 데이터를 컨트롤러로 받을 때 사용할 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeleteRequest {
        private Long restaurantId; // (중요) 리다이렉트를 위한 맛집 ID
    }
}