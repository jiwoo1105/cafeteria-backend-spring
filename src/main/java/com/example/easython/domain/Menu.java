package com.example.easython.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "menus")
public class Menu {
    @Id
    private String id;
    private String name; // 메뉴 이름
    private String description; // 메뉴 설명
    private BigDecimal price; // 가격
    private String restaurantName; // 가게 이름 (실제 가게 이름 저장)
    private String imageUrl; // 이미지 URL
    private Boolean isAvailable; // 현재 제공 가능 여부
    private LocalDate availableDate; // 제공 날짜
    private NutritionInfo nutritionInfo; // 영양성분 정보
    private List<String> allergyIngredients; // 알레르기 유발성분 목록
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 영양성분 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionInfo {
        private Integer calories; // 칼로리 (kcal)
        private BigDecimal protein; // 단백질 (g)
        private BigDecimal carbs; // 탄수화물 (g)
        private BigDecimal fat; // 지방 (g)
        private BigDecimal sugar; // 당 (g)
        private Integer sodium; // 나트륨 (mg)
        private BigDecimal fiber; // 섬유질 (g)
        private Integer cholesterol; // 콜레스테롤 (mg)
    }
}

