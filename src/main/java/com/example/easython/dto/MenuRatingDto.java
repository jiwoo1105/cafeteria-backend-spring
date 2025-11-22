package com.example.easython.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuRatingDto {
    private String id;
    private String menuId;
    private String menuName;
    private String userId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

