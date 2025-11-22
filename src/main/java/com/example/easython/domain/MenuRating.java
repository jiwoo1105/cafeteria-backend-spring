package com.example.easython.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "menu_ratings")
public class MenuRating {
    @Id
    private String id;
    private String menuId; // 메뉴 ID
    private String userId; // 평가한 사용자 ID
    private Integer rating; // 평점 (1-5)
    private String comment; // 코멘트
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

