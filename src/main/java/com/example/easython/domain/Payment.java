package com.example.easython.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String orderId; // 주문 ID
    private String userId; // 사용자 ID
    private String tableId; // 테이블 ID
    private BigDecimal amount; // 결제 금액
    private PaymentMethod paymentMethod; // 결제 방법
    private PaymentStatus status; // 결제 상태
    private LocalDateTime paidAt; // 결제 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum PaymentMethod {
        CARD,       // 카드
        CASH,       // 현금
        MOBILE      // 모바일 결제
    }

    public enum PaymentStatus {
        PENDING,    // 결제 대기
        COMPLETED,  // 결제 완료
        FAILED,     // 결제 실패
        CANCELLED   // 결제 취소
    }
}

