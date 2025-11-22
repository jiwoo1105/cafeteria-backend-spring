package com.example.easython.domain;

import java.math.BigDecimal;
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
@Document(collection = "users")
public class User {
    @Id
    private String id; // 사용자 ID (학번 또는 고유 식별자)
    private String name; // 사용자 이름 (선택적)
    private List<String> allergyIngredients; // 알레르기 유발성분 목록
    private NutritionGoal nutritionGoal; // 영양 목표/선호도
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 영양 목표/선호도 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionGoal {
        private Integer dailyCalorieGoal; // 일일 칼로리 목표 (kcal, null이면 제한 없음)
        private BigDecimal minProteinGoal; // 최소 단백질 목표 (g, null이면 제한 없음)
        private BigDecimal maxCalorieGoal; // 최대 칼로리 목표 (kcal, null이면 제한 없음)
        private Boolean isDietMode; // 다이어트 모드 여부
        private Boolean isHighProteinMode; // 고단백 모드 여부
    }
}

