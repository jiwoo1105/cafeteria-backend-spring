package com.example.easython.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.easython.domain.Menu.NutritionInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String restaurantName; // 가게 이름
    private String imageUrl;
    private Boolean isAvailable;
    private LocalDate availableDate;
    private NutritionInfo nutritionInfo; // 영양성분 정보
    private List<String> allergyIngredients; // 알레르기 유발성분 목록
    private Double averageRating; // 평균 평점
    private Integer orderCount; // 주문 횟수 (사용자별)
}

