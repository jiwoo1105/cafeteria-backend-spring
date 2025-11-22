package com.example.easython.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private String id;
    private String userId;
    private String tableId;
    private String tableNumber;
    private List<CartItemDto> items;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDto {
        private String menuId;
        private String menuName;
        private Integer quantity;
        private BigDecimal price;
        private Integer spicinessLevel; // 매움 단계 (0-5)
        private String riceAmount; // 밥 양 (보통, 적게, 많이)
        private List<String> additionalOptions; // 추가 옵션 목록
        private String comment; // 코멘트
    }
}

