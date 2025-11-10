package dev.okawari.repository;

import dev.okawari.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Restaurant 엔티티를 Long 타입의 ID로 관리합니다.
// JpaRepository를 상속받는 것만으로도 findAll(), findById() 등 기본 CRUD가 완성됩니다.
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    /**
     * 카테고리 이름으로 맛집 목록을 검색합니다.
     * JPA가 메서드 이름을 분석해 "SELECT * FROM RESTAURANTS WHERE category = ?" 쿼리를 자동 생성합니다.
     * @param category (예: "한식", "중식")
     * @return 해당 카테고리의 맛집 리스트
     */
    List<Restaurant> findByCategory(String category);

    /**
     * 맛집 이름으로 맛집 목록을 검색합니다.
     * JPA가 메서드 이름을 분석해 "SELECT * FROM RESTAURANTS WHERE name = ?" 쿼리를 자동 생성합니다.
     * @param keyword (예: "파", "파스타", "스타") LIKE '%키워드%' 형식으로 검사
     * @return 해당 검색어의 맛집 리스트
     */
    List<Restaurant> findByNameContainingIgnoreCase(String keyword);
}