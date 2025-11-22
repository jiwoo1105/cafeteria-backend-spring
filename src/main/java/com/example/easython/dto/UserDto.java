package com.example.easython.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.example.easython.domain.User.NutritionGoal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String name;
    private List<String> allergyIngredients; // 알레르기 유발성분 목록
    private NutritionGoal nutritionGoal; // 영양 목표/선호도
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

