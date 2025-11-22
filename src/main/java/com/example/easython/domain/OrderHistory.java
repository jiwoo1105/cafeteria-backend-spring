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
@Document(collection = "order_histories")
public class OrderHistory {
    @Id
    private String id;
    private String userId; // 사용자 ID
    private String menuId; // 메뉴 ID
    private String menuName; // 메뉴 이름
    private Integer orderCount; // 주문 횟수
    private LocalDateTime lastOrderedAt; // 마지막 주문 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

