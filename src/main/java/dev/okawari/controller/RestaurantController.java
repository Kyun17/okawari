package dev.okawari.controller;

import dev.okawari.dto.ReviewDTO;
import dev.okawari.dto.UserDTO;
import dev.okawari.entity.Restaurant;
import dev.okawari.service.RestaurantService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * 메인 페이지 (맛집 목록)
     * @param category URL의 쿼리 파라미터 (?category=...) 값을 받습니다.
     * 값이 없으면 기본값(defaultValue)으로 "전체"를 사용합니다.
     */
    @GetMapping("/")
    public String mainPage(Model model, HttpSession session,
                           @RequestParam(name = "category", required = false, defaultValue = "전체") String category) {

        // 1. 세션에서 로그인한 사용자 정보 가져오기 (기존과 동일)
        UserDTO.SessionUser loggedInUser = (UserDTO.SessionUser) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
        }

        // 2. (수정) 서비스 레이어를 통해 "특정 카테고리"의 맛집 목록 조회
        List<Restaurant> restaurants = restaurantService.findAllRestaurants(category);

        // 3. 각 맛집의 평균 평점(avgRating)을 Map 형태로 계산
        // ("restaurantId" : "avgRating")
        Map<Long, Double> ratingMap = restaurants.stream()
                .collect(Collectors.toMap(
                        Restaurant::getId,
                        r -> {
                            Map<String, Object> summary = restaurantService.reviewsRating(r.getId());
                            return (Double) summary.get("avgRating");
                        }
                ));

        // 4. 모델에 맛집 목록 및 평점 맵 추가
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("ratingMap", ratingMap);

        // 5. (신규) 현재 활성화된 카테고리가 무엇인지 뷰(HTML)에 알려주기
        // (예: "한식" 버튼을 활성화(active) 상태로 만들기 위함)
        model.addAttribute("activeCategory", category);

        // 6. "main.html" 뷰 반환
        return "main";
    }

    /**
     * (신규) 맛집 상세 페이지
     * @param id URL 경로의 {id} 값 (예: /restaurants/1 -> id=1)
     */
    @GetMapping("/restaurants/{id}")
    public String detailPage(@PathVariable Long id, Model model, HttpSession session) {

        // 1. 로그인 정보 가져오기 (헤더 UI 및 리뷰 소유권 확인용)
        UserDTO.SessionUser loggedInUser = (UserDTO.SessionUser) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
        }

        // 2. 맛집 상세 정보 조회
        Restaurant restaurant = restaurantService.findRestaurantById(id);
        model.addAttribute("restaurant", restaurant);

        // 3. 해당 맛집의 리뷰 목록 조회 (로그인 정보 전달)
        List<ReviewDTO.Response> reviews = restaurantService.findReviewsForDisplay(id, loggedInUser);
        model.addAttribute("reviews", reviews);

        // 4. 해당 맛집의 리뷰 갯수 및 평점 전달
        Map<String, Object> summary = restaurantService.reviewsRating(id);
        model.addAttribute("reviewCount", summary.get("reviewCount"));
        model.addAttribute("avgRating", summary.get("avgRating"));

        // 5. (신규) "리뷰 작성" 폼을 위한 빈 DTO 객체 전달
        model.addAttribute("newReviewRequest", new ReviewDTO.CreateRequest());

        // 6. (신규) "리뷰 수정" 팝업을 위한 빈 DTO 객체 전달
        model.addAttribute("updateReviewRequest", new ReviewDTO.UpdateRequest());

        // 7. (신규) "리뷰 삭제" 팝업을 위한 빈 DTO 객체 전달
        model.addAttribute("deleteReviewRequest", new ReviewDTO.DeleteRequest());

        return "detail";
    }

    /**
     * (신규) 맛집 검색 기능입니다.
     * URL의 쿼리 파라미터로 전달된 keyword를 기반으로 restaurant.name에서 검색을 수행합니다.
     * - 검색어가 없을 경우 빈 리스트를 반환합니다.
     * - 검색 결과는 main.html에 표시됩니다.
     */
    @GetMapping("/restaurants/search")
    public String searchRestaurants(@RequestParam("q") String keyword,
                                    Model model,
                                    HttpSession session) {

        // 1. 로그인 정보
        UserDTO.SessionUser loggedInUser = (UserDTO.SessionUser) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
        }

        // 2. 검색 결과 가져오기
        List<Restaurant> restaurants = restaurantService.searchByName(keyword);

        // 3. 각 맛집의 평점/리뷰 수 계산 (ratingMap 생성)
        Map<Long, Double> ratingMap = new HashMap<>();
        for (Restaurant r : restaurants) {
            Map<String, Object> summary = restaurantService.reviewsRating(r.getId());
            ratingMap.put(r.getId(), (Double) summary.get("avgRating"));
        }

        // 4. 모델에 추가
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("ratingMap", ratingMap);
        model.addAttribute("keyword", keyword);

        return "main";
    }

}