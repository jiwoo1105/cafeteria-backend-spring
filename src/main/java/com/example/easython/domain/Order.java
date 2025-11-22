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
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId; // 주문한 사용자 ID
    private String tableId; // 테이블 ID
    private String tableNumber; // 테이블 번호
    private List<OrderItem> items; // 주문 항목들
    private BigDecimal totalPrice; // 총 가격
    private OrderStatus status; // 주문 상태
    private LocalDateTime orderedAt; // 주문 시간
    private LocalDateTime completedAt; // 완료 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String menuId;
        private String menuName;
        private Integer quantity;
        private BigDecimal price;
        private Integer spicinessLevel; // 매움 단계 (0-5)
        private String riceAmount; // 밥 양 (보통, 적게, 많이)
        private List<String> additionalOptions; // 추가 옵션 목록
        private String comment; // 코멘트
    }

    public enum OrderStatus {
        PENDING,    // 대기 중
        IN_CART,    // 장바구니에 담김
        PAYED,      // 결제 완료
        CONFIRMED,  // 확인됨
        PREPARING,  // 준비 중
        READY,      // 준비 완료
        COMPLETED,  // 완료
        CANCELLED   // 취소
    }
}

