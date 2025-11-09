package dev.okawari.controller;

import dev.okawari.dto.ReviewDTO;
import dev.okawari.dto.UserDTO;
import dev.okawari.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // (공통) 로그인 확인 메서드
    private UserDTO.SessionUser getSessionUser(HttpSession session) {
        UserDTO.SessionUser user = (UserDTO.SessionUser) session.getAttribute("loggedInUser");
        if (user == null) {
            // 로그인하지 않았으면 예외 발생 (또는 리다이렉트)
            throw new SecurityException("로그인이 필요합니다.");
        }
        return user;
    }

    /**
     * 리뷰 생성 (POST /restaurants/{id}/reviews)
     */
    @PostMapping("/restaurants/{restaurantId}/reviews")
    public String createReview(@PathVariable Long restaurantId,
                               @ModelAttribute("newReviewRequest") ReviewDTO.CreateRequest request,
                               HttpSession session, RedirectAttributes redirectAttributes) {

        try {
            UserDTO.SessionUser user = getSessionUser(session);
            reviewService.createReview(request, restaurantId, user.getUserId());
            redirectAttributes.addFlashAttribute("successMessage", "리뷰가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            // (예외 처리)
            redirectAttributes.addFlashAttribute("errorMessage", "리뷰 등록에 실패했습니다: " + e.getMessage());
        }

        // 성공/실패 모두, 방금 리뷰를 단 그 상세 페이지로 다시 리다이렉트
        return "redirect:/restaurants/" + restaurantId;
    }

    /**
     * 리뷰 수정 (POST /reviews/{reviewId}/edit)
     */
    @PostMapping("/reviews/{reviewId}/edit")
    public String editReview(@PathVariable Long reviewId,
                             @ModelAttribute("updateReviewRequest") ReviewDTO.UpdateRequest request,
                             HttpSession session, RedirectAttributes redirectAttributes) {

        // (중요) 폼에서 전달받은 restaurantId로 리다이렉트
        Long restaurantId = request.getRestaurantId();

        try {
            UserDTO.SessionUser user = getSessionUser(session);
            reviewService.updateReview(request, reviewId, user.getUserId());
            redirectAttributes.addFlashAttribute("successMessage", "리뷰가 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "리뷰 수정에 실패했습니다: " + e.getMessage());
        }

        // 수정 완료 후, 원래 있던 상세 페이지로 돌아가야 함.
        return "redirect:/restaurants/" + restaurantId;
    }

    /**
     * 리뷰 삭제 (POST /reviews/{reviewId}/delete)
     */
    @PostMapping("/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId,
                               @ModelAttribute("deleteReviewRequest") ReviewDTO.DeleteRequest request,
                               HttpSession session, RedirectAttributes redirectAttributes) {

        Long restaurantId = request.getRestaurantId();

        try {
            UserDTO.SessionUser user = getSessionUser(session);
            reviewService.deleteReview(reviewId, user.getUserId());
            redirectAttributes.addFlashAttribute("successMessage", "리뷰가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "리뷰 삭제에 실패했습니다: " + e.getMessage());
        }

        // 삭제 완료 후, 원래 있던 상세 페이지로 리다이렉트
        return "redirect:/restaurants/" + restaurantId;
    }
}