package com.example.easython.domain;

import java.time.LocalDateTime;

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
@Document(collection = "tables")
public class Table {
    @Id
    private String id;
    private String tableNumber; // 테이블 번호
    private Integer capacity; // 수용 인원
    private String restaurantName; // 가게 이름 (실제 가게 이름 저장)
    private Boolean isAvailable; // 사용 가능 여부 (주문/결제로 점유됨)
    private String qrCode; // QR 코드 URL/데이터
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

