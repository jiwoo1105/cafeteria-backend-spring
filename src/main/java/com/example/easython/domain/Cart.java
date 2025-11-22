package com.example.easython.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    private String userId; // 사용자 ID
    private String tableId; // 테이블 ID
    private String tableNumber; // 테이블 번호
    private List<CartItem> items; // 장바구니 항목들
    private BigDecimal totalPrice; // 총 가격
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItem {
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

