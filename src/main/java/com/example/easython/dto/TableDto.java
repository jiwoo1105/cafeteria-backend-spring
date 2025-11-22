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
public class TableDto {
    private String id;
    private String tableNumber;
    private Integer capacity;
    private String restaurantName; // 가게 이름
    private Boolean isAvailable; // 주문/결제로 점유 여부
    private String qrCode;
}

